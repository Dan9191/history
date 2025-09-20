package dan.competition.history.service;

import dan.competition.history.entity.MedicalData;
import dan.competition.history.entity.MedicalDataBatch;
import dan.competition.history.entity.Patient;
import dan.competition.history.model.DiagnosisDTO;
import dan.competition.history.model.MedicalDataBatchDTO;
import dan.competition.history.model.MedicalDataDTO;
import dan.competition.history.model.PatientDTO;
import dan.competition.history.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public void save(Patient patient) {
        patientRepository.save(patient);
    }

    public PatientDTO findByIdAsDTO(Long id) {
        Patient patient = findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        List<DiagnosisDTO> diagnosisDTOs = patient.getDiagnoses().stream()
                .map(d -> new DiagnosisDTO(d.getId(), d.getName(), d.getImpact()))
                .toList();
        List<MedicalDataBatchDTO> batchDTOs = patient.getMedicalDataBatches().stream()
                .map(b -> new MedicalDataBatchDTO(
                        b.getId(),
                        b.getName(),
                        b.getMedicalDataList().stream()
                                .map(d -> new MedicalDataDTO(d.getId(), d.getTimeSec(), d.getUterus(), d.getBpm()))
                                .toList()
                ))
                .toList();
        return new PatientDTO(patient.getId(), diagnosisDTOs, batchDTOs);
    }

    public void saveWithZipFile(Patient patient, MultipartFile zipFile) throws IOException {
        // Группировка файлов по префиксу из ZIP-архива
        Map<String, Map<String, String>> groups = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String path = entry.getName();
                String filename = path.substring(path.lastIndexOf('/') + 1);
                String prefix = null;
                String type = null;
                if (path.startsWith("bpm/") && filename.endsWith("_1.csv")) {
                    prefix = filename.substring(0, filename.length() - 6);
                    type = "bpm";
                } else if (path.startsWith("uterus/") && filename.endsWith("_2.csv")) {
                    prefix = filename.substring(0, filename.length() - 6);
                    type = "uterus";
                }
                if (prefix != null && type != null) {
                    String content = readZipEntry(zis);
                    groups.computeIfAbsent(prefix, k -> new HashMap<>()).put(type, content);
                }
                zis.closeEntry();
            }
        }

        // Для каждого префикса создать батч
        for (String prefix : groups.keySet()) {
            Map<String, String> group = groups.get(prefix);
            MedicalDataBatch batch = new MedicalDataBatch();
            batch.setName(prefix);
            batch.setPatient(patient);
            if (patient.getMedicalDataBatches() == null) {
                patient.setMedicalDataBatches(new ArrayList<>());
            }
            patient.getMedicalDataBatches().add(batch);

            // Парсинг данных
            Map<Double, Double> bpmMap = new HashMap<>();
            if (group.containsKey("bpm")) {
                parseCsvContent(group.get("bpm"), bpmMap);
            }
            Map<Double, Double> uterusMap = new HashMap<>();
            if (group.containsKey("uterus")) {
                parseCsvContent(group.get("uterus"), uterusMap);
            }

            // Объединение времен
            Set<Double> allTimes = new HashSet<>(bpmMap.keySet());
            allTimes.addAll(uterusMap.keySet());
            List<Double> sortedTimes = new ArrayList<>(allTimes);
            Collections.sort(sortedTimes);

            // Создание MedicalData
            List<MedicalData> dataList = new ArrayList<>();
            for (Double time : sortedTimes) {
                MedicalData data = new MedicalData();
                data.setTimeSec(time);
                data.setBpm(bpmMap.getOrDefault(time, 0.0));
                data.setUterus(uterusMap.getOrDefault(time, 0.0));
                data.setMedicalDataBatch(batch);
                dataList.add(data);
            }
            batch.setMedicalDataList(dataList);
        }

        // Сохранение пациента (каскадно сохранит батчи и данные)
        patientRepository.save(patient);
    }

    private String readZipEntry(ZipInputStream zis) throws IOException {
        StringBuilder content = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            content.append(new String(buffer, 0, len));
        }
        return content.toString();
    }

    private void parseCsvContent(String content, Map<Double, Double> map) {
        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i++) { // Пропуск заголовка
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            double time = Double.parseDouble(parts[0].trim());
            double value = Double.parseDouble(parts[1].trim());
            map.put(time, value);
        }
    }
}