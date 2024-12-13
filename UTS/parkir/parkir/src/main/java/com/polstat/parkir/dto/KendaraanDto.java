package com.polstat.parkir.dto;

import com.polstat.parkir.entity.Kendaraan;
import com.polstat.parkir.entity.TransaksiParkir;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KendaraanDto {
    private Long id;
    private String nomorPlat;
    private Kendaraan.JenisKendaraan jenisKendaraan;
    private TransaksiParkir.StatusTransaksi statusTerakhir;
    private LocalDateTime waktuMasukTerakhir;
    private LocalDateTime waktuKeluarTerakhir;
    private String lokasiTerakhir;
}
