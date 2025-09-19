package dan.competition.history.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "medical_data")
@Data
public class MedicalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_sec")
    private double timeSec;

    private double uterus;

    private double bpm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_data_batch_id", nullable = false)
    private MedicalDataBatch medicalDataBatch;

}
