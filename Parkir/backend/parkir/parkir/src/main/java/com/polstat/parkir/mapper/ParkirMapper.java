package com.polstat.parkir.mapper;

import com.polstat.parkir.dto.ParkirDto;
import com.polstat.parkir.entity.Parkir;
import java.util.List;
import java.util.stream.Collectors;

public class ParkirMapper {
    public static ParkirDto toDto(Parkir parkir) {
        if (parkir == null) {
            return null;
        }
        return ParkirDto.builder()
                .id(parkir.getId())
                .namaLokasi(parkir.getNamaLokasi())
                .kapasitas(parkir.getKapasitas())
                .terisi(parkir.getTerisi())
                .status(parkir.getStatus())
                .build();
    }

    public static Parkir toEntity(ParkirDto dto) {
        if (dto == null) {
            return null;
        }
        return Parkir.builder()
                .id(dto.getId())
                .namaLokasi(dto.getNamaLokasi())
                .kapasitas(dto.getKapasitas())
                .terisi(dto.getTerisi())
                .status(dto.getStatus())
                .build();
    }

    public static List<ParkirDto> toDtoList(List<Parkir> parkirList) {
        if (parkirList == null) {
            return null;
        }
        return parkirList.stream()
                .map(ParkirMapper::toDto)
                .collect(Collectors.toList());
    }
}
