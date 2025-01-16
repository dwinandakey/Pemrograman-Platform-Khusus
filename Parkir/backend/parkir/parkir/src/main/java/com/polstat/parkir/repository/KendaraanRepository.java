package com.polstat.parkir.repository;

import com.polstat.parkir.entity.Kendaraan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "kendaraan", path = "kendaraan")
public interface KendaraanRepository extends JpaRepository<Kendaraan, Long>, CrudRepository<Kendaraan, Long> {
    Optional<Kendaraan> findByNomorPlat(String nomorPlat);
    boolean existsByNomorPlat(String nomorPlat);
    List<Kendaraan> findByJenisKendaraan(Kendaraan.JenisKendaraan jenisKendaraan);
}
