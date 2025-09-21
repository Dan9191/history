package dan.competition.history.model;

import lombok.Getter;

import java.util.Arrays;

/**
 * Результат родов.
 */
@Getter
public enum ChildbirthResultEnum {
    REGULAR(1, "Обычные роды"),
    HYPOXIA(2, "Гипоксия");

    private final int id;
    private final String label;

    ChildbirthResultEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static ChildbirthResultEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(result -> result.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID: " + id));
    }
}
