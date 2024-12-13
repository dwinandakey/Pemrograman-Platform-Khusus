package com.polstat.perpustakaan.repository;

import com.polstat.perpustakaan.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "member", path = "member")
public interface MemberRepository extends CrudRepository<Member, Long> {
  // Create
  Member save(Member member);

  // Read
  List<Member> findAll();
  Optional<Member> findById(Long id);

  // Update - menggunakan save yang sama dengan create

  // Delete
  void delete(Member member);
}
