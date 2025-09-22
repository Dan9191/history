# образ java
FROM eclipse-temurin:21-jdk-alpine

# рабочая директория в контейнере
WORKDIR /app

# копирование файлов
COPY build/libs/*.jar app.jar

# дефолтный порт
EXPOSE 8097

# команда для запуска
ENTRYPOINT ["java","-jar","app.jar"]
