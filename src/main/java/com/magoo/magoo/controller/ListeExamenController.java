package com.magoo.magoo.controller;

import com.magoo.magoo.entity.ListeExamen;
import com.magoo.magoo.service.ListeExamenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/liste-examens")
@RequiredArgsConstructor
public class ListeExamenController {

    private final ListeExamenService listeExamenService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("listeExamens", listeExamenService.findAll());
        return "liste-examens/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("listeExamen", new ListeExamen());
        return "liste-examens/create";
    }

    @PostMapping
    public String store(@ModelAttribute ListeExamen listeExamen, RedirectAttributes ra) {
        listeExamenService.save(listeExamen);
        ra.addFlashAttribute("success", "Type d'examen créé avec succès.");
        return "redirect:/liste-examens";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        ListeExamen listeExamen = listeExamenService.findById(id);
        if (listeExamen == null) return "redirect:/liste-examens";

        model.addAttribute("listeExamen", listeExamen);
        return "liste-examens/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute ListeExamen listeExamen, RedirectAttributes ra) {
        listeExamen.setId(id);
        listeExamenService.save(listeExamen);
        ra.addFlashAttribute("success", "Type d'examen modifié avec succès.");
        return "redirect:/liste-examens";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        listeExamenService.delete(id);
        ra.addFlashAttribute("success", "Type d'examen supprimé.");
        return "redirect:/liste-examens";
    }
}
