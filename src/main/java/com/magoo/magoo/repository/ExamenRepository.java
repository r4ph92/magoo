package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {

    @Query("SELECT e FROM Examen e LEFT JOIN FETCH e.listeExamen WHERE e.patient.id = :patientId ORDER BY e.dateExamen DESC")
    // SELECT e.*, le.*
    // FROM examen e
    // LEFT JOIN liste_examen le ON e.id_liste_examen = le.id
    // WHERE e.id_patient = :patientId
    // ORDER BY e.date_examen DESC
    List<Examen> findByPatientIdWithDetails(@Param("patientId") Integer patientId);
}
