package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String init;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suppliers_id", referencedColumnName = "id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private Integer minStock = 0;

    private Date expirationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
