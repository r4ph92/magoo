package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Clinique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CliniqueRepository extends JpaRepository<Clinique, Integer> {

    @Query("SELECT c FROM Clinique c LEFT JOIN FETCH c.ville ORDER BY c.nom")
    // SELECT c.*, v.*
    // FROM clinique c
    // LEFT JOIN ville v ON c.id_ville = v.id
    // ORDER BY c.nom
    List<Clinique> findAllWithVille();
}
