package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Patient;
import com.magoo.magoo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;
    private final VilleRepository villeRepository;
    private final DocteurRepository docteurRepository;
    private final TelephoneRepository telephoneRepository;
    private final ExamenRepository examenRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("patients", patientRepository.findAllWithDetails());
        return "patients/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Patient patient = patientRepository.findByIdWithDetails(id).orElse(null);
        if (patient == null) return "redirect:/patients";

        model.addAttribute("patient", patient);
        model.addAttribute("telephones", telephoneRepository.findByPatientId(id));
        model.addAttribute("examens", examenRepository.findByPatientIdWithDetails(id));
        return "patients/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("docteurs", docteurRepository.findAllWithClinique());
        return "patients/create";
    }

    @PostMapping
    public String store(@ModelAttribute Patient patient,
                        @RequestParam(required = false) Integer villeId,
                        @RequestParam(required = false) Integer docteurId,
                        RedirectAttributes ra) {
        patient.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        patient.setDocteur(docteurId != null ? docteurRepository.getReferenceById(docteurId) : null);
        patientRepository.save(patient);
        ra.addFlashAttribute("success", "Patient créé avec succès.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Patient patient = patientRepository.findByIdWithDetails(id).orElse(null);
        if (patient == null) return "redirect:/patients";

        model.addAttribute("patient", patient);
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("docteurs", docteurRepository.findAllWithClinique());
        return "patients/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Patient patient,
                         @RequestParam(required = false) Integer villeId,
                         @RequestParam(required = false) Integer docteurId,
                         RedirectAttributes ra) {
        patient.setId(id);
        patient.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        patient.setDocteur(docteurId != null ? docteurRepository.getReferenceById(docteurId) : null);
        patientRepository.save(patient);
        ra.addFlashAttribute("success", "Patient modifié avec succès.");
        return "redirect:/patients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        patientRepository.deleteById(id);
        ra.addFlashAttribute("success", "Patient supprimé.");
        return "redirect:/patients";
    }
}
