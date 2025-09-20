package dan.competition.history.repository;

import dan.competition.history.entity.MedicalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalDataRepository extends JpaRepository<MedicalData, Long> {
}
