package com.magoo.magoo.service;

import com.magoo.magoo.entity.Examen;
import com.magoo.magoo.repository.ExamenRepository;
import com.magoo.magoo.repository.ListeExamenRepository;
import com.magoo.magoo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamenService {

    private final ExamenRepository examenRepository;
    private final PatientRepository patientRepository;
    private final ListeExamenRepository listeExamenRepository;

    public List<Examen> findAll() {
        return examenRepository.findAllWithDetails();
        // SELECT e.*, le.*, p.*
        // FROM examen e
        // LEFT JOIN liste_examen le ON e.id_liste_examen = le.id
        // LEFT JOIN patient p ON e.id_patient = p.id
        // ORDER BY e.date_examen DESC
    }

    public List<Examen> findByPatientId(Integer patientId) {
        return examenRepository.findByPatientIdWithDetails(patientId);
        // SELECT e.*, le.*
        // FROM examen e
        // LEFT JOIN liste_examen le ON e.id_liste_examen = le.id
        // WHERE e.id_patient = ?
        // ORDER BY e.date_examen DESC
    }

    public Examen findById(Integer id) {
        return examenRepository.findById(id).orElse(null);
        // SELECT * FROM examen WHERE id = ?
    }

    @Transactional
    public void save(Examen examen, Integer patientId, Integer listeExamenId) {
        examen.setPatient(patientRepository.getReferenceById(patientId));
        examen.setListeExamen(listeExamenRepository.getReferenceById(listeExamenId));
        examenRepository.save(examen);
        // INSERT INTO examen (id_patient, id_liste_examen, date_examen) VALUES (?, ?, ?)
        // ou UPDATE examen SET id_patient = ?, id_liste_examen = ?, date_examen = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        examenRepository.deleteById(id);
        // DELETE FROM examen WHERE id = ?
    }
}
