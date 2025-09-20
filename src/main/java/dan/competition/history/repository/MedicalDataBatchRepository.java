package dan.competition.history.repository;

import dan.competition.history.entity.MedicalDataBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalDataBatchRepository extends JpaRepository<MedicalDataBatch, Long> {
}
