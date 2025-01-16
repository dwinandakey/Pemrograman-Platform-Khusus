package com.polstat.parkir.entity;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    DOSEN("ROLE_DOSEN"),
    MAHASISWA("ROLE_MAHASISWA"),
    KARYAWAN("ROLE_KARYAWAN"),
    UMUM("ROLE_UMUM");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}