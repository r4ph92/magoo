package com.magoo.magoo.service;

import com.magoo.magoo.entity.Telephone;
import com.magoo.magoo.repository.TelephoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelephoneService {

    private final TelephoneRepository telephoneRepository;

    public List<Telephone> findByPatientId(Integer patientId) {
        return telephoneRepository.findByPatientId(patientId);
        // SELECT * FROM telephone WHERE id_patient = ?
    }

    @Transactional
    public void save(Telephone telephone) {
        telephoneRepository.save(telephone);
        // INSERT INTO telephone (id_patient, numero, type_tel) VALUES (?, ?, ?)
        // ou UPDATE telephone SET numero = ?, type_tel = ? WHERE id = ?
    }

    @Transactional
    public void delete(Integer id) {
        telephoneRepository.deleteById(id);
        // DELETE FROM telephone WHERE id = ?
    }
}
