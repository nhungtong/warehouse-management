package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock_out_details")
@Setter
@Getter
public class StockOutDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id", referencedColumnName = "id")
    private StockOutForm stockOutForm;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
