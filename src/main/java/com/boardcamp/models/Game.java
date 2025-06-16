package com.boardcamp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "games")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String image;
    @Column(name = "stock_total", nullable = false)
    private Integer stockTotal;
    @Column(name = "price_per_day", nullable = false)
    private Integer pricePerDay;
}
