package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Clinique;
import com.magoo.magoo.service.CliniqueService;
import com.magoo.magoo.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliniques")
@RequiredArgsConstructor
public class CliniqueController {

    private final CliniqueService cliniqueService;
    private final VilleService villeService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("cliniques", cliniqueService.findAll());
        return "cliniques/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("clinique", new Clinique());
        model.addAttribute("villes", villeService.findAll());
        return "cliniques/create";
    }

    @PostMapping
    public String store(@ModelAttribute Clinique clinique,
                        @RequestParam(required = false) Integer villeId,
                        RedirectAttributes ra) {
        cliniqueService.save(clinique, villeId);
        ra.addFlashAttribute("success", "Clinique créée avec succès.");
        return "redirect:/cliniques";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Clinique clinique = cliniqueService.findById(id);
        if (clinique == null) return "redirect:/cliniques";

        model.addAttribute("clinique", clinique);
        model.addAttribute("villes", villeService.findAll());
        return "cliniques/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Clinique clinique,
                         @RequestParam(required = false) Integer villeId,
                         RedirectAttributes ra) {
        clinique.setId(id);
        cliniqueService.save(clinique, villeId);
        ra.addFlashAttribute("success", "Clinique modifiée avec succès.");
        return "redirect:/cliniques";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        cliniqueService.delete(id);
        ra.addFlashAttribute("success", "Clinique supprimée.");
        return "redirect:/cliniques";
    }
}
