package com.polstat.parkir.service;

import com.polstat.parkir.dto.ProfileUpdateDto;
import com.polstat.parkir.dto.UserDto;
import com.polstat.parkir.entity.User;
import com.polstat.parkir.mapper.UserMapper;
import com.polstat.parkir.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public UserDto updateUserProfile(String currentEmail, ProfileUpdateDto updatedProfile) {
        User user = userRepository.findByEmail(currentEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if the new email is already taken by another user
        if (!currentEmail.equals(updatedProfile.getEmail()) &&
                userRepository.findByEmail(updatedProfile.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use by another user");
        }

        // Update only name and email
        user.setName(updatedProfile.getName());
        user.setEmail(updatedProfile.getEmail());

        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void updateUserProfileByAdmin(Long userId, UserDto updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validasi jika email baru sudah digunakan oleh pengguna lain
        if (!user.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.findByEmail(updatedUser.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use by another user");
        }

        // Perbarui profil pengguna
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void changePasswordByAdmin(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Encode password baru dan simpan
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserMapper.mapToUserDto(user);
    }
}