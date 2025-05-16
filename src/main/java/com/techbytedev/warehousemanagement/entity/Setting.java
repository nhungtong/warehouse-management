package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "settings")
@Data

public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`key`", unique = true, nullable = false)
    private String key;

    @Column(name = "`value`", nullable = false)
    private String value;

    @Column(name = "description")
    private String description;
}