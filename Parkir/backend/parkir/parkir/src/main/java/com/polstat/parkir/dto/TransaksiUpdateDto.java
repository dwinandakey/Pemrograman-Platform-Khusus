package com.polstat.parkir.dto;

import com.polstat.parkir.entity.TransaksiParkir;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransaksiUpdateDto {
    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private BigDecimal biaya;
    private TransaksiParkir.StatusTransaksi status;
    private Long lokasiParkirId;
    private Long kendaraanId;
    private String userEmail;
}
