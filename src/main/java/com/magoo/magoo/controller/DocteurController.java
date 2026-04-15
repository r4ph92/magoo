package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Docteur;
import com.magoo.magoo.service.CliniqueService;
import com.magoo.magoo.service.DocteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/docteurs")
@RequiredArgsConstructor
public class DocteurController {

    private final DocteurService docteurService;
    private final CliniqueService cliniqueService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("docteurs", docteurService.findAll());
        return "docteurs/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("docteur", new Docteur());
        model.addAttribute("cliniques", cliniqueService.findAll());
        return "docteurs/create";
    }

    @PostMapping
    public String store(@ModelAttribute Docteur docteur,
                        @RequestParam(required = false) Integer cliniqueId,
                        RedirectAttributes ra) {
        docteurService.save(docteur, cliniqueId);
        ra.addFlashAttribute("success", "Docteur créé avec succès.");
        return "redirect:/docteurs";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Docteur docteur = docteurService.findById(id);
        if (docteur == null) return "redirect:/docteurs";

        model.addAttribute("docteur", docteur);
        model.addAttribute("cliniques", cliniqueService.findAll());
        return "docteurs/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Docteur docteur,
                         @RequestParam(required = false) Integer cliniqueId,
                         RedirectAttributes ra) {
        docteur.setId(id);
        docteurService.save(docteur, cliniqueId);
        ra.addFlashAttribute("success", "Docteur modifié avec succès.");
        return "redirect:/docteurs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        docteurService.delete(id);
        ra.addFlashAttribute("success", "Docteur supprimé.");
        return "redirect:/docteurs";
    }
}
