package dan.competition.history.controller;

import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.entity.Patient;
import dan.competition.history.model.PatientDTO;
import dan.competition.history.service.DiagnosisService;
import dan.competition.history.service.PatientService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
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
        PatientDTO patientDTO = patientService.findByIdAsDTO(id);
        model.addAttribute("patient", patientDTO);
        return "patients/details";
    }

    // Экспорт батча в PDF
    @GetMapping("/{patientId}/batches/{batchId}/pdf")
    public ResponseEntity<byte[]> exportBatchToPdf(@PathVariable Long patientId, @PathVariable Long batchId) {
        Patient patient = patientService.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        MedicalDataBatch batch = patient.getMedicalDataBatches().stream()
                .filter(b -> b.getId().equals(batchId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Phrase("Medical Data Batch: " + batch.getName() + "\n\n", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2});
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Time (sec)", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Uterus", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("BPM", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (var data : batch.getMedicalDataList()) {
                table.addCell(new Phrase(String.format("%.6f", data.getTimeSec()), cellFont));
                table.addCell(new Phrase(data.getUterus() != 0.0 ? String.format("%.6f", data.getUterus()) : "N/A", cellFont));
                table.addCell(new Phrase(data.getBpm() != 0.0 ? String.format("%.6f", data.getBpm()) : "N/A", cellFont));
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        byte[] contents = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "batch_" + batch.getName() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }
}