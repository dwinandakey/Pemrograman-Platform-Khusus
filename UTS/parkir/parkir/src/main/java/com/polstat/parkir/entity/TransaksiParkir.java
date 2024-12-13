package com.polstat.parkir.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transaksiparkir")
public class TransaksiParkir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_kendaraan", nullable = false)
    private Kendaraan kendaraan;

    @ManyToOne
    @JoinColumn(name = "id_lokasi", nullable = false)
    private Parkir lokasiParkir;

    @Column(nullable = false)
    private LocalDateTime waktuMasuk;

    private LocalDateTime waktuKeluar;

    private BigDecimal biaya;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransaksi status = StatusTransaksi.AKTIF;

    public enum StatusTransaksi {
        AKTIF, SELESAI
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
