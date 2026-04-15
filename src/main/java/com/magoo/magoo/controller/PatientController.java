package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Patient;
import com.magoo.magoo.entity.Telephone;
import com.magoo.magoo.service.DocteurService;
import com.magoo.magoo.service.ExamenService;
import com.magoo.magoo.service.PatientService;
import com.magoo.magoo.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final VilleService villeService;
    private final DocteurService docteurService;
    private final ExamenService examenService;

    @GetMapping
    public String index(@RequestParam(required = false) Integer villeId,
                        @RequestParam(required = false) Integer docteurId,
                        @RequestParam(required = false) String sexe,
                        @RequestParam(required = false) Boolean nePasRappeler,
                        Model model) {
        model.addAttribute("patients", patientService.findWithFilters(villeId, docteurId, sexe, nePasRappeler));
        model.addAttribute("villes", villeService.findAll());
        model.addAttribute("docteurs", docteurService.findAll());
        model.addAttribute("villeId", villeId);
        model.addAttribute("docteurId", docteurId);
        model.addAttribute("sexe", sexe);
        model.addAttribute("nePasRappeler", nePasRappeler);
        return "patients/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Patient patient = patientService.findById(id);
        if (patient == null) return "redirect:/patients";

        model.addAttribute("patient", patient);
        model.addAttribute("telephones", patientService.findTelephonesByPatientId(id));
        model.addAttribute("examens", examenService.findByPatientId(id));
        return "patients/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("villes", villeService.findAll());
        model.addAttribute("docteurs", docteurService.findAll());
        return "patients/create";
    }

    @PostMapping
    public String store(@ModelAttribute Patient patient,
                        @RequestParam(required = false) Integer villeId,
                        @RequestParam(required = false) Integer docteurId,
                        RedirectAttributes ra) {
        patientService.save(patient, villeId, docteurId);
        ra.addFlashAttribute("success", "Patient créé avec succès.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Patient patient = patientService.findById(id);
        if (patient == null) return "redirect:/patients";

        model.addAttribute("patient", patient);
        model.addAttribute("villes", villeService.findAll());
        model.addAttribute("docteurs", docteurService.findAll());
        return "patients/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Patient patient,
                         @RequestParam(required = false) Integer villeId,
                         @RequestParam(required = false) Integer docteurId,
                         RedirectAttributes ra) {
        patient.setId(id);
        patientService.save(patient, villeId, docteurId);
        ra.addFlashAttribute("success", "Patient modifié avec succès.");
        return "redirect:/patients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        patientService.delete(id);
        ra.addFlashAttribute("success", "Patient supprimé.");
        return "redirect:/patients";
    }

    @PostMapping("/{id}/telephones")
    public String addTelephone(@PathVariable Integer id,
                               @ModelAttribute Telephone telephone,
                               RedirectAttributes ra) {
        patientService.addTelephone(id, telephone);
        ra.addFlashAttribute("success", "Téléphone ajouté.");
        return "redirect:/patients/" + id;
    }

    @PostMapping("/{id}/telephones/{telId}/delete")
    public String deleteTelephone(@PathVariable Integer id,
                                  @PathVariable Integer telId,
                                  RedirectAttributes ra) {
        patientService.deleteTelephone(telId);
        ra.addFlashAttribute("success", "Téléphone supprimé.");
        return "redirect:/patients/" + id;
    }
}
