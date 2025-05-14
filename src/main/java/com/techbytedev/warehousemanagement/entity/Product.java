package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suppliers_id", referencedColumnName = "id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private Integer minStock = 0;

    private Date expirationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Product(Supplier supplier) {
        this.supplier = supplier;
    }
}
