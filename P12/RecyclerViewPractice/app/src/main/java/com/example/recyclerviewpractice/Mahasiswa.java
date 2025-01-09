package com.example.recyclerviewpractice;

public class Mahasiswa {

    private String nama;
    private String nim;
    private String nohp;

    public Mahasiswa(String nama, String nim, String
            nohp) {
        this.nama = nama;
        this.nim = nim;
        this.nohp = nohp;

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }
}
