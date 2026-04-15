package com.magoo.magoo.service;

import com.magoo.magoo.entity.ListeExamen;
import com.magoo.magoo.repository.ListeExamenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListeExamenService {

    private final ListeExamenRepository listeExamenRepository;

    public List<ListeExamen> findAll() {
        return listeExamenRepository.findAllOrdered();
        // SELECT * FROM liste_examen ORDER BY nom
    }

    public ListeExamen findById(Integer id) {
        return listeExamenRepository.findById(id).orElse(null);
        // SELECT * FROM liste_examen WHERE id = ?
    }

    @Transactional
    public void save(ListeExamen listeExamen) {
        listeExamenRepository.save(listeExamen);
        // INSERT INTO liste_examen (nom) VALUES (?)
        // ou UPDATE liste_examen SET nom = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        listeExamenRepository.deleteById(id);
        // DELETE FROM liste_examen WHERE id = ?
    }
}
