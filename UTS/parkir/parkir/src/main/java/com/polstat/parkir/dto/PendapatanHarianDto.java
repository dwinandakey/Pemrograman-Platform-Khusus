package com.polstat.parkir.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendapatanHarianDto {
    private LocalDate tanggalStart;
    private LocalDate tanggalEnd;
    private BigDecimal totalPendapatan;
    private Integer jumlahTransaksi;
    private Map<String, Integer> jumlahPerJenisKendaraan;
}
