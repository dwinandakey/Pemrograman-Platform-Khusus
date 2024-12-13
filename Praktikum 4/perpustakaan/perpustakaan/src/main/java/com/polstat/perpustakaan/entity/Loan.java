package com.polstat.perpustakaan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private Date borrowDate;

    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "loan_status", nullable = false)
    private String loanStatus;

    @Column(name = "late_days")
    private Integer lateDays;

    // Default loan period in days (adjust this value as needed)
    private static final int LOAN_PERIOD_DAYS = 7;

    // This method runs before the entity is saved to the database for the first time
    @PrePersist
    public void prePersist() {
        this.borrowDate = new Date(); // Set the current date as borrow date
        this.loanStatus = "sedang dipinjam"; // Set default loan status
        this.lateDays = 0; // Initialize lateDays to 0
    }

    // Method to calculate late days when a book is returned
    public void calculateLateDays() {
        if (this.returnDate != null) {
            // Calculate the difference between the return date and borrow date
            long diffInMillies = Math.abs(this.returnDate.getTime() - this.borrowDate.getTime());
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

            // Check if the book is returned late
            int late = (int) (diffInDays - LOAN_PERIOD_DAYS);
            this.lateDays = Math.max(late, 0); // If late days are negative, set it to 0
        }
    }

    // Method to handle the return of a book
    public void returnBook() {
        this.returnDate = new Date(); // Set current date as return date
        this.loanStatus = "sudah dikembalikan"; // Update loan status
        calculateLateDays(); // Calculate the late days if any
    }
}