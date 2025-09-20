package dan.competition.history.controller;

import dan.competition.history.entity.Patient;
import dan.competition.history.service.DiagnosisService;
import dan.competition.history.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final DiagnosisService diagnosisService;

    public PatientController(PatientService patientService, DiagnosisService diagnosisService) {
        this.patientService = patientService;
        this.diagnosisService = diagnosisService;
    }

    // Список пациентов
    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("newPatient", new Patient());
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "patients/list";
    }

    // Создание нового пациента
    @PostMapping
    public String createPatient(@ModelAttribute("newPatient") Patient patient,
                                @RequestParam("zipFile") MultipartFile zipFile) throws IOException {
        if (!zipFile.isEmpty()) {
            patientService.saveWithZipFile(patient, zipFile);
        } else {
            patientService.save(patient);
        }
        return "redirect:/patients";
    }

    // Детальная страница пациента
    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("patient", patient);
        return "patients/details";
    }
}