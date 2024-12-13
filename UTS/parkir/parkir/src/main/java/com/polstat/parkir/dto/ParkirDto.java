package com.polstat.parkir.dto;

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
public class ParkirDto {
    private Long id;
    private String namaLokasi;
    private Integer kapasitas;
    private Integer terisi;
    private Boolean status; // true = aktif, false = nonaktif
}
