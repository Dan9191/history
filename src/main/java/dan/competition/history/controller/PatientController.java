package dan.competition.history.controller;

import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.entity.Patient;
import dan.competition.history.model.PatientDTO;
import dan.competition.history.repository.PatientRepository;
import dan.competition.history.service.DiagnosisService;
import dan.competition.history.service.MedicalDataBatchService;
import dan.competition.history.service.PatientService;
import dan.competition.history.util.PDFCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final MedicalDataBatchService medicalDataBatchService;
    private final PatientRepository patientRepository;

    // Список пациентов
    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("newPatient", new PatientDTO());
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "patients/list";
    }

    // Создание нового пациента
    @PostMapping
    public String createPatient(@Valid @ModelAttribute("newPatient") PatientDTO patientDto,
                                BindingResult result,
                                @RequestParam("zipFile") MultipartFile zipFile,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/list";
        }
        if (!zipFile.isEmpty()) {
            patientService.createPatientWithZipFile(patientDto, zipFile);
        } else {
            patientService.createPatient(patientDto);
        }
        return "redirect:/patients";
    }

    // Детальная страница пациента
    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        PatientDTO patientDTO = patientService.findByIdAsDTO(id);
        model.addAttribute("patient", patientDTO);
        return "patients/details";
    }


    // Страница редактирования пациента
    @GetMapping("/{id}/edit")
    public String editPatient(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("patient", patient);
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "patients/edit";
    }

    // Сохранение изменений пациента
    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patient") Patient patient,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/edit";
        }
        patient.setId(id);
        patientService.save(patient);
        return "redirect:/patients";
    }

    // Экспорт батча в PDF
    @GetMapping("/{patientId}/batches/{batchId}/pdf")
    public ResponseEntity<byte[]> exportBatchToPdf(@PathVariable Long patientId, @PathVariable Long batchId) {
        MedicalDataBatch batch = medicalDataBatchService.getBatchByPatientIdAndBatchId(patientId, batchId);

        byte[] contents = PDFCreator.createPDF(batch);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "batch_" + batch.getName() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }
}
//todo графики выводить согласно очереди