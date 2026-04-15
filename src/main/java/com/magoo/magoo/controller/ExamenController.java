package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Examen;
import com.magoo.magoo.service.ExamenService;
import com.magoo.magoo.service.ListeExamenService;
import com.magoo.magoo.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/examens")
@RequiredArgsConstructor
public class ExamenController {

    private final ExamenService examenService;
    private final PatientService patientService;
    private final ListeExamenService listeExamenService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("examens", examenService.findAll());
        return "examens/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("examen", new Examen());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("listeExamens", listeExamenService.findAll());
        return "examens/create";
    }

    @PostMapping
    public String store(@ModelAttribute Examen examen,
                        @RequestParam Integer patientId,
                        @RequestParam Integer listeExamenId,
                        RedirectAttributes ra) {
        examenService.save(examen, patientId, listeExamenId);
        ra.addFlashAttribute("success", "Examen ajouté avec succès.");
        return "redirect:/examens";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Examen examen = examenService.findById(id);
        if (examen == null) return "redirect:/examens";

        model.addAttribute("examen", examen);
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("listeExamens", listeExamenService.findAll());
        return "examens/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Examen examen,
                         @RequestParam Integer patientId,
                         @RequestParam Integer listeExamenId,
                         RedirectAttributes ra) {
        examen.setId(id);
        examenService.save(examen, patientId, listeExamenId);
        ra.addFlashAttribute("success", "Examen modifié avec succès.");
        return "redirect:/examens";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        examenService.delete(id);
        ra.addFlashAttribute("success", "Examen supprimé.");
        return "redirect:/examens";
    }
}
