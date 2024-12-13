package com.polstat.perpustakaan.service;

import com.polstat.perpustakaan.dto.MemberDto;
import java.util.List;

public interface MemberService {
    public MemberDto createMember(MemberDto memberDto);
    public MemberDto updateMember(MemberDto memberDto);
    public void deleteMember(MemberDto memberDto);
    public List<MemberDto> getMembers();
    public MemberDto getMember(Long id);
}