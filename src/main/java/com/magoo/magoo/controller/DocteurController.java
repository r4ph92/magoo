package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Docteur;
import com.magoo.magoo.repository.CliniqueRepository;
import com.magoo.magoo.repository.DocteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/docteurs")
@RequiredArgsConstructor
public class DocteurController {

    private final DocteurRepository docteurRepository;
    private final CliniqueRepository cliniqueRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("docteurs", docteurRepository.findAllWithClinique());
        // SELECT d.*, c.*
        // FROM docteur d
        // LEFT JOIN clinique c ON d.id_clinique = c.id
        // ORDER BY d.nom_complet
        return "docteurs/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("docteur", new Docteur());
        model.addAttribute("cliniques", cliniqueRepository.findAll());
        // SELECT * FROM clinique
        return "docteurs/create";
    }

    @PostMapping
    public String store(@ModelAttribute Docteur docteur,
                        @RequestParam(required = false) Integer cliniqueId,
                        RedirectAttributes ra) {
        docteur.setClinique(cliniqueId != null ? cliniqueRepository.getReferenceById(cliniqueId) : null);
        docteurRepository.save(docteur);
        // INSERT INTO docteur (licence, nom_complet, id_clinique) VALUES (?, ?, ?)
        ra.addFlashAttribute("success", "Docteur créé avec succès.");
        return "redirect:/docteurs";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Docteur docteur = docteurRepository.findById(id).orElse(null);
        // SELECT * FROM docteur WHERE id = ?
        if (docteur == null) return "redirect:/docteurs";

        model.addAttribute("docteur", docteur);
        model.addAttribute("cliniques", cliniqueRepository.findAll());
        // SELECT * FROM clinique
        return "docteurs/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Docteur docteur,
                         @RequestParam(required = false) Integer cliniqueId,
                         RedirectAttributes ra) {
        docteur.setId(id);
        docteur.setClinique(cliniqueId != null ? cliniqueRepository.getReferenceById(cliniqueId) : null);
        docteurRepository.save(docteur);
        // UPDATE docteur SET licence = ?, nom_complet = ?, id_clinique = ? WHERE id = ?
        ra.addFlashAttribute("success", "Docteur modifié avec succès.");
        return "redirect:/docteurs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        docteurRepository.deleteById(id);
        // DELETE FROM docteur WHERE id = ?
        ra.addFlashAttribute("success", "Docteur supprimé.");
        return "redirect:/docteurs";
    }
}
