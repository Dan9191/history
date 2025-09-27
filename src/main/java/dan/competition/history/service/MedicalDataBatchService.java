package dan.competition.history.service;

import dan.competition.history.entity.MedicalData;
import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.repository.MedicalDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalDataBatchService {

    private final MedicalDataRepository medicalRepository;

    public MedicalDataBatch getBatchByPatientIdAndBatchId(String batchName, Long batchId) {
        List<MedicalData> medicalDataList = medicalRepository.findByMedicalDataBatchIdOrderByTimeSec(batchId);
        MedicalDataBatch medicalDataBatch = new MedicalDataBatch();
        medicalDataBatch.setId(batchId);
        medicalDataBatch.setName(batchName);
        medicalDataBatch.setMedicalDataList(medicalDataList);

        return medicalDataBatch;
    }
}
