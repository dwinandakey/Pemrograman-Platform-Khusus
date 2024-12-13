package com.polstat.parkir.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
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
public class TransaksiParkirDto {
    private Long id;
    private String nomorPlat;
    private String jenisKendaraan;
    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private BigDecimal biaya;
    private String status; // AKTIF, SELESAI
    private String lokasiParkir;
}
