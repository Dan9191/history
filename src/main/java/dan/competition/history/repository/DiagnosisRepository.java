package dan.competition.history.repository;

import dan.competition.history.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {


    @Query(value = "SELECT d.* FROM diagnoses d " +
            "INNER JOIN patient_diagnoses pd ON d.id = pd.diagnosis_id " +
            "WHERE pd.patient_id = :patientId", nativeQuery = true)
    List<Diagnosis> findDiagnosesByPatientId(@Param("patientId") Long patientId);
}
