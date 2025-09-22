package dan.competition.history.repository;

import dan.competition.history.entity.MedicalData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalDataJdbcRepositoryImpl implements MedicalDataJdbcRepository {

    /** Сохранение записи. */
    private static final String INSERT_MEDICAL_DATA = "insert into patients_history.medical_data("
            + "time_sec, uterus, bpm, medical_data_batch_id"
            + ") values (?, ?, ?, ?) returning id";

    /**
     * Jdbc-шаблон.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Пакетное сохранение глоссария.
     *
     * @param medicalDataList список глоссария
     */
    @Override
    public void saveBatch(List<MedicalData> medicalDataList, Long batchId) {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            PreparedStatement ps = connection.prepareStatement(INSERT_MEDICAL_DATA, Statement.RETURN_GENERATED_KEYS);

            for (MedicalData data : medicalDataList) {
                ps.setObject(1, data.getTimeSec(), Types.DECIMAL);
                ps.setObject(2, data.getUterus(), Types.DECIMAL);
                ps.setObject(3, data.getBpm(), Types.DECIMAL);
                ps.setObject(4, batchId, Types.BIGINT);
                ps.addBatch();
            }
            ps.executeBatch();

            Iterator<MedicalData> iterator = medicalDataList.iterator();
            ResultSet keys = ps.getGeneratedKeys();

            // updating ids
            while (keys.next()) {
                long id = keys.getLong(1);
                MedicalData next = iterator.next();
                next.setId(id);
            }

        } catch (SQLException e) {
            log.error("medicalData saving error", e);
            throw new RuntimeException("medicalData saving error", e);
        }
    }


}
