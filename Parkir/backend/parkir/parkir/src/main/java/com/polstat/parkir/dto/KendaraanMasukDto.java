package com.polstat.parkir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KendaraanMasukDto {
    private String nomorPlat;
    private String jenisKendaraan; // MOTOR, MOBIL
    private Long idLokasiParkir;
}

