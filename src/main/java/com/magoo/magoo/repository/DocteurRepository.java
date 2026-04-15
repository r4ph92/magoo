package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocteurRepository extends JpaRepository<Docteur, Integer> {

    @Query("SELECT d FROM Docteur d LEFT JOIN FETCH d.clinique ORDER BY d.nomComplet")
    // SELECT d.*, c.*
    // FROM docteur d
    // LEFT JOIN clinique c ON d.id_clinique = c.id
    // ORDER BY d.nom_complet
    List<Docteur> findAllWithClinique();
}
