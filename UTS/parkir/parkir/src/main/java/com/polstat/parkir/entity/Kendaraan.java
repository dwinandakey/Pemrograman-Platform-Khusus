package com.polstat.parkir.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "kendaraan")
public class Kendaraan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomorPlat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JenisKendaraan jenisKendaraan;

    public enum JenisKendaraan {
        MOTOR, MOBIL
    }
}

