package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_check")
@Getter
@Setter
public class StockCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(nullable = false)
    private Integer actualQuantity;

    @Column(nullable = false)
    private Integer systemQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checked_by", referencedColumnName = "id")
    private User checkedBy;

    @Column(nullable = false)
    private LocalDateTime createAt;
}
