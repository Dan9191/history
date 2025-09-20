package dan.competition.history.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiagnosisDTO {
    private Long id;
    private String name;
    private String impact;
}