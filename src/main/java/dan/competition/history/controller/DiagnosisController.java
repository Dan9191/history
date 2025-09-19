package dan.competition.history.controller;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.service.DiagnosisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/diagnoses")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    // список диагнозов
    @GetMapping
    public String listDiagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        model.addAttribute("newDiagnosis", new Diagnosis());
        return "diagnoses/list";
    }

    // создание нового диагноза
    @PostMapping
    public String createDiagnosis(@ModelAttribute("newDiagnosis") Diagnosis diagnosis) {
        if (diagnosis.getImpact() == null || diagnosis.getImpact().trim().isEmpty()) {
            diagnosis.setImpact("1");
        }
        diagnosisService.save(diagnosis);
        return "redirect:/diagnoses";
    }

    // детальная страница по диагнозу
    @GetMapping("/{id}")
    public String viewDiagnosis(@PathVariable Long id, Model model) {
        Diagnosis diagnosis = diagnosisService.findById(id).orElseThrow(() -> new RuntimeException("Diagnosis not found"));
        model.addAttribute("diagnosis", diagnosis);
        // пока без списка пациентов (будет позже)
        return "diagnoses/details";
    }
}