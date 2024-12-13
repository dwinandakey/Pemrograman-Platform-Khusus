package com.polstat.parkir.controller;

import com.polstat.parkir.dto.PendapatanHarianDto;
import com.polstat.parkir.entity.*;
import com.polstat.parkir.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Tag(name = "Search", description = "Endpoints untuk mencari data")
@PreAuthorize("isAuthenticated()")
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private KendaraanRepository kendaraanRepository;

    @Autowired
    private TransaksiParkirRepository transaksiParkirRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkirRepository parkirRepository;

    List<Role> civitasRoles = Arrays.asList(Role.DOSEN, Role.MAHASISWA, Role.KARYAWAN, Role.ADMIN);

    // Search by nomor plat
    @Operation(summary = "Cari kendaraan berdasarkan nomor plat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kendaraan ditemukan"),
            @ApiResponse(responseCode = "404", description = "Kendaraan tidak ditemukan")
    })
    @GetMapping("/kendaraan/plat/{nomorPlat}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Optional<Kendaraan>> searchByNomorPlat(@PathVariable String nomorPlat) {
        return ResponseEntity.ok(kendaraanRepository.findByNomorPlat(nomorPlat));
    }

    // Search by jenis kendaraan
    @Operation(summary = "Cari kendaraan berdasarkan jenis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar kendaraan ditemukan"),
            @ApiResponse(responseCode = "404", description = "Tidak ada kendaraan dengan jenis tersebut")
    })
    @GetMapping("/kendaraan/jenis/{jenisKendaraan}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Kendaraan>> searchByJenisKendaraan(
            @PathVariable Kendaraan.JenisKendaraan jenisKendaraan) {
        return ResponseEntity.ok(kendaraanRepository.findByJenisKendaraan(jenisKendaraan));
    }

    // Transaksi Search Endpoints
    @Operation(summary = "Cari transaksi parkir berdasarkan rentang tanggal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar transaksi ditemukan"),
            @ApiResponse(responseCode = "404", description = "Tidak ada transaksi pada rentang tanggal tersebut")
    })
    @GetMapping("/transaksi/{tanggal_start}/{tanggal_end}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransaksiParkir>> searchTransaksiByDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal_start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal_end) {
        // Konversi ke LocalDateTime untuk digunakan dalam query
        LocalDateTime startDateTime = tanggal_start.atStartOfDay();
        LocalDateTime endDateTime = tanggal_end.plusDays(1).atStartOfDay(); // Include full last day
        return ResponseEntity.ok(transaksiParkirRepository.searchTransaksi(startDateTime, endDateTime));
    }

    @Operation(summary = "Dapatkan pendapatan harian berdasarkan rentang tanggal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pendapatan harian berhasil dihitung"),
            @ApiResponse(responseCode = "400", description = "Input tanggal tidak valid")
    })
    @GetMapping("/transaksi/pendapatan/harian/{tanggal_start}/{tanggal_end}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PendapatanHarianDto> getPendapatanHarian(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal_start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal_end) {
        LocalDateTime startDateTime = tanggal_start.atStartOfDay();
        LocalDateTime endDateTime = tanggal_end.plusDays(1).atStartOfDay();

        // Query langsung ke repository
        List<Object[]> summary = transaksiParkirRepository.getPendapatanSummary(startDateTime, endDateTime);

        // Map hasil ke DTO
        BigDecimal totalPendapatan = BigDecimal.ZERO;
        int totalTransaksi = 0;
        Map<String, Integer> jumlahPerJenisKendaraan = new HashMap<>();

        for (Object[] result : summary) {
            totalPendapatan = totalPendapatan.add((BigDecimal) result[0]);
            totalTransaksi += ((Long) result[1]).intValue();
            Kendaraan.JenisKendaraan jenisKendaraan = (Kendaraan.JenisKendaraan) result[2];
            int jumlah = ((Long) result[3]).intValue();
            jumlahPerJenisKendaraan.put(jenisKendaraan.name(), jumlah);
        }

        PendapatanHarianDto dto = new PendapatanHarianDto(
                tanggal_start, tanggal_end, totalPendapatan, totalTransaksi, jumlahPerJenisKendaraan);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Cari pengguna berdasarkan nama atau email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pengguna ditemukan"),
            @ApiResponse(responseCode = "404", description = "Pengguna tidak ditemukan")
    })
    @GetMapping("/users/{nameoremail}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> searchUsers(
            @PathVariable String nameoremail,
            @RequestParam(required = false) Role role) {
        return ResponseEntity.ok(userRepository.searchUsers(
                nameoremail,
                role,
                nameoremail // Using same search term for both name and email
        ));
    }

    @Operation(summary = "Dapatkan semua civitas akademik")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar civitas akademik ditemukan"),
            @ApiResponse(responseCode = "404", description = "Tidak ada civitas akademik ditemukan")
    })
    @GetMapping("/users/civitas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllCivitasAkademik() {
        return ResponseEntity.ok(userRepository.findAllCivitasAkademik(civitasRoles));
    }

    @Operation(summary = "Dapatkan statistik pengguna berdasarkan peran")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistik pengguna berhasil ditemukan"),
            @ApiResponse(responseCode = "404", description = "Tidak ada data statistik ditemukan")
    })
    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getUserStats() {
        return ResponseEntity.ok(userRepository.getUserCountByRole());
    }

    @Operation(summary = "Cari lokasi parkir berdasarkan nama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lokasi parkir ditemukan"),
            @ApiResponse(responseCode = "404", description = "Lokasi parkir tidak ditemukan")
    })
    @GetMapping("/parkir/{nama}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<List<Parkir>> searchParkirByNama(@PathVariable String nama) {
        return ResponseEntity.ok(parkirRepository.findByNamaLokasiContainingIgnoreCase(nama));
    }

    @Operation(summary = "Dapatkan ringkasan parkir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ringkasan parkir ditemukan"),
            @ApiResponse(responseCode = "404", description = "Ringkasan parkir tidak ditemukan")
    })
    @GetMapping("/parkir/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<Object> getParkirSummary() {
        return ResponseEntity.ok(parkirRepository.getParkirSummary());
    }
}