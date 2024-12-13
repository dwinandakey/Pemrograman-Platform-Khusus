package com.polstat.perpustakaan.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String memberID;
    @NotEmpty(message = "Nama wajib diisi.")
    private String name;
    private String address;
    private String phoneNumber;
}
