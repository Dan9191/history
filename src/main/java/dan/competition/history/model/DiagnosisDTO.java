package dan.competition.history.model;

import dan.competition.history.entity.Diagnosis;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class DiagnosisDTO {
    private Long id;
    private String name;
    private String description;
    private String impact;

    public static Diagnosis mapToDiagnosis(DiagnosisDTO diagnosisDTO) {
        Diagnosis diagnosis = new Diagnosis();
        Optional.ofNullable(diagnosisDTO.getId()).ifPresent(diagnosis::setId);
        Optional.ofNullable(diagnosisDTO.getName()).ifPresent(diagnosis::setName);
        Optional.ofNullable(diagnosisDTO.getDescription()).ifPresent(diagnosis::setDescription);
        Optional.ofNullable(diagnosisDTO.getImpact()).ifPresent(diagnosis::setImpact);
        return diagnosis;
    }
}