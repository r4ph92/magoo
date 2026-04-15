package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Examen;
import com.magoo.magoo.repository.ExamenRepository;
import com.magoo.magoo.repository.ListeExamenRepository;
import com.magoo.magoo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/examens")
@RequiredArgsConstructor
public class ExamenController {

    private final ExamenRepository examenRepository;
    private final PatientRepository patientRepository;
    private final ListeExamenRepository listeExamenRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("examens", examenRepository.findAllWithDetails());
        // SELECT e.*, le.*, p.*
        // FROM examen e
        // LEFT JOIN liste_examen le ON e.id_liste_examen = le.id
        // LEFT JOIN patient p ON e.id_patient = p.id
        // ORDER BY e.date_examen DESC
        return "examens/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("examen", new Examen());
        model.addAttribute("patients", patientRepository.findAllWithDetails());
        model.addAttribute("listeExamens", listeExamenRepository.findAllOrdered());
        // SELECT * FROM liste_examen ORDER BY nom
        return "examens/create";
    }

    @PostMapping
    public String store(@ModelAttribute Examen examen,
                        @RequestParam Integer patientId,
                        @RequestParam Integer listeExamenId,
                        RedirectAttributes ra) {
        examen.setPatient(patientRepository.getReferenceById(patientId));
        examen.setListeExamen(listeExamenRepository.getReferenceById(listeExamenId));
        examenRepository.save(examen);
        // INSERT INTO examen (id_patient, id_liste_examen, date_examen) VALUES (?, ?, ?)
        ra.addFlashAttribute("success", "Examen ajouté avec succès.");
        return "redirect:/examens";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Examen examen = examenRepository.findById(id).orElse(null);
        // SELECT * FROM examen WHERE id = ?
        if (examen == null) return "redirect:/examens";

        model.addAttribute("examen", examen);
        model.addAttribute("patients", patientRepository.findAllWithDetails());
        model.addAttribute("listeExamens", listeExamenRepository.findAllOrdered());
        return "examens/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Examen examen,
                         @RequestParam Integer patientId,
                         @RequestParam Integer listeExamenId,
                         RedirectAttributes ra) {
        examen.setId(id);
        examen.setPatient(patientRepository.getReferenceById(patientId));
        examen.setListeExamen(listeExamenRepository.getReferenceById(listeExamenId));
        examenRepository.save(examen);
        // UPDATE examen SET id_patient = ?, id_liste_examen = ?, date_examen = ? WHERE id = ?
        ra.addFlashAttribute("success", "Examen modifié avec succès.");
        return "redirect:/examens";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        examenRepository.deleteById(id);
        // DELETE FROM examen WHERE id = ?
        ra.addFlashAttribute("success", "Examen supprimé.");
        return "redirect:/examens";
    }
}
