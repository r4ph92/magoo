package com.magoo.magoo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ville")
@Getter @Setter @NoArgsConstructor
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 50)
    private String province;

    @Column(length = 50)
    private String pays;

    @Override
    public String toString() {
        return nom + (province != null ? ", " + province : "");
    }
}
