package com.polstat.parkir.mapper;

import com.polstat.parkir.dto.KendaraanMasukDto;
import com.polstat.parkir.entity.Kendaraan;

import java.util.List;
import java.util.stream.Collectors;

public class KendaraanMapper {

    // Mengubah dari KendaraanMasukDto ke Kendaraan entity
    public static Kendaraan toEntity(KendaraanMasukDto dto) {
        return Kendaraan.builder()
                .nomorPlat(dto.getNomorPlat())
                .jenisKendaraan(Kendaraan.JenisKendaraan.valueOf(dto.getJenisKendaraan().toUpperCase()))
                .build();
    }

    // Mengubah dari Kendaraan entity ke format respons (map langsung ke data sederhana)
    public static KendaraanMasukDto toResponse(Kendaraan kendaraan) {
        return KendaraanMasukDto.builder()
                .nomorPlat(kendaraan.getNomorPlat())
                .jenisKendaraan(kendaraan.getJenisKendaraan().name())
                .idLokasiParkir(null) // Lokasi parkir tidak diambil langsung dari entitas ini
                .build();
    }

    // Mengonversi daftar Kendaraan entity ke daftar DTO KendaraanMasukDto
    public static List<KendaraanMasukDto> toResponseList(List<Kendaraan> kendaraanList) {
        return kendaraanList.stream()
                .map(KendaraanMapper::toResponse)
                .collect(Collectors.toList());
    }
}
