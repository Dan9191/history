package dan.competition.history.service;


import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalDataBatchService {

    private final PatientService patientService;

    public MedicalDataBatch getBatchByPatientIdAndBatchId(Long patientId, Long batchId) {
        Patient patient = patientService.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        return patient.getMedicalDataBatches().stream()
                .filter(b -> b.getId().equals(batchId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }
}
