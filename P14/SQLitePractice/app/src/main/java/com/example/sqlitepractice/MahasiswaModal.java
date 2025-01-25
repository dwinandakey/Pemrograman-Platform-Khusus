package com.example.sqlitepractice;

public class MahasiswaModal {
    private String nim;
    private String nama;
    private String kelas;
    private String nohp;
    private int id;

    public MahasiswaModal(String nim, String nama, String
            kelas, String nohp){
        this.nim = nim;
        this.nama = nama;
        this.kelas = kelas;
        this.nohp = nohp;
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

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
