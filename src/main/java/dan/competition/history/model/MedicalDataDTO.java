package dan.competition.history.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalDataDTO {
    private Long id;
    private double timeSec;
    private double uterus;
    private double bpm;
}
