package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Clinique;
import com.magoo.magoo.repository.CliniqueRepository;
import com.magoo.magoo.repository.VilleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliniques")
@RequiredArgsConstructor
public class CliniqueController {

    private final CliniqueRepository cliniqueRepository;
    private final VilleRepository villeRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("cliniques", cliniqueRepository.findAllWithVille());
        // SELECT c.*, v.*
        // FROM clinique c
        // LEFT JOIN ville v ON c.id_ville = v.id
        // ORDER BY c.nom
        return "cliniques/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("clinique", new Clinique());
        model.addAttribute("villes", villeRepository.findAllOrdered());
        // SELECT * FROM ville ORDER BY nom
        return "cliniques/create";
    }

    @PostMapping
    public String store(@ModelAttribute Clinique clinique,
                        @RequestParam(required = false) Integer villeId,
                        RedirectAttributes ra) {
        clinique.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        cliniqueRepository.save(clinique);
        // INSERT INTO clinique (nom, id_ville) VALUES (?, ?)
        ra.addFlashAttribute("success", "Clinique créée avec succès.");
        return "redirect:/cliniques";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Clinique clinique = cliniqueRepository.findById(id).orElse(null);
        // SELECT * FROM clinique WHERE id = ?
        if (clinique == null) return "redirect:/cliniques";

        model.addAttribute("clinique", clinique);
        model.addAttribute("villes", villeRepository.findAllOrdered());
        // SELECT * FROM ville ORDER BY nom
        return "cliniques/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Clinique clinique,
                         @RequestParam(required = false) Integer villeId,
                         RedirectAttributes ra) {
        clinique.setId(id);
        clinique.setVille(villeId != null ? villeRepository.getReferenceById(villeId) : null);
        cliniqueRepository.save(clinique);
        // UPDATE clinique SET nom = ?, id_ville = ? WHERE id = ?
        ra.addFlashAttribute("success", "Clinique modifiée avec succès.");
        return "redirect:/cliniques";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        cliniqueRepository.deleteById(id);
        // DELETE FROM clinique WHERE id = ?
        ra.addFlashAttribute("success", "Clinique supprimée.");
        return "redirect:/cliniques";
    }
}
