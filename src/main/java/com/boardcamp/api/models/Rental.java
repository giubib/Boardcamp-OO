package com.boardcamp.api.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "rent_date", nullable = false)
    private LocalDate rentDate;

    @Column(name = "days_rented", nullable = false)
    private Integer daysRented;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "delay_fee", nullable = false)
    private Integer delayFee;
}
