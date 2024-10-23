package com.polstat.perpustakaan.controller;

import com.polstat.perpustakaan.entity.Loan;
import com.polstat.perpustakaan.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    // Endpoint for borrowing a book
    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody Loan loanRequest) {
        Loan loan = Loan.builder()
                .member(loanRequest.getMember())
                .book(loanRequest.getBook())
                .borrowDate(new Date()) // Set the current date as the borrow date
                .loanStatus("sedang dipinjam") // Set the loan status to "sedang dipinjam"
                .build();

        loanRepository.save(loan);

        return ResponseEntity.ok("Book borrowed successfully.");
    }

    // Endpoint for returning a book
    @PostMapping("/return/{loanId}")
    public ResponseEntity<String> returnBook(@PathVariable Long loanId) {
        Optional<Loan> loanOptional = loanRepository.findById(loanId);

        if (!loanOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Loan record not found.");
        }

        Loan loan = loanOptional.get();
        if (!loan.getLoanStatus().equals("sedang dipinjam")) {
            return ResponseEntity.badRequest().body("This book has already been returned.");
        }

        // Set the return date to the current date
        loan.setReturnDate(new Date());
        loan.setLoanStatus("sudah dikembalikan");

        // Calculate the number of late days if applicable
        long diffInMillies = Math.abs(loan.getReturnDate().getTime() - loan.getBorrowDate().getTime());
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

        // Assume that the loan period is 7 days, adjust as needed
        int lateDays = (int) (diffInDays - 7);
        loan.setLateDays(Math.max(lateDays, 0)); // Set late days, but no negative values

        loanRepository.save(loan);

        return ResponseEntity.ok("Book returned successfully.");
    }
}

