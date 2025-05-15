package com.techbytedev.warehousemanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
<<<<<<< HEAD
import lombok.NoArgsConstructor;
=======
>>>>>>> main
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
<<<<<<< HEAD
@NoArgsConstructor
=======
>>>>>>> main
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

<<<<<<< HEAD
    private String contactInfo;

    public Supplier(String name) {
        this.name = name;
    }
=======
    @Column(nullable = false)
    private String contactInfo;
>>>>>>> main
}
