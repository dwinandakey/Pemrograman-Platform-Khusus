package com.Mahasiswa.Application.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author dwinanda
 */

@Entity
@Table(name="mahasiswa")
public class Mahasiswa {

    @Id
    @Column(unique = true)
    @NotBlank(message = "NIM tidak boleh kosong")
    @Size(min = 8, max = 12, message = "NIM harus antara 8 dan 12 karakter")
    @Pattern(regexp = "^[0-9]+$", message = "NIM hanya boleh berisi angka")
    private String nim;
    
    @NotBlank(message = "Nama tidak boleh kosong")
    private String nama;
    
    @NotBlank(message = "Jurusan tidak boleh kosong")
    private String jurusan;
    
    @NotNull(message = "Tanggal lahir harus diisi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Tanggal Lahir harus di masa lalu")
    private LocalDate tanggalLahir;
    
    public Mahasiswa() {
    }

    public Mahasiswa(String nim, String nama, String jurusan, LocalDate tanggalLahir) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.tanggalLahir = tanggalLahir;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
