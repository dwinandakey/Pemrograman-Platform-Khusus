package com.Mahasiswa.Application.Services;

import com.Mahasiswa.Application.Model.Mahasiswa;
import com.Mahasiswa.Application.Repository.MahasiswaRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class MahasiswaService {

  @Autowired
  private MahasiswaRepository mahasiswaRepository;

  public List<Mahasiswa> getAllMahasiswa() {
    return mahasiswaRepository.findAll();
  }

  public List<Mahasiswa> searchMahasiswa(String keyword) {
    return mahasiswaRepository.search(keyword); // Menggunakan query kustom dari repository
  }

  @Transactional
  public void saveMahasiswa(Mahasiswa mahasiswa) {
    try {
      mahasiswaRepository.save(mahasiswa);
    } catch (DataIntegrityViolationException e) {
      throw new RuntimeException(
        "NIM sudah ada di database. Silakan gunakan NIM yang berbeda."
      );
    }
  }

  public Mahasiswa getMahasiswaByNim(String nim) {
    return mahasiswaRepository
      .findById(nim)
      .orElseThrow(() ->
        new RuntimeException("Mahasiswa tidak ditemukan dengan NIM: " + nim)
      );
  }

  public void deleteMahasiswa(String nim) {
    mahasiswaRepository.deleteById(nim);
  }

  public boolean existsByNim(String nim) {
    return mahasiswaRepository.existsById(nim);
  }
}
