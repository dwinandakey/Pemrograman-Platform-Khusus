/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Mahasiswa.Application.Controller;

import com.Mahasiswa.Application.Model.Mahasiswa;
import com.Mahasiswa.Application.Services.MahasiswaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author dwinanda
 */
@Controller
public class MahasiswaController {

  @Autowired
  private MahasiswaService mahasiswaService;

  @GetMapping("/daftar")
  public String showMahasiswaList(
    Model model,
    @RequestParam(value = "keyword", required = false) String keyword
  ) {
    model.addAttribute("mahasiswaList", mahasiswaService.getAllMahasiswa());
    return "daftar";
  }

  @GetMapping("/tambah")
  public String tambahForm(Model model) {
    model.addAttribute("mahasiswa", new Mahasiswa());
    return "tambah";
  }

  @PostMapping("/tambah")
  public String addMahasiswa(
    @Valid Mahasiswa mahasiswa,
    BindingResult result,
    Model model
  ) {
    if (result.hasErrors()) {
      model.addAttribute("mahasiswa", mahasiswa);
      return "tambah";
    }

    if (result.hasErrors()) {
      return "tambah";
    }

    if (mahasiswaService.existsByNim(mahasiswa.getNim())) {
      model.addAttribute("mahasiswa", mahasiswa);
      result.rejectValue("nim", "error.nim", "NIM sudah terdaftar!");
      return "tambah";
    }

    mahasiswaService.saveMahasiswa(mahasiswa);
    return "redirect:/daftar";
  }

  @GetMapping("/edit/{nim}")
  public String editMahasiswa(@PathVariable String nim, Model model) {
    Mahasiswa mahasiswa = mahasiswaService.getMahasiswaByNim(nim);
    if (mahasiswa == null) {
      return "redirect:/error";
    }
    model.addAttribute("mahasiswa", mahasiswa);
    return "edit";
  }

  @PostMapping("/update/{nim}")
  public String updateMahasiswa(
    @PathVariable String nim,
    @RequestParam String nama,
    @RequestParam String jurusan,
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE
    ) LocalDate tanggalLahir
  ) {
    Mahasiswa mahasiswa = mahasiswaService.getMahasiswaByNim(nim);
    if (mahasiswa != null) {
      mahasiswa.setNama(nama);
      mahasiswa.setJurusan(jurusan);
      mahasiswa.setTanggalLahir(tanggalLahir);

      mahasiswaService.saveMahasiswa(mahasiswa);
    }
    return "redirect:/daftar";
  }

  @GetMapping("/delete/{nim}")
  public String deleteMahasiswa(@PathVariable String nim) {
    mahasiswaService.deleteMahasiswa(nim);
    return "redirect:/daftar";
  }

  @GetMapping("/detail/{nim}")
  public String detailMahasiswa(@PathVariable String nim, Model model) {
    Mahasiswa mahasiswa = mahasiswaService.getMahasiswaByNim(nim);
    if (mahasiswa == null) {
      return "redirect:/error";
    }
    model.addAttribute("mahasiswa", mahasiswa);
    return "detail";
  }

  @GetMapping("/cari")
  public String searchMahasiswa(@RequestParam String keyword, Model model) {
    model.addAttribute(
      "mahasiswaList",
      mahasiswaService.searchMahasiswa(keyword)
    );
    return "daftar";
  }
}
