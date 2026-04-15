package com.magoo.magoo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "docteur")
@Getter @Setter @NoArgsConstructor
public class Docteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String licence;

    @Column(name = "nom_complet", nullable = false, length = 150)
    private String nomComplet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clinique")
    private Clinique clinique;
}
