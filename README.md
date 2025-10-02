### Сервис-хранилище данных.

## Описание

Сервис для создания, хранения и отправки данных

## Сборка

Требуется Java 21

сборка
```shell script
./gradlew clean build
```

запуск
```shell script
./gradlew bootRun
```


## Конфигурация

- `src/main/resources/application.properties` - все настройки приложения по-умолчанию

### Описание основных переменных окружения:

| Переменная               | Значение переменной (по-умолчанию)                                   | Описание                            |
|--------------------------|----------------------------------------------------------------------|-------------------------------------|
| PATIENT_SERVER_PORT      | 8097                                                                 | Порт, на котором поднимается сервис |
| PATIENT_DATASOURCE_URL   | jdbc:postgresql://localhost:5432/test?currentSchema=patients_history | Урл схемы в БД                      |
| PATIENT_USER_NAME        | test                                                                 | Пользователь бд                     |
| PATIENT_USER_PASSWORD    | test                                                                 | Пароль пользователя БД              |
| PATIENT_LOGIN            | admin                                                                | Логин                               |
| PATIENT_PASSWORD         | secret123                                                            | Пароль                              |
