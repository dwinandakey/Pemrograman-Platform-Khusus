package com.polstat.parkir.dto;

import com.polstat.parkir.entity.Kendaraan;
import lombok.Data;

@Data
public class KendaraanUpdateDto {
    private String nomorPlat;
    private Kendaraan.JenisKendaraan jenisKendaraan;
}
