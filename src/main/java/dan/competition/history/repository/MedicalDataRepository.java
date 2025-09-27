package dan.competition.history.repository;

import dan.competition.history.entity.MedicalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalDataRepository extends JpaRepository<MedicalData, Long>, MedicalDataJdbcRepository {
    List<MedicalData> findByMedicalDataBatchIdOrderByTimeSec(Long batchId);
}
