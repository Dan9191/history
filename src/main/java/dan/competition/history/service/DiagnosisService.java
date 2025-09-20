package dan.competition.history.service;

import dan.competition.history.entity.Diagnosis;
import dan.competition.history.repository.DiagnosisRepository;
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

    public void save(Diagnosis diagnosis) {
        if (diagnosis.getImpact() == null || diagnosis.getImpact().trim().isEmpty()) {
            diagnosis.setImpact("1");
        }
        diagnosisRepository.save(diagnosis);
    }

    public void deleteById(Long id) {
        diagnosisRepository.deleteById(id);
    }
}
