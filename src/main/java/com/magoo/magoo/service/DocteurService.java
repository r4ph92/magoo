package com.magoo.magoo.service;

import com.magoo.magoo.entity.Docteur;
import com.magoo.magoo.repository.CliniqueRepository;
import com.magoo.magoo.repository.DocteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocteurService {

    private final DocteurRepository docteurRepository;
    private final CliniqueRepository cliniqueRepository;

    public List<Docteur> findAll() {
        return docteurRepository.findAllWithClinique();
        // SELECT d.*, c.*
        // FROM docteur d
        // LEFT JOIN clinique c ON d.id_clinique = c.id
        // ORDER BY d.nom_complet
    }

    public Docteur findById(Integer id) {
        return docteurRepository.findById(id).orElse(null);
        // SELECT * FROM docteur WHERE id = ?
    }

    @Transactional
    public void save(Docteur docteur, Integer cliniqueId) {
        docteur.setClinique(cliniqueId != null ? cliniqueRepository.getReferenceById(cliniqueId) : null);
        docteurRepository.save(docteur);
        // INSERT INTO docteur (licence, nom_complet, id_clinique) VALUES (?, ?, ?)
        // ou UPDATE docteur SET licence = ?, nom_complet = ?, id_clinique = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        docteurRepository.deleteById(id);
        // DELETE FROM docteur WHERE id = ?
    }
}
