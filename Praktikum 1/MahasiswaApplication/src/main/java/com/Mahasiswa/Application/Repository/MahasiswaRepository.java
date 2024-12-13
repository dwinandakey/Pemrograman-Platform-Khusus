package com.Mahasiswa.Application.Repository;

import com.Mahasiswa.Application.Model.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface MahasiswaRepository extends JpaRepository<Mahasiswa, String> {
    List<Mahasiswa> findByNamaContainingOrNimContaining(String nama, String nim);
    
    @Query("SELECT m FROM Mahasiswa m WHERE m.nim LIKE %:keyword% OR LOWER(m.nama) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Mahasiswa> search(String keyword); 
}
