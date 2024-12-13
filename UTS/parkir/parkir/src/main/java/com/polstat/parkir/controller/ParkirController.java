package com.polstat.parkir.controller;

import com.polstat.parkir.dto.*;
import com.polstat.parkir.entity.Kendaraan;
import com.polstat.parkir.entity.TransaksiParkir;
import com.polstat.parkir.exception.ParkirException;
import com.polstat.parkir.repository.TransaksiParkirRepository;
import com.polstat.parkir.service.ParkirService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Tag(name = "Parkir Controller", description = "Manajemen parkir dan transaksi terkait")
@PreAuthorize("isAuthenticated()")
@RequestMapping("/parkir")
@RequiredArgsConstructor
public class ParkirController {
    private final ParkirService parkirService;
    private final TransaksiParkirRepository transaksiParkirRepository;

    @Operation(summary = "Tambah lokasi parkir baru")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lokasi parkir berhasil ditambahkan"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid")
    })
    @PostMapping("/lokasi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkirDto> tambahLokasiParkir(@RequestBody ParkirBaruDto request) {
        return ResponseEntity.ok(parkirService.tambahLokasiParkir(request));
    }

    @Operation(summary = "Dapatkan daftar lokasi parkir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar lokasi parkir berhasil diambil")
    })
    @GetMapping("/lokasi")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<List<ParkirDto>> getDaftarLokasiParkir() {
        return ResponseEntity.ok(parkirService.getDaftarLokasiParkir());
    }

    @Operation(summary = "Dapatkan lokasi parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lokasi parkir ditemukan"),
            @ApiResponse(responseCode = "404", description = "Lokasi parkir tidak ditemukan")
    })
    @GetMapping("/lokasi/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<ParkirDto> getLokasiParkirById(@PathVariable Long id) {
        return parkirService.getLokasiParkirById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Proses kendaraan masuk")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kendaraan berhasil diproses masuk"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid")
    })
    @PostMapping("/masuk")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<TransaksiParkirDto> prosesKendaraanMasuk(@RequestBody KendaraanMasukDto request) {
        return ResponseEntity.ok(parkirService.prosesKendaraanMasuk(request));
    }

    @Operation(summary = "Proses kendaraan keluar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kendaraan berhasil diproses keluar"),
            @ApiResponse(responseCode = "400", description = "Data tidak valid")
    })
    @PostMapping("/keluar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<TransaksiParkirDto> prosesKendaraanKeluar() {
        return ResponseEntity.ok(parkirService.prosesKendaraanKeluar());
    }

    @Operation(summary = "Cek ketersediaan parkir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ketersediaan parkir berhasil diambil")
    })
    @GetMapping("/ketersediaan")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<List<KetersediaanParkirDto>> cekKetersediaan() {
        return ResponseEntity.ok(parkirService.getKetersediaanSaatIni());
    }

    @Operation(summary = "Dapatkan ketersediaan parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ketersediaan ditemukan"),
            @ApiResponse(responseCode = "404", description = "Ketersediaan tidak ditemukan")
    })
    @GetMapping("/ketersediaan/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    public ResponseEntity<KetersediaanParkirDto> getKetersediaanById(@PathVariable Long id) {
        return parkirService.getKetersediaanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Dapatkan daftar transaksi parkir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar transaksi berhasil diambil")
    })
    @GetMapping("/transaksi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransaksiParkirDto>> getDaftarTransaksi(
            @RequestParam(defaultValue = "0") int halaman,
            @RequestParam(defaultValue = "10") int jumlahPerHalaman) {
        return ResponseEntity.ok(parkirService.getDaftarTransaksi(halaman, jumlahPerHalaman));
    }

    @Operation(summary = "Dapatkan kendaraan berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kendaraan ditemukan"),
            @ApiResponse(responseCode = "404", description = "Kendaraan tidak ditemukan")
    })
    @GetMapping("/kendaraan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Kendaraan> getKendaraanById(@PathVariable Long id) {
        return parkirService.getKendaraanById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Hapus semua transaksi berdasarkan ID kendaraan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaksi terkait kendaraan berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus transaksi")
    })
    @DeleteMapping("/transaksi/kendaraan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTransaksiByKendaraanId(@PathVariable Long id) {
        try {
            parkirService.deleteTransaksiByKendaraanId(id);
            return ResponseEntity.ok()
                    .body("Semua transaksi untuk kendaraan ID " + id + " berhasil dihapus. " +
                            "Sekarang Anda dapat menghapus kendaraan dengan endpoint DELETE /parkir/delete-kendaraan/" + id);
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Hapus kendaraan berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kendaraan berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus kendaraan karena terkait dengan transaksi")
    })
    @DeleteMapping("/kendaraan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteKendaraanById(@PathVariable Long id) {
        try {
            parkirService.deleteKendaraanById(id);
            return ResponseEntity.ok().body("Kendaraan berhasil dihapus");
        } catch (DataIntegrityViolationException e) {
            // Tangkap exception foreign key constraint
            try {
                // Coba dapatkan transaksi terkait
                List<TransaksiParkir> transaksiTerkait = transaksiParkirRepository.findByKendaraan_Id(id);
                return ResponseEntity.badRequest().body(
                        "Tidak dapat menghapus kendaraan karena masih memiliki " +
                                transaksiTerkait.size() +
                                " transaksi terkait. Silahkan hapus transaksi terlebih dahulu dengan endpoint: " +
                                "DELETE /parkir/transaksi/kendaraan/" + id
                );
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body("Tidak dapat menghapus kendaraan karena masih memiliki transaksi terkait");
            }
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Dapatkan transaksi berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaksi ditemukan"),
            @ApiResponse(responseCode = "404", description = "Transaksi tidak ditemukan")
    })
    @GetMapping("/transaksi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransaksiParkirDto> getTransaksiById(@PathVariable Long id) {
        return parkirService.getTransaksiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Dapatkan semua kendaraan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar kendaraan berhasil diambil")
    })
    @GetMapping("/kendaraan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KendaraanDto>> getAllKendaraan() {
        List<KendaraanDto> kendaraanList = parkirService.getAllKendaraan();
        return ResponseEntity.ok(kendaraanList);
    }

    @Operation(summary = "Perbarui lokasi parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lokasi parkir berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Gagal memperbarui lokasi parkir")
    })
    @PutMapping("/lokasi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLokasiParkir(@PathVariable Long id, @RequestBody ParkirBaruDto request) {
        try {
            ParkirDto updatedLokasi = parkirService.updateLokasiParkir(id, request);
            return ResponseEntity.ok(updatedLokasi);
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Hapus lokasi parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lokasi parkir berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus lokasi parkir")
    })
    @DeleteMapping("/lokasi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLokasiParkir(@PathVariable Long id) {
        try {
            parkirService.deleteLokasiParkir(id);
            return ResponseEntity.ok().body("Lokasi parkir berhasil dihapus");
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Perbarui transaksi parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaksi parkir berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Gagal memperbarui transaksi parkir")
    })
    @PutMapping("/transaksi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTransaksiParkir(@PathVariable Long id, @RequestBody TransaksiUpdateDto request) {
        try {
            TransaksiParkirDto updatedTransaksi = parkirService.updateTransaksiParkir(id, request);
            return ResponseEntity.ok(updatedTransaksi);
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Hapus transaksi parkir berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaksi parkir berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus transaksi parkir")
    })
    @DeleteMapping("/transaksi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTransaksiParkir(@PathVariable Long id) {
        try {
            parkirService.deleteTransaksiParkir(id);
            return ResponseEntity.ok().body("Transaksi parkir berhasil dihapus");
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Perbarui data kendaraan berdasarkan ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data kendaraan berhasil diperbarui"),
            @ApiResponse(responseCode = "400", description = "Gagal memperbarui data kendaraan")
    })    @PutMapping("/kendaraan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateKendaraan(@PathVariable Long id, @RequestBody KendaraanUpdateDto request) {
        try {
            KendaraanDto updatedKendaraan = parkirService.updateKendaraan(id, request);
            return ResponseEntity.ok(updatedKendaraan);
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Hapus semua transaksi berdasarkan ID profil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Semua transaksi terkait profil berhasil dihapus"),
            @ApiResponse(responseCode = "400", description = "Gagal menghapus transaksi")
    })
    @DeleteMapping("/transaksi/profile/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTransaksiByProfileId(@PathVariable Long id) {
        try {
            parkirService.deleteTransaksiByUserId(id);
            return ResponseEntity.ok().body("Semua transaksi untuk profile ID " + id + " berhasil dihapus. " +
                    "Sekarang Anda dapat menghapus profile dengan endpoint DELETE /profile/" + id);
        } catch (ParkirException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}