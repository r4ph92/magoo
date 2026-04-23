package com.magoo.magoo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinique")
@Getter @Setter @NoArgsConstructor @ToString
public class Clinique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150, unique = true)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville")
    private Ville ville;
}
