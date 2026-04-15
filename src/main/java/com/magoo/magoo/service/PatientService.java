package com.magoo.magoo.service;

import com.magoo.magoo.entity.Patient;
import com.magoo.magoo.entity.Telephone;
import com.magoo.magoo.repository.DocteurRepository;
import com.magoo.magoo.repository.PatientRepository;
import com.magoo.magoo.repository.TelephoneRepository;
import com.magoo.magoo.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final VilleRepository villeRepository;
    private final DocteurRepository docteurRepository;
    private final TelephoneRepository telephoneRepository;

    public List<Patient> findAll() {
        return patientRepository.findAllWithDetails();
        // SELECT p.*, d.*, c.*, v.*
        // FROM patient p
        // LEFT JOIN docteur d ON p.id_docteur = d.id
        // LEFT JOIN clinique c ON d.id_clinique = c.id
        // LEFT JOIN ville v ON p.id_ville = v.id
        // ORDER BY p.nom, p.prenom
    }

    public List<Patient> findWithFilters(Integer villeId, Integer docteurId, String sexe, Boolean nePasRappeler) {
        return patientRepository.findWithFilters(villeId, docteurId, sexe, nePasRappeler);
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
    }

    public List<Telephone> findTelephonesByPatientId(Integer patientId) {
        return telephoneRepository.findByPatientId(patientId);
        // SELECT * FROM telephone WHERE id_patient = ?
    }

    @Transactional
    public void addTelephone(Integer patientId, Telephone telephone) {
        telephone.setPatient(patientRepository.getReferenceById(patientId));
        telephoneRepository.save(telephone);
        // INSERT INTO telephone (id_patient, numero, type_tel) VALUES (?, ?, ?)
    }

    @Transactional
    public void deleteTelephone(Integer telephoneId) {
        telephoneRepository.deleteById(telephoneId);
        // DELETE FROM telephone WHERE id = ?
    }

    public Patient findById(Integer id) {
        return patientRepository.findByIdWithDetails(id).orElse(null);
        // SELECT p.*, d.*, c.*, v.*
        // FROM patient p
        // LEFT JOIN docteur d ON p.id_docteur = d.id
        // LEFT JOIN clinique c ON d.id_clinique = c.id
        // LEFT JOIN ville v ON p.id_ville = v.id
        // WHERE p.id = ?
    }

    @Transactional
    public void save(Patient patient, Integer villeId, Integer docteurId) {
        patient.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        patient.setDocteur(docteurId != null ? docteurRepository.getReferenceById(docteurId) : null);
        patientRepository.save(patient);
        // INSERT INTO patient (...) VALUES (...)
        // ou UPDATE patient SET ... WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        patientRepository.deleteById(id);
        // DELETE FROM patient WHERE id = ?
        // (cascade supprime les téléphones et examens associés)
    }
}
