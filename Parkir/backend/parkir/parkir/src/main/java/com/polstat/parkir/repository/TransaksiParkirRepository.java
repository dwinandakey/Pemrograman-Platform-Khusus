package com.polstat.parkir.repository;

import com.polstat.parkir.entity.Kendaraan;
import com.polstat.parkir.entity.Parkir;
import com.polstat.parkir.entity.TransaksiParkir;
import com.polstat.parkir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "transaksiparkir", path = "transaksiparkir")
public interface TransaksiParkirRepository extends JpaRepository<TransaksiParkir, Long> {

    List<TransaksiParkir> findByStatus(TransaksiParkir.StatusTransaksi status);
    Optional<TransaksiParkir> findByUserAndStatus(User user, TransaksiParkir.StatusTransaksi statusTransaksi);
    List<TransaksiParkir> findByKendaraan(Kendaraan kendaraan);
    List<TransaksiParkir> findByKendaraan_Id(Long id);
    List<TransaksiParkir> findByLokasiParkirAndStatus(Parkir lokasi, TransaksiParkir.StatusTransaksi statusTransaksi);
    Optional<TransaksiParkir> findFirstByKendaraanOrderByWaktuMasukDesc(Kendaraan kendaraan);
    boolean existsByLokasiParkirAndStatus(Parkir lokasi, TransaksiParkir.StatusTransaksi statusTransaksi);

    @Query("SELECT t FROM TransaksiParkir t WHERE " +
            "(:startDate IS NULL OR t.waktuMasuk >= :startDate) AND " +
            "(:endDate IS NULL OR t.waktuMasuk <= :endDate)")
    List<TransaksiParkir> searchTransaksi(
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end);

    @Query("SELECT " +
            "SUM(t.biaya) AS totalPendapatan, " +
            "COUNT(t) AS jumlahTransaksi, " +
            "t.kendaraan.jenisKendaraan AS jenisKendaraan, " +
            "COUNT(t.kendaraan.jenisKendaraan) AS jumlahKendaraan " +
            "FROM TransaksiParkir t " +
            "WHERE t.waktuMasuk >= :start AND t.waktuMasuk < :end " +
            "GROUP BY t.kendaraan.jenisKendaraan")
    List<Object[]> getPendapatanSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<TransaksiParkir> findByUser_Id(Long Id);
}