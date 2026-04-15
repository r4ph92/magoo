package com.magoo.magoo.service;

import com.magoo.magoo.entity.Ville;
import com.magoo.magoo.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VilleService {

    private final VilleRepository villeRepository;

    public List<Ville> findAll() {
        return villeRepository.findAllOrdered();
        // SELECT * FROM ville ORDER BY nom
    }

    public Ville findById(Integer id) {
        return villeRepository.findById(id).orElse(null);
        // SELECT * FROM ville WHERE id = ?
    }

    @Transactional
    public void save(Ville ville) {
        villeRepository.save(ville);
        // INSERT INTO ville (nom, province, pays) VALUES (?, ?, ?)
        // ou UPDATE ville SET nom = ?, province = ?, pays = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        villeRepository.deleteById(id);
        // DELETE FROM ville WHERE id = ?
    }
}
