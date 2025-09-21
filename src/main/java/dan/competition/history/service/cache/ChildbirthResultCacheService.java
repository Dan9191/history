package dan.competition.history.service.cache;

import dan.competition.history.entity.ChildbirthResult;
import dan.competition.history.repository.ChildbirthResultRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий справочные значения по результату родов.
 */
@Service
@RequiredArgsConstructor
public class ChildbirthResultCacheService {

    private final ChildbirthResultRepository childbirthResultRepository;

    private Map<Integer, ChildbirthResult> childbirthResultMap;

    @PostConstruct
    void init() {
        childbirthResultMap = childbirthResultRepository.findAll().stream()
                .collect(Collectors.toMap(ChildbirthResult::getId, Function.identity()));
    }

    public ChildbirthResult findById(int id) {
        return childbirthResultMap.get(id);
    }
}
