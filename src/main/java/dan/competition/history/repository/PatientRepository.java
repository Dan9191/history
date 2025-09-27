package dan.competition.history.repository;

import dan.competition.history.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT new map("
            + "p.id as id,"
            + " p.name as name,"
            + " p.age as age,"
            + " p.ph as ph,"
            + " p.co2 as co2,"
            + " p.glu as glu,"
            + " p.lac as lac,"
            + " p.be as be,"
            + " p.childbirthResult.id as childbirthResultId) "
            + "FROM Patient p WHERE p.id = :id")
    Optional<Map<String, Object>> findPatientMapById(@Param("id") Long id);
}
