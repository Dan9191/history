package dan.competition.history.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "patients")
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "childbirth_result_id", nullable = false)
    private ChildbirthResult childbirthResult;

    @ManyToMany
    @JoinTable(
            name = "patient_diagnoses",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "diagnosis_id")
    )
    private List<Diagnosis> diagnoses;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalDataBatch> medicalDataBatches;

}
