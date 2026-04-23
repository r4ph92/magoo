package com.magoo.magoo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Getter @Setter @NoArgsConstructor @ToString
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 20)
    private String sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 50)
    private String langue;

    @Column(length = 150)
    private String courriel;

    @Column(name = "adresse_rue", length = 200)
    private String adresseRue;

    @Column(name = "adresse_appart", length = 50)
    private String adresseAppart;

    @Column(name = "code_postal", length = 20)
    private String codePostal;

    @Column(name = "ramq_date_exp")
    private LocalDate ramqDateExp;

    @Column(name = "dossier_no")
    private Integer dossierNo;

    @Column(length = 150)
    private String profession;

    // DB default: CURRENT_DATE — excluded from INSERT/UPDATE so the DB handles it
    @Column(name = "date_creation", insertable = false, updatable = false)
    private LocalDate dateCreation;

    @Column(name = "ne_pas_rappeler")
    private Boolean nePasRappeler = false;

    @Column(name = "est_decede")
    private Boolean estDecede = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docteur")
    private Docteur docteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville")
    private Ville ville;

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
