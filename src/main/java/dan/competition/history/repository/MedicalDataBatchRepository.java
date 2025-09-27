package dan.competition.history.repository;

import dan.competition.history.entity.MedicalDataBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalDataBatchRepository extends JpaRepository<MedicalDataBatch, Long> {


    @Query("SELECT m.id, m.name FROM MedicalDataBatch m WHERE m.patient.id = :patientId ORDER BY m.name")
    List<Object[]> findIdAndNameByPatientIdSimpleOrderByNameAsc(@Param("patientId") Long patientId);
}
