package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.docteur d LEFT JOIN FETCH d.clinique LEFT JOIN FETCH p.ville ORDER BY p.nom, p.prenom")
    // SELECT p.*, d.*, c.*, v.*
    // FROM patient p
    // LEFT JOIN docteur d ON p.id_docteur = d.id
    // LEFT JOIN clinique c ON d.id_clinique = c.id
    // LEFT JOIN ville v ON p.id_ville = v.id
    // ORDER BY p.nom, p.prenom
    List<Patient> findAllWithDetails();

    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.docteur d LEFT JOIN FETCH d.clinique LEFT JOIN FETCH p.ville WHERE p.id = :id")
    // SELECT p.*, d.*, c.*, v.*
    // FROM patient p
    // LEFT JOIN docteur d ON p.id_docteur = d.id
    // LEFT JOIN clinique c ON d.id_clinique = c.id
    // LEFT JOIN ville v ON p.id_ville = v.id
    // WHERE p.id = :id
    Optional<Patient> findByIdWithDetails(@Param("id") Integer id);

    @Query("""
        SELECT p FROM Patient p
        LEFT JOIN FETCH p.docteur d
        LEFT JOIN FETCH d.clinique
        LEFT JOIN FETCH p.ville
        WHERE (:villeId IS NULL OR p.ville.id = :villeId)
          AND (:docteurId IS NULL OR p.docteur.id = :docteurId)
          AND (:sexe IS NULL OR p.sexe = :sexe)
          AND (:nePasRappeler IS NULL OR p.nePasRappeler = :nePasRappeler)
        ORDER BY p.nom, p.prenom
        """)
    // SELECT p.*, d.*, c.*, v.*
    // FROM patient p
    // LEFT JOIN docteur d ON p.id_docteur = d.id
    // LEFT JOIN clinique c ON d.id_clinique = c.id
    // LEFT JOIN ville v ON p.id_ville = v.id
    // WHERE (:villeId IS NULL OR p.id_ville = :villeId)
    //   AND (:docteurId IS NULL OR p.id_docteur = :docteurId)
    //   AND (:sexe IS NULL OR p.sexe = :sexe)
    //   AND (:nePasRappeler IS NULL OR p.ne_pas_rappeler = :nePasRappeler)
    // ORDER BY p.nom, p.prenom
    List<Patient> findWithFilters(
        @Param("villeId") Integer villeId,
        @Param("docteurId") Integer docteurId,
        @Param("sexe") String sexe,
        @Param("nePasRappeler") Boolean nePasRappeler
    );
}
