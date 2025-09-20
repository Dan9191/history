package dan.competition.history.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private List<DiagnosisDTO> diagnoses;
    private List<MedicalDataBatchDTO> medicalDataBatches;
}
