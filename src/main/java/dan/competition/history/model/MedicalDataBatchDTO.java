package dan.competition.history.model;

import dan.competition.history.model.view.MedicalDataView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MedicalDataBatchDTO {
    private Long id;
    private String name;
    private List<MedicalDataView> medicalDataList;
}
