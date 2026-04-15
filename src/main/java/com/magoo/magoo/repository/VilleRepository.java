package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VilleRepository extends JpaRepository<Ville, Integer> {

    @Query("SELECT v FROM Ville v ORDER BY v.nom, v.province")
    // SELECT * FROM ville ORDER BY nom, province
    List<Ville> findAllOrdered();
}
