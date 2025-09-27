package dan.competition.history.model.view;

import dan.competition.history.entity.Patient;
import dan.competition.history.model.DiagnosisDTO;
import dan.competition.history.model.MedicalDataBatchDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientView {
    private Long id;
    private String name;
    private List<DiagnosisDTO> diagnoses;
    private List<MedicalDataBatchDTO> medicalDataBatches;
    private Integer age;
    private Float ph;
    private Float co2;
    private Float glu;
    private Float lac;
    private Float be;
    private Integer childbirthResultId;

    public PatientView(Patient patient) {
        List<DiagnosisDTO> diagnosisDTOs = patient.getDiagnoses().stream()
                .map(d -> new DiagnosisDTO(d.getId(), d.getName(), d.getDescription(), d.getImpact()))
                .toList();
        List<MedicalDataBatchDTO> batchDTOs = patient.getMedicalDataBatches().stream()
                .map(b -> new MedicalDataBatchDTO(
                        b.getId(),
                        b.getName(),
                        b.getMedicalDataList().stream()
                                .map(d -> new MedicalDataView(d.getId(), d.getTimeSec(), d.getUterus(), d.getBpm()))
                                .toList()
                ))
                .sorted(Comparator.comparing(MedicalDataBatchDTO::getName))
                .toList();
        this.id = patient.getId();
        this.name = patient.getName();
        this.diagnoses = diagnosisDTOs;
        this.medicalDataBatches = batchDTOs;
        this.age = patient.getAge();
        this.ph = patient.getPh();
        this.co2 = patient.getCo2();
        this.glu = patient.getGlu();
        this.lac = patient.getLac();
        this.be = patient.getBe();
        this.childbirthResultId = patient.getChildbirthResult().getId();
    }
}
