package dan.competition.history.repository;

import dan.competition.history.entity.MedicalData;

import java.util.List;

public interface MedicalDataJdbcRepository {

    /**
     * Пакетное сохранение глоссария.
     *
     * @param medicalDataList список глоссария
     */
    void saveBatch(List<MedicalData> medicalDataList, Long batchId);
}
