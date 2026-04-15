package com.magoo.magoo.service;

import com.magoo.magoo.entity.Clinique;
import com.magoo.magoo.repository.CliniqueRepository;
import com.magoo.magoo.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CliniqueService {

    private final CliniqueRepository cliniqueRepository;
    private final VilleRepository villeRepository;

    public List<Clinique> findAll() {
        return cliniqueRepository.findAllWithVille();
        // SELECT c.*, v.*
        // FROM clinique c
        // LEFT JOIN ville v ON c.id_ville = v.id
        // ORDER BY c.nom
    }

    public Clinique findById(Integer id) {
        return cliniqueRepository.findById(id).orElse(null);
        // SELECT * FROM clinique WHERE id = ?
    }

    @Transactional
    public void save(Clinique clinique, Integer villeId) {
        clinique.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        cliniqueRepository.save(clinique);
        // INSERT INTO clinique (nom, id_ville) VALUES (?, ?)
        // ou UPDATE clinique SET nom = ?, id_ville = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        cliniqueRepository.deleteById(id);
        // DELETE FROM clinique WHERE id = ?
    }
}
