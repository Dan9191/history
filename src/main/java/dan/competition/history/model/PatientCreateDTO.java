package dan.competition.history.model;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.entity.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCreateDTO {
    private Long id;
    @NotBlank(message = "Имя пациента обязательно")
    private String name;

    @NotEmpty(message = "Необходимо выбрать хотя бы один диагноз")
    private List<Long> diagnosesIds;
    private List<MedicalDataBatchDTO> medicalDataBatches;
    private Integer age;
    private Float ph;
    private Float co2;
    private Float glu;
    private Float lac;
    private Float be;
    @NotNull(message = "Результат родов обязателен")
    private Integer childbirthResultId;

    public PatientCreateDTO(Patient patient) {
        List<Long> diagnosisDTOs = patient.getDiagnoses().stream()
                .map(Diagnosis::getId)
                .toList();
        List<MedicalDataBatchDTO> batchDTOs = patient.getMedicalDataBatches().stream()
                .map(b -> new MedicalDataBatchDTO(
                        b.getId(),
                        b.getName(),
                        b.getMedicalDataList().stream()
                                .map(d -> new MedicalDataDTO(d.getId(), d.getTimeSec(), d.getUterus(), d.getBpm()))
                                .toList()
                ))
                .sorted(Comparator.comparing(MedicalDataBatchDTO::getName))
                .toList();
        this.id = patient.getId();
        this.name = patient.getName();
        this.diagnosesIds = diagnosisDTOs;
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
