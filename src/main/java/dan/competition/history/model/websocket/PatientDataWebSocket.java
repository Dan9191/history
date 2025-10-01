package dan.competition.history.model.websocket;

import dan.competition.history.model.DiagnosisDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientDataWebSocket {

    private Long id;

    private String name;

    private Integer age;

    /**
     * Кислотно-щелочной баланс.
     */
    private Float ph;

    /**
     * Давление углекислого газа, растворенного в артериальной крови.
     */
    private Float co2;

    /**
     * Уровень сахара в крови.
     */
    private Float glu;

    /**
     * Продукт анаэробного (бескислородного) метаболизма глюкозы.
     */
    private Float lac;

    /**
     * Показатель метаболического компонента регуляции pH.
     */
    private Float be;

    private List<DiagnosisDTO> diagnoses;

    private Boolean status;
}