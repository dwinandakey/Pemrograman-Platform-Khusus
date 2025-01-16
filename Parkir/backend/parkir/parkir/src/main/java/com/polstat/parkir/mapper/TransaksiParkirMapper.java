package com.polstat.parkir.mapper;

import com.polstat.parkir.dto.TransaksiParkirDto;
import com.polstat.parkir.entity.TransaksiParkir;
import java.util.List;
import java.util.stream.Collectors;

public class TransaksiParkirMapper {
    public static TransaksiParkirDto toDto(TransaksiParkir transaksi) {
        if (transaksi == null) {
            return null;
        }
        return TransaksiParkirDto.builder()
                .id(transaksi.getId())
                .nomorPlat(transaksi.getKendaraan().getNomorPlat())
                .jenisKendaraan(transaksi.getKendaraan().getJenisKendaraan().name())
                .waktuMasuk(transaksi.getWaktuMasuk())
                .waktuKeluar(transaksi.getWaktuKeluar())
                .biaya(transaksi.getBiaya())
                .status(transaksi.getStatus().name())
                .lokasiParkir(transaksi.getLokasiParkir().getNamaLokasi())
                .build();
    }

    public static List<TransaksiParkirDto> toDtoList(List<TransaksiParkir> transaksiList) {
        if (transaksiList == null) {
            return null;
        }
        return transaksiList.stream()
                .map(TransaksiParkirMapper::toDto)
                .collect(Collectors.toList());
    }
}
