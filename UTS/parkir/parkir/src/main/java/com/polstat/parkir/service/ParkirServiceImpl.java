package com.polstat.parkir.service;

import com.polstat.parkir.dto.*;
import com.polstat.parkir.entity.*;
import com.polstat.parkir.mapper.*;
import com.polstat.parkir.repository.*;
import com.polstat.parkir.exception.ParkirException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkirServiceImpl implements ParkirService {
    private final ParkirRepository parkirRepository;
    private final KendaraanRepository kendaraanRepository;
    private final TransaksiParkirRepository transaksiParkirRepository;
    private final UserRepository userRepository;

    private static final BigDecimal BIAYA_PARKIR_MOTOR_PERJAM = new BigDecimal("2000");
    private static final BigDecimal BIAYA_PARKIR_MOBIL_PERJAM = new BigDecimal("4000");

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    public List<ParkirDto> getDaftarLokasiParkir() {
        // Menggunakan mapper dengan benar untuk mengonversi entity menjadi DTO
        return ParkirMapper.toDtoList(parkirRepository.findAll());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    @Transactional
    public TransaksiParkirDto prosesKendaraanMasuk(KendaraanMasukDto request) {
        // Dapatkan user dari token
        User user = getCurrentUser();

        // Validasi lokasi parkir
        Parkir lokasi = parkirRepository.findById(request.getIdLokasiParkir())
                .orElseThrow(() -> new ParkirException("Lokasi parkir tidak ditemukan"));

        if (lokasi.getTerisi() >= lokasi.getKapasitas()) {
            throw new ParkirException("Lokasi parkir penuh");
        }

        // Simpan atau ambil data kendaraan
        Kendaraan kendaraan = kendaraanRepository.findByNomorPlat(request.getNomorPlat())
                .orElseGet(() -> {
                    Kendaraan newKendaraan = KendaraanMapper.toEntity(request);
                    return kendaraanRepository.save(newKendaraan);
                });

        // Cek apakah user sudah memiliki transaksi aktif
        Optional<TransaksiParkir> existingTransaction = transaksiParkirRepository
                .findByUserAndStatus(user, TransaksiParkir.StatusTransaksi.AKTIF);

        if (existingTransaction.isPresent()) {
            throw new ParkirException("Anda masih memiliki transaksi parkir yang aktif");
        }

        // Buat transaksi baru
        TransaksiParkir transaksi = new TransaksiParkir();
        transaksi.setKendaraan(kendaraan);
        transaksi.setLokasiParkir(lokasi);
        transaksi.setWaktuMasuk(LocalDateTime.now());
        transaksi.setStatus(TransaksiParkir.StatusTransaksi.AKTIF);
        transaksi.setUser(user);

        // Update jumlah terisi pada lokasi parkir
        lokasi.setTerisi(lokasi.getTerisi() + 1);
        parkirRepository.save(lokasi);

        return TransaksiParkirMapper.toDto(transaksiParkirRepository.save(transaksi));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    @Transactional
    public TransaksiParkirDto prosesKendaraanKeluar() {
        // Dapatkan user dari token
        User user = getCurrentUser();

        // Cari transaksi aktif dari user
        TransaksiParkir transaksi = transaksiParkirRepository
                .findByUserAndStatus(user, TransaksiParkir.StatusTransaksi.AKTIF)
                .orElseThrow(() -> new ParkirException("Tidak ada transaksi parkir aktif"));

        LocalDateTime waktuKeluar = LocalDateTime.now();
        transaksi.setWaktuKeluar(waktuKeluar);

        // Cek apakah user adalah civitas akademik
        if (user.isCivitasAkademik()) {
            transaksi.setBiaya(BigDecimal.ZERO);
        } else {
            // Hitung durasi parkir dan biaya
            long durasiMenit = ChronoUnit.MINUTES.between(transaksi.getWaktuMasuk(), waktuKeluar);
            long durasiJam = Math.max((durasiMenit + 59) / 60, 1);

            BigDecimal tarifPerJam = transaksi.getKendaraan().getJenisKendaraan() == Kendaraan.JenisKendaraan.MOTOR ?
                    BIAYA_PARKIR_MOTOR_PERJAM : BIAYA_PARKIR_MOBIL_PERJAM;

            transaksi.setBiaya(tarifPerJam.multiply(BigDecimal.valueOf(durasiJam)));
        }

        transaksi.setStatus(TransaksiParkir.StatusTransaksi.SELESAI);

        // Update kapasitas parkir
        Parkir lokasi = transaksi.getLokasiParkir();
        lokasi.setTerisi(lokasi.getTerisi() - 1);
        parkirRepository.save(lokasi);

        return TransaksiParkirMapper.toDto(transaksiParkirRepository.save(transaksi));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<TransaksiParkirDto> getDaftarTransaksi(int halaman, int jumlahPerHalaman) {
        // Panggil repository untuk mendapatkan transaksi dalam bentuk halaman
        return transaksiParkirRepository
                .findAll(PageRequest.of(halaman, jumlahPerHalaman))
                .map(TransaksiParkirMapper::toDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    public List<KetersediaanParkirDto> getKetersediaanSaatIni() {
        List<TransaksiParkir> transaksiAktif = transaksiParkirRepository
                .findByStatus(TransaksiParkir.StatusTransaksi.AKTIF);

        // Ambil semua lokasi parkir
        List<Parkir> lokasiParkir = parkirRepository.findAll();

        // Buat DTO untuk setiap lokasi
        return lokasiParkir.stream().map(lokasi -> {
            // Hitung statistik khusus untuk lokasi ini
            Map<String, Integer> statistikLokasi = transaksiAktif.stream()
                    .filter(t -> t.getLokasiParkir().getId().equals(lokasi.getId()))
                    .collect(Collectors.groupingBy(
                            t -> t.getKendaraan().getJenisKendaraan().toString(),
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ));

            KetersediaanParkirDto dto = new KetersediaanParkirDto();
            dto.setIdLokasi(lokasi.getId());
            dto.setNamaLokasi(lokasi.getNamaLokasi());
            dto.setKapasitasTotal(lokasi.getKapasitas());
            dto.setJumlahTerisi(lokasi.getTerisi());
            dto.setSisaKapasitas(lokasi.getKapasitas() - lokasi.getTerisi());
            dto.setStatistikJenisKendaraan(statistikLokasi);

            return dto;
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public ParkirDto tambahLokasiParkir(ParkirBaruDto request) {
        // Buat objek lokasi parkir baru berdasarkan DTO
        Parkir lokasiBaru = new Parkir();
        lokasiBaru.setNamaLokasi(request.getNamaLokasi());
        lokasiBaru.setKapasitas(request.getKapasitas());
        lokasiBaru.setTerisi(0); // Mulai dengan kapasitas terisi 0

        // Simpan lokasi parkir baru ke database
        Parkir lokasiDisimpan = parkirRepository.save(lokasiBaru);

        // Map entity yang telah disimpan ke DTO untuk response
        return ParkirMapper.toDto(lokasiDisimpan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Optional<Kendaraan> getKendaraanById(Long id) {
        // Mengambil kendaraan berdasarkan ID
        return kendaraanRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteKendaraanById(Long id) {
        Kendaraan kendaraan = kendaraanRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Kendaraan dengan ID " + id + " tidak ditemukan"));

        try {
            // Delete the vehicle
            kendaraanRepository.delete(kendaraan);
        } catch (Exception e) {
            // Catch foreign key constraint violation
            List<TransaksiParkir> transaksiTerkait = transaksiParkirRepository.findByKendaraan(kendaraan);
            throw new ParkirException("Tidak dapat menghapus kendaraan karena masih memiliki " +
                    transaksiTerkait.size() + " transaksi terkait. Silahkan hapus transaksi terlebih dahulu dengan endpoint: " +
                    "DELETE /parkir/transaksi/kendaraan/" + id);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteTransaksiByKendaraanId(Long kendaraanId) {
        Kendaraan kendaraan = kendaraanRepository.findById(kendaraanId)
                .orElseThrow(() -> new ParkirException("Kendaraan dengan ID " + kendaraanId + " tidak ditemukan"));

        List<TransaksiParkir> transaksiList = transaksiParkirRepository.findByKendaraan(kendaraan);

        if (transaksiList.isEmpty()) {
            throw new ParkirException("Tidak ada transaksi untuk kendaraan dengan ID " + kendaraanId);
        }

        // Check for active transactions
        boolean hasActiveTransactions = transaksiList.stream()
                .anyMatch(t -> t.getStatus() == TransaksiParkir.StatusTransaksi.AKTIF);

        if (hasActiveTransactions) {
            throw new ParkirException("Tidak dapat menghapus transaksi karena kendaraan masih memiliki transaksi aktif");
        }

        // Delete all transactions for this vehicle
        transaksiParkirRepository.deleteAll(transaksiList);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    public Optional<ParkirDto> getLokasiParkirById(Long id) {
        return parkirRepository.findById(id)
                .map(ParkirMapper::toDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOSEN', 'MAHASISWA', 'KARYAWAN', 'UMUM')")
    @Override
    public Optional<KetersediaanParkirDto> getKetersediaanById(Long id) {
        return parkirRepository.findById(id)
                .map(lokasi -> {
                    List<TransaksiParkir> transaksiAktif = transaksiParkirRepository
                            .findByLokasiParkirAndStatus(lokasi, TransaksiParkir.StatusTransaksi.AKTIF);

                    Map<String, Integer> statistikLokasi = transaksiAktif.stream()
                            .collect(Collectors.groupingBy(
                                    t -> t.getKendaraan().getJenisKendaraan().toString(),
                                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                            ));

                    KetersediaanParkirDto dto = new KetersediaanParkirDto();
                    dto.setIdLokasi(lokasi.getId());
                    dto.setNamaLokasi(lokasi.getNamaLokasi());
                    dto.setKapasitasTotal(lokasi.getKapasitas());
                    dto.setJumlahTerisi(lokasi.getTerisi());
                    dto.setSisaKapasitas(lokasi.getKapasitas() - lokasi.getTerisi());
                    dto.setStatistikJenisKendaraan(statistikLokasi);

                    return dto;
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Optional<TransaksiParkirDto> getTransaksiById(Long id) {
        return transaksiParkirRepository.findById(id)
                .map(TransaksiParkirMapper::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<KendaraanDto> getAllKendaraan() {
        return kendaraanRepository.findAll().stream()
                .map(kendaraan -> {
                    KendaraanDto dto = new KendaraanDto();
                    dto.setId(kendaraan.getId());
                    dto.setNomorPlat(kendaraan.getNomorPlat());
                    dto.setJenisKendaraan(kendaraan.getJenisKendaraan());

                    // Tambahkan informasi transaksi terakhir
                    transaksiParkirRepository.findFirstByKendaraanOrderByWaktuMasukDesc(kendaraan)
                            .ifPresent(lastTransaction -> {
                                dto.setStatusTerakhir(lastTransaction.getStatus());
                                dto.setWaktuMasukTerakhir(lastTransaction.getWaktuMasuk());
                                dto.setWaktuKeluarTerakhir(lastTransaction.getWaktuKeluar());
                                dto.setLokasiTerakhir(lastTransaction.getLokasiParkir().getNamaLokasi());
                            });

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public ParkirDto updateLokasiParkir(Long id, ParkirBaruDto request) {
        Parkir lokasi = parkirRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Lokasi parkir tidak ditemukan"));

        // Validate that new capacity is not less than current occupied spaces
        if (request.getKapasitas() < lokasi.getTerisi()) {
            throw new ParkirException("Kapasitas baru tidak boleh lebih kecil dari jumlah kendaraan yang sedang parkir");
        }

        lokasi.setNamaLokasi(request.getNamaLokasi());
        lokasi.setKapasitas(request.getKapasitas());

        return ParkirMapper.toDto(parkirRepository.save(lokasi));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteLokasiParkir(Long id) {
        Parkir lokasi = parkirRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Lokasi parkir tidak ditemukan"));

        // Check if there are any active transactions
        boolean hasActiveTransactions = transaksiParkirRepository
                .existsByLokasiParkirAndStatus(lokasi, TransaksiParkir.StatusTransaksi.AKTIF);

        if (hasActiveTransactions) {
            throw new ParkirException("Tidak dapat menghapus lokasi parkir karena masih ada kendaraan yang parkir");
        }

        parkirRepository.delete(lokasi);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public TransaksiParkirDto updateTransaksiParkir(Long id, TransaksiUpdateDto request) {
        TransaksiParkir transaksi = transaksiParkirRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Transaksi parkir tidak ditemukan"));

        // Update related entities if IDs are provided
        if (request.getLokasiParkirId() != null) {
            Parkir lokasiBaru = parkirRepository.findById(request.getLokasiParkirId())
                    .orElseThrow(() -> new ParkirException("Lokasi parkir tidak ditemukan"));
            transaksi.setLokasiParkir(lokasiBaru);
        }

        if (request.getKendaraanId() != null) {
            Kendaraan kendaraanBaru = kendaraanRepository.findById(request.getKendaraanId())
                    .orElseThrow(() -> new ParkirException("Kendaraan tidak ditemukan"));
            transaksi.setKendaraan(kendaraanBaru);
        }

        if (request.getUserEmail() != null) {
            User userBaru = userRepository.findByEmail(request.getUserEmail());
            if (userBaru == null) {
                throw new ParkirException("User tidak ditemukan");
            }
            transaksi.setUser(userBaru);
        }

        // Update basic fields
        if (request.getWaktuMasuk() != null) transaksi.setWaktuMasuk(request.getWaktuMasuk());
        if (request.getWaktuKeluar() != null) transaksi.setWaktuKeluar(request.getWaktuKeluar());
        if (request.getBiaya() != null) transaksi.setBiaya(request.getBiaya());
        if (request.getStatus() != null) transaksi.setStatus(request.getStatus());

        return TransaksiParkirMapper.toDto(transaksiParkirRepository.save(transaksi));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteTransaksiParkir(Long id) {
        TransaksiParkir transaksi = transaksiParkirRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Transaksi parkir tidak ditemukan"));

        if (transaksi.getStatus() == TransaksiParkir.StatusTransaksi.AKTIF) {
            throw new ParkirException("Tidak dapat menghapus transaksi yang masih aktif");
        }

        transaksiParkirRepository.delete(transaksi);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public KendaraanDto updateKendaraan(Long id, KendaraanUpdateDto request) {
        Kendaraan kendaraan = kendaraanRepository.findById(id)
                .orElseThrow(() -> new ParkirException("Kendaraan tidak ditemukan"));

        // Check if new plate number is already used by another vehicle
        if (!kendaraan.getNomorPlat().equals(request.getNomorPlat()) &&
                kendaraanRepository.existsByNomorPlat(request.getNomorPlat())) {
            throw new ParkirException("Nomor plat sudah digunakan");
        }

        kendaraan.setNomorPlat(request.getNomorPlat());
        kendaraan.setJenisKendaraan(request.getJenisKendaraan());

        Kendaraan updatedKendaraan = kendaraanRepository.save(kendaraan);

        // Convert to DTO
        KendaraanDto dto = new KendaraanDto();
        dto.setId(updatedKendaraan.getId());
        dto.setNomorPlat(updatedKendaraan.getNomorPlat());
        dto.setJenisKendaraan(updatedKendaraan.getJenisKendaraan());

        // Add last transaction information
        transaksiParkirRepository.findFirstByKendaraanOrderByWaktuMasukDesc(updatedKendaraan)
                .ifPresent(lastTransaction -> {
                    dto.setStatusTerakhir(lastTransaction.getStatus());
                    dto.setWaktuMasukTerakhir(lastTransaction.getWaktuMasuk());
                    dto.setWaktuKeluarTerakhir(lastTransaction.getWaktuKeluar());
                    dto.setLokasiTerakhir(lastTransaction.getLokasiParkir().getNamaLokasi());
                });

        return dto;
    }

    public void deleteTransaksiByUserId(Long Id) throws ParkirException {
        List<TransaksiParkir> transaksiList = transaksiParkirRepository.findByUser_Id(Id);
        if (transaksiList.isEmpty()) {
            throw new ParkirException("Tidak ada transaksi terkait dengan profile ID " + Id);
        }
        transaksiParkirRepository.deleteAll(transaksiList);
    }
}
