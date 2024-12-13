package com.polstat.parkir.repository;

import com.polstat.parkir.entity.Parkir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "parkir", path = "parkir")
public interface ParkirRepository extends JpaRepository<Parkir, Long> {

    // Mencari lokasi parkir berdasarkan nama (contains/like)
    List<Parkir> findByNamaLokasiContainingIgnoreCase(String namaLokasi);

    // Custom query untuk mendapatkan ringkasan ketersediaan
    @Query("SELECT new map(" +
            "COUNT(p) as totalLokasi, " +
            "SUM(p.kapasitas) as totalKapasitas, " +
            "SUM(p.terisi) as totalTerisi, " +
            "SUM(p.kapasitas - p.terisi) as totalTersedia) " +
            "FROM Parkir p WHERE p.status = true")
    Object getParkirSummary();
}