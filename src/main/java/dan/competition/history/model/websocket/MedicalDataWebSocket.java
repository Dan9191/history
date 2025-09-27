package dan.competition.history.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDataWebSocket {
    private double timeSec;
    private double uterus;
    private double bpm;
}
