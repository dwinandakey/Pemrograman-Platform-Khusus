package com.polstat.perpustakaan.repository;

import com.polstat.perpustakaan.entity.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "loans", path = "loans")
public interface LoanRepository extends PagingAndSortingRepository<Loan, Long>, CrudRepository<Loan, Long> {

    // Menemukan peminjaman buku berdasarkan member ID
    List<Loan> findByMemberId(@Param("member_id") Long memberId);

    // Menemukan peminjaman buku berdasarkan book ID
    List<Loan> findByBookId(@Param("book_id") Long bookId);

    // Menemukan peminjaman berdasarkan status
    List<Loan> findByLoanStatus(@Param("status") String status);

    List<Loan> findByBookIdAndLoanStatus(@Param("book_id") Long bookId, @Param("loan_status") String loanStatus);
}