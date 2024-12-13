package com.polstat.perpustakaan.service;

import com.polstat.perpustakaan.dto.MemberDto;
import com.polstat.perpustakaan.entity.Member;
import com.polstat.perpustakaan.mapper.MemberMapper;
import com.polstat.perpustakaan.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        Member member = memberRepository.save(MemberMapper.mapToMember(memberDto));
        return MemberMapper.mapToMemberDto(member);
    }

    @Override
    public List<MemberDto> getMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberMapper::mapToMemberDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDto getMember(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            throw new EntityNotFoundException("Member not found with id: " + id);
        }
        return MemberMapper.mapToMemberDto(memberOptional.get());
    }

    @Override
    public MemberDto updateMember(MemberDto memberDto) {
        if (memberDto.getId() != null) {
            Optional<Member> existingMember = memberRepository.findById(memberDto.getId());
            if (existingMember.isEmpty()) {
                throw new EntityNotFoundException("Member not found with id: " + memberDto.getId());
            }
        }
        Member member = memberRepository.save(MemberMapper.mapToMember(memberDto));
        return MemberMapper.mapToMemberDto(member);
    }

    @Override
    public void deleteMember(MemberDto memberDto) {
        if (memberDto.getId() != null) {
            Optional<Member> existingMember = memberRepository.findById(memberDto.getId());
            if (existingMember.isEmpty()) {
                throw new EntityNotFoundException("Member not found with id: " + memberDto.getId());
            }
        }
        memberRepository.delete(MemberMapper.mapToMember(memberDto));
    }
}