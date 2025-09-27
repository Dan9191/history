package dan.competition.history.model.view;

import dan.competition.history.entity.Patient;
import dan.competition.history.model.DiagnosisDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientShortView {
    private Long id;
    private String name;
    private List<DiagnosisDTO> diagnoses;
    private Integer medicalDataBatchSize;
    private Integer age;
    private Float ph;
    private Float co2;
    private Float glu;
    private Float lac;
    private Float be;
    private Integer childbirthResultId;

    public PatientShortView(Patient patient) {
        List<DiagnosisDTO> diagnosisDTOs = patient.getDiagnoses().stream()
                .map(d -> new DiagnosisDTO(d.getId(), d.getName(), d.getDescription(), d.getImpact()))
                .toList();
        this.id = patient.getId();
        this.name = patient.getName();
        this.diagnoses = diagnosisDTOs;
        this.medicalDataBatchSize = patient.getMedicalDataBatches().size();
        this.age = patient.getAge();
        this.ph = patient.getPh();
        this.co2 = patient.getCo2();
        this.glu = patient.getGlu();
        this.lac = patient.getLac();
        this.be = patient.getBe();
        this.childbirthResultId = patient.getChildbirthResult().getId();
    }
}
