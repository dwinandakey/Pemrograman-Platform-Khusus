package com.polstat.parkir.service;

import com.polstat.parkir.dto.*;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

import com.polstat.parkir.entity.Kendaraan;

public interface ParkirService {
    ParkirDto tambahLokasiParkir(ParkirBaruDto request);

    List<ParkirDto> getDaftarLokasiParkir();

    TransaksiParkirDto prosesKendaraanMasuk(KendaraanMasukDto request);

    TransaksiParkirDto prosesKendaraanKeluar();

    List<KetersediaanParkirDto> getKetersediaanSaatIni();

    Page<TransaksiParkirDto> getDaftarTransaksi(int halaman, int jumlahPerHalaman);

    Optional<Kendaraan> getKendaraanById(Long id);

    void deleteKendaraanById(Long id);

    void deleteTransaksiByKendaraanId(Long kendaraanId);

    Optional<ParkirDto> getLokasiParkirById(Long id);

    Optional<KetersediaanParkirDto> getKetersediaanById(Long id);

    Optional<TransaksiParkirDto> getTransaksiById(Long id);

    List<KendaraanDto> getAllKendaraan();

    ParkirDto updateLokasiParkir(Long id, ParkirBaruDto request);

    void deleteLokasiParkir(Long id);

    TransaksiParkirDto updateTransaksiParkir(Long id, TransaksiUpdateDto request);

    void deleteTransaksiParkir(Long id);

    KendaraanDto updateKendaraan(Long id, KendaraanUpdateDto request);

    void deleteTransaksiByUserId(Long Id);
}
