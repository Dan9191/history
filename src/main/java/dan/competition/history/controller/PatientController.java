package dan.competition.history.controller;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.model.view.PatientCreateView;
import dan.competition.history.model.view.PatientShortView;
import dan.competition.history.model.view.PatientView;
import dan.competition.history.service.DataSenderService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final MedicalDataBatchService medicalDataBatchService;

    private final DataSenderService dataSenderService;

    // Список пациентов
    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients",
                patientService.findAllShortDto().stream().sorted(Comparator.comparing(PatientShortView::getName)).toList());
        model.addAttribute("newPatient", new PatientCreateView());
        model.addAttribute("diagnoses",
                diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
        return "patients/list";
    }

    // Создание нового пациента
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @ModelAttribute("newPatient") PatientCreateView patientDto,
                                                             BindingResult result,
                                                             @RequestParam("zipFile") MultipartFile zipFile) {
        log.info("Creating patient with DTO: {}", patientDto);
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            log.warn("Validation errors: {}", result.getAllErrors());
            response.put("message", "Пожалуйста, исправьте ошибки в форме: " + result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
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
            response.put("message", "Пациент успешно создан");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating patient: {}", e.getMessage(), e);
            response.put("message", "Ошибка при создании пациента: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Детальная страница пациента
    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        log.info("Viewing patient with ID: {}", id);
        try {
            PatientView patientView = patientService.findByIdAsViewDTO(id);
            model.addAttribute("patient", patientView);
            return "patients/details";
        } catch (Exception e) {
            log.error("Error viewing patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при просмотре пациента: " + e.getMessage());
            model.addAttribute(
                    "patients",
                    patientService.findAllShortDto().stream()
                            .sorted(Comparator.comparing(PatientShortView::getName)).toList()
            );
            model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
            return "patients/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPatient(@PathVariable Long id, Model model) {
        log.info("Editing patient with ID: {}", id);
        try {
            PatientCreateView patientDTO = patientService.findByIdAsDTO(id);
            model.addAttribute("patient", patientDTO);
            model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
            return "patients/edit";
        } catch (Exception e) {
            log.error("Error editing patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при редактировании пациента: " + e.getMessage());
            model.addAttribute("patients", patientService.findAllShortDto().stream().sorted(Comparator.comparing(PatientShortView::getName)).toList());
            model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
            return "patients/list";
        }
    }

    // Сохранение изменений пациента
    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id, @Valid @ModelAttribute("patient") PatientCreateView patientDTO, BindingResult result, Model model) {
        log.info("Updating patient with ID: {}", id);
        if (result.hasErrors()) {
            log.warn("Validation errors: {}", result.getAllErrors());
            model.addAttribute("errorMessage", "Пожалуйста, исправьте ошибки в форме");
            model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
            return "patients/edit";
        }
        try {
            patientService.updatePatient(id, patientDTO);
            log.info("Patient updated successfully");
            return "redirect:/patients";
        } catch (Exception e) {
            log.error("Error updating patient: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Ошибка при обновлении пациента: " + e.getMessage());
            model.addAttribute("diagnoses", diagnosisService.findAll().stream().sorted(Comparator.comparing(Diagnosis::getName)).toList());
            return "patients/edit";
        }
    }

    // Экспорт батча в PDF
    @GetMapping("/{batchName}/batches/{batchId}/pdf")
    public ResponseEntity<byte[]> exportBatchToPdf(@PathVariable String batchName, @PathVariable Long batchId) {
        MedicalDataBatch batch = medicalDataBatchService.getBatchByPatientIdAndBatchId(batchName, batchId);

        byte[] contents = PDFCreator.createPDF(batch);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "batch_" + batch.getName() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    // удаление пациента
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePatient(@PathVariable Long id) {
        log.info("Deleting patient with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            patientService.deleteById(id);
            log.info("Patient deleted successfully");
            response.put("message", "Пациент успешно удалён");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting patient: {}", e.getMessage(), e);
            response.put("message", "Ошибка при удалении пациента: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/sendBatch/{patientId}/{batchId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendBatchToDevice(@PathVariable Long patientId, @PathVariable Long batchId) {
        log.info("Initiating async batch send for patientId: {}, batchId: {}", patientId, batchId);
        Map<String, Object> response = new HashMap<>();

        // Запускаем асинхронную задачу
        CompletableFuture.runAsync(() -> {
            try {
                dataSenderService.sendBatchToDevice(patientId, batchId);
                log.info("Batch sent successfully for patientId: {}, batchId: {}", patientId, batchId);
            } catch (Exception e) {
                log.error("Error sending batch for patientId: {}, batchId: {}, error: {}",
                        patientId, batchId, e.getMessage(), e);
            }
        });

        // Возвращаем немедленный ответ
        response.put("message", "Запрос на отправку батча принят");
        return ResponseEntity.ok(response);
    }
}
