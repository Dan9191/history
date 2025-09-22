package dan.competition.history.service;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.model.DiagnosisDTO;
import dan.competition.history.repository.DiagnosisRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    public List<Diagnosis> findAll() {
        return diagnosisRepository.findAll();
    }

    public Optional<Diagnosis> findById(Long id) {
        return diagnosisRepository.findById(id);
    }

    public List<Diagnosis> findByIds(List<Long> ids) {
        return diagnosisRepository.findAllById(ids);
    }

    public void save(Diagnosis diagnosis) {
        if (diagnosis.getImpact() == null || diagnosis.getImpact().trim().isEmpty()) {
            diagnosis.setImpact("1");
        }
        diagnosisRepository.save(diagnosis);
    }

    public void deleteById(Long id) {
        diagnosisRepository.deleteById(id);
    }

    public void createDiagnosis(@Valid DiagnosisDTO diagnosisDto) {
        Diagnosis diagnosis = new Diagnosis();
        Optional.ofNullable(diagnosisDto.getName()).ifPresent(diagnosis::setName);
        Optional.ofNullable(diagnosisDto.getDescription()).ifPresent(diagnosis::setDescription);
        Optional.ofNullable(diagnosisDto.getImpact()).ifPresent(diagnosis::setImpact);
        if (diagnosis.getImpact() == null || diagnosis.getImpact().trim().isEmpty()) {
            diagnosis.setImpact("1");
        }
        diagnosisRepository.save(diagnosis);
    }
}
