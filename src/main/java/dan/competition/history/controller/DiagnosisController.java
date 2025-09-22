package dan.competition.history.controller;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.model.DiagnosisDTO;
import dan.competition.history.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/diagnoses")
@Slf4j
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    // список диагнозов
    @GetMapping
    public String listDiagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
        model.addAttribute("newDiagnosis", new Diagnosis());
        return "diagnoses/list";
    }

    // создание нового диагноза
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createDiagnosis(@Valid @RequestBody DiagnosisDTO diagnosisDto,
                                                               BindingResult result) {
        log.info("Creating diagnosis with DTO: {}", diagnosisDto);
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            log.warn("Validation errors: {}", result.getAllErrors());
            response.put("message", "Пожалуйста, исправьте ошибки в форме: " + result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        try {
            diagnosisService.createDiagnosis(diagnosisDto);
            log.info("Diagnosis created successfully");
            response.put("message", "Диагноз успешно создан");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating diagnosis: {}", e.getMessage(), e);
            response.put("message", "Ошибка при создании диагноза: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // детальная страница по диагнозу
    @GetMapping("/{id}")
    public String viewDiagnosis(@PathVariable Long id, Model model) {
        Diagnosis diagnosis = diagnosisService.findById(id).orElseThrow(() -> new RuntimeException("Diagnosis not found"));
        model.addAttribute("diagnosis", diagnosis);
        return "diagnoses/details";
    }

    // редактирование диагноза
    @GetMapping("/{id}/edit")
    public String editDiagnosis(@PathVariable Long id, Model model) {
        Diagnosis diagnosis = diagnosisService.findById(id).orElseThrow(() -> new RuntimeException("Diagnosis not found"));
        model.addAttribute("diagnosis", diagnosis);
        return "diagnoses/edit";
    }

    // сохранение редактирования диагноза
    @PostMapping("/{id}")
    public String updateDiagnosis(@PathVariable Long id, @ModelAttribute("diagnosis") Diagnosis diagnosis) {
        if (diagnosis.getImpact() == null || diagnosis.getImpact().trim().isEmpty()) {
            diagnosis.setImpact("1");
        }
        diagnosisService.save(diagnosis);
        return "redirect:/diagnoses";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDiagnosis(@PathVariable Long id) {
        log.info("Deleting diagnosis with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            diagnosisService.deleteById(id);
            log.info("Diagnosis deleted successfully");
            response.put("message", "Диагноз успешно удалён");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting diagnosis: {}", e.getMessage(), e);
            response.put("message", "Ошибка при удалении диагноза: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}