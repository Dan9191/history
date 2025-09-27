package dan.competition.history.model.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalDataView {
    private Long id;
    private double timeSec;
    private double uterus;
    private double bpm;
}
