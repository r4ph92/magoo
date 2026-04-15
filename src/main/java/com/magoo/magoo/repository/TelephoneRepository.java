package com.magoo.magoo.repository;

import com.magoo.magoo.entity.Telephone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelephoneRepository extends JpaRepository<Telephone, Integer> {

    // SELECT * FROM telephone WHERE id_patient = :patientId
    List<Telephone> findByPatientId(Integer patientId);
}
