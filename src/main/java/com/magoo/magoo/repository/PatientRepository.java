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
}
