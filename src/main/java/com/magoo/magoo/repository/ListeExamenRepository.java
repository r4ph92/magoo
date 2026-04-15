package com.magoo.magoo.repository;

import com.magoo.magoo.entity.ListeExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ListeExamenRepository extends JpaRepository<ListeExamen, Integer> {

    @Query("SELECT l FROM ListeExamen l ORDER BY l.nom")
    // SELECT * FROM liste_examen ORDER BY nom
    List<ListeExamen> findAllOrdered();
}
