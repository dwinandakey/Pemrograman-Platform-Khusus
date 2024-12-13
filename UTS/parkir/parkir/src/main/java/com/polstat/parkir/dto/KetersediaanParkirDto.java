package com.polstat.parkir.dto;

import lombok.Data;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KetersediaanParkirDto {
    private Long idLokasi;
    private String namaLokasi;
    private Integer kapasitasTotal;
    private Integer jumlahTerisi;
    private Integer sisaKapasitas;
    private Map<String, Integer> statistikJenisKendaraan;
}

