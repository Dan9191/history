package dan.competition.history.service;

import dan.competition.history.entity.MedicalData;
import dan.competition.history.model.websocket.MedicalDataWebSocket;
import dan.competition.history.model.websocket.PatientDataWebSocket;
import dan.competition.history.repository.MedicalDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSenderService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MedicalDataRepository medicalDataRepository;
    private final PatientService patientService;

    @Async
    public synchronized void sendBatchToDevice(Long patientId, Long batchId) {
        log.info("Starting batch sending for patientId: {}, batchId: {}", patientId, batchId);

        // 1. Отправляем PatientData с status=true
        PatientDataWebSocket patientData = patientService.getPatientDataWebSocket(patientId, true);
        messagingTemplate.convertAndSend("/topic/patient", patientData);
        log.info("Sent PatientData with status=true: {}", patientData);

        // 2. Ждем 10 секунд
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("Interrupted during initial delay: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return;
        }

        // 3. Извлекаем MedicalData из БД
        List<MedicalData> medicalDataList = medicalDataRepository.findByMedicalDataBatchIdOrderByTimeSec(batchId);
        if (medicalDataList.isEmpty()) {
            log.warn("No MedicalData found for batchId: {}", batchId);
            sendPatientDataWithStatusFalse(patientData);
            return;
        }

        // 4. Эмуляция отправки в реальном времени
        double currentSec = medicalDataList.getFirst().getTimeSec();
        for (MedicalData entity : medicalDataList) {
            // Конвертируем JPA-сущность в модель для WebSocket
            MedicalDataWebSocket medicalDataWebSocket = new MedicalDataWebSocket(
                    entity.getTimeSec(),
                    entity.getUterus(),
                    entity.getBpm()
            );

            // Ждем, пока currentSec не достигнет timeSec текущего MedicalData
            while (currentSec < medicalDataWebSocket.getTimeSec()) {
                try {
                    Thread.sleep(100); // Увеличиваем время каждые 100 мс
                    currentSec += 0.1; // Увеличиваем на 0.1 секунды
                } catch (InterruptedException e) {
                    log.error("Interrupted during MedicalData sending: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Отправляем MedicalData
            messagingTemplate.convertAndSend("/topic/data", medicalDataWebSocket);
            log.info("Sent MedicalData: {}", medicalDataWebSocket);
        }

        // 6. Ждем 5 секунд после последнего medicalDataWebSocket
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("Interrupted during final delay: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return;
        }

        // 7. Отправляем PatientData с status=false
        sendPatientDataWithStatusFalse(patientData);
    }

    private void sendPatientDataWithStatusFalse(PatientDataWebSocket patientData) {
        patientData.setStatus(false);
        messagingTemplate.convertAndSend("/topic/patient", patientData);
        log.info("Sent PatientData with status=false: {}", patientData);
    }
}
