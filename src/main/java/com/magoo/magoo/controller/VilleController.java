package com.magoo.magoo.controller;

import com.magoo.magoo.entity.Ville;
import com.magoo.magoo.service.VilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/villes")
@RequiredArgsConstructor
public class VilleController {

    private final VilleService villeService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("villes", villeService.findAll());
        return "villes/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("ville", new Ville());
        return "villes/create";
    }

    @PostMapping
    public String store(@ModelAttribute Ville ville, RedirectAttributes ra) {
        villeService.save(ville);
        ra.addFlashAttribute("success", "Ville créée avec succès.");
        return "redirect:/villes";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        Ville ville = villeService.findById(id);
        if (ville == null) return "redirect:/villes";

        model.addAttribute("ville", ville);
        return "villes/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute Ville ville, RedirectAttributes ra) {
        ville.setId(id);
        villeService.save(ville);
        ra.addFlashAttribute("success", "Ville modifiée avec succès.");
        return "redirect:/villes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        villeService.delete(id);
        ra.addFlashAttribute("success", "Ville supprimée.");
        return "redirect:/villes";
    }
}
