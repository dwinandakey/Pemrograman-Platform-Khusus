package com.polstat.parkir.auth;

import com.polstat.parkir.dto.PasswordChangeDto;
import com.polstat.parkir.dto.ProfileUpdateDto;
import com.polstat.parkir.dto.UserDto;
import com.polstat.parkir.entity.Role;
import com.polstat.parkir.exception.ParkirException;
import com.polstat.parkir.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "Authentication", description = "Authentication dan manajemen user APIs")
public class AuthController {

  @Autowired
  AuthenticationManager authManager;

  @Autowired
  JwtUtil jwtUtil;

  @Autowired
  UserService userService;

  @Operation(summary = "Login user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login berhasil"),
          @ApiResponse(responseCode = "401", description = "Kredensial salah")
  })
  @PreAuthorize("permitAll()")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
    try {
      Authentication authentication = authManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
      String accessToken = jwtUtil.generateAccessToken(authentication);
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      UserDto userDto = userService.getUserByEmail(userDetails.getUsername());
      AuthResponse response = new AuthResponse(request.getEmail(), accessToken);
      return ResponseEntity.ok().body(response);
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

    @Operation(summary = "Register user baru")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrasi berhasil"),
            @ApiResponse(responseCode = "400", description = "Input data tidak valid")
    })
  @PreAuthorize("permitAll()")
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid UserDto request) {
    if (request.getRole() == null) {
      request.setRole(Role.UMUM);
    }
    UserDto user = userService.createUser(request);
    return ResponseEntity.ok().body(user);
  }

    @Operation(summary = "Dapatkan profil pengguna yang sedang login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil ditemukan"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update profil pengguna")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid ProfileUpdateDto profileUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserDto updatedUser = userService.updateUserProfile(userDetails.getUsername(), profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Ubah kata sandi pengguna yang sedang login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kata sandi berhasil diubah"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        try {
            userService.changePassword(
                    userDetails.getUsername(),
                    passwordChangeDto.getCurrentPassword(),
                    passwordChangeDto.getNewPassword()
            );
            return ResponseEntity.ok().body("Password successfully changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Hapus akun pengguna")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Akun berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Tidak dapat menghapus akun terkait transaksi"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDto currentUser, @PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().body("Akun berhasil dihapus");
        } catch (DataIntegrityViolationException e) {
            // Tangkap exception foreign key constraint
            try {
                return ResponseEntity.badRequest().body(
                        "Tidak dapat menghapus profile karena masih memiliki transaksi terkait. " +
                        "Silahkan hapus transaksi terlebih dahulu dengan endpoint: DELETE /parkir/transaksi/profile/" + userId
                );
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body("Tidak dapat menghapus profile karena masih memiliki transaksi terkait");
            }
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update profil pengguna oleh admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil berhasil diperbarui oleh admin"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfileByAdmin(
            @PathVariable Long userId,
            @RequestBody @Valid UserDto updatedUser
    ) {
        userService.updateUserProfileByAdmin(userId, updatedUser);
        return ResponseEntity.ok().body("User profile successfully updated by admin");
    }

    @Operation(summary = "Ubah kata sandi pengguna oleh admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kata sandi berhasil diubah oleh admin"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changeUserPasswordByAdmin(
            @PathVariable Long userId,
            @RequestBody @Valid PasswordChangeDto passwordChangeRequest
    ) {
        userService.changePasswordByAdmin(userId, passwordChangeRequest.getNewPassword());
        return ResponseEntity.ok().body("User password successfully changed by admin");
    }

    @Operation(summary = "Dapatkan profil pengguna berdasarkan ID oleh admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil pengguna ditemukan"),
            @ApiResponse(responseCode = "404", description = "Profil pengguna tidak ditemukan"),
            @ApiResponse(responseCode = "403", description = "Akses tidak diizinkan")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok().body(user);
    }
}