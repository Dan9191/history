package dan.competition.history.model.view;

import dan.competition.history.entity.MedicalData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalDataView {
    private Long id;
    private double timeSec;
    private double uterus;
    private double bpm;

    public MedicalDataView(MedicalData medicalData) {
        this.id = medicalData.getId();
        this.timeSec = medicalData.getTimeSec();
        this.uterus = medicalData.getUterus();
        this.bpm = medicalData.getBpm();
    }
}
