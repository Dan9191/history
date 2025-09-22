package dan.competition.history.controller;

import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.model.PatientCreateDTO;
import dan.competition.history.model.PatientViewDTO;
import dan.competition.history.repository.PatientRepository;
import dan.competition.history.service.DiagnosisService;
import dan.competition.history.service.MedicalDataBatchService;
import dan.competition.history.service.PatientService;
import dan.competition.history.util.PDFCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final MedicalDataBatchService medicalDataBatchService;
    private final PatientRepository patientRepository;

    // Список пациентов
    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("newPatient", new PatientCreateDTO());
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "patients/list";
    }

    // Создание нового пациента
    @PostMapping
    public String createPatient(@Valid @ModelAttribute("newPatient") PatientCreateDTO patientDto,
                                BindingResult result,
                                @RequestParam("zipFile") MultipartFile zipFile,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            model.addAttribute("errorMessage", "Пожалуйста, исправьте ошибки в форме");
            log.warn(result.getAllErrors().toString());
            return "patients/list";
        }
        try {
            if (!zipFile.isEmpty()) {
                log.info("Processing patient with zip file");
                patientService.createPatientWithZipFile(patientDto, zipFile);
            } else {
                log.info("Processing patient without zip file");
                patientService.createPatient(patientDto);
            }
            log.info("Patient created successfully");
            return "redirect:/patients";
        } catch (Exception e) {
            log.error("Error creating patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при создании пациента: " + e.getMessage());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/list";
        }
    }

    // Детальная страница пациента
    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        log.info("Viewing patient with ID: {}", id);
        try {
            PatientViewDTO patientViewDTO = patientService.findByIdAsViewDTO(id);
            model.addAttribute("patient", patientViewDTO);
            return "patients/details";
        } catch (Exception e) {
            log.error("Error viewing patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при просмотре пациента: " + e.getMessage());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPatient(@PathVariable Long id, Model model) {
        log.info("Editing patient with ID: {}", id);
        try {
            PatientCreateDTO patientDTO = patientService.findByIdAsDTO(id);
            model.addAttribute("patient", patientDTO);
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/edit";
        } catch (Exception e) {
            log.error("Error editing patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при редактировании пациента: " + e.getMessage());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/list";
        }
    }

    // Сохранение изменений пациента
    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id, @Valid @ModelAttribute("patient") PatientCreateDTO patientDTO, BindingResult result, Model model) {
        log.info("Updating patient with ID: {}", id);
        if (result.hasErrors()) {
            log.warn("Validation errors: {}", result.getAllErrors());
            model.addAttribute("errorMessage", "Пожалуйста, исправьте ошибки в форме");
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/edit";
        }
        try {
            patientService.updatePatient(id, patientDTO);
            log.info("Patient updated successfully");
            return "redirect:/patients";
        } catch (Exception e) {
            log.error("Error updating patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при обновлении пациента: " + e.getMessage());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "patients/edit";
        }
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
