# Stage 1: Build
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Копіюємо конфігурацію Maven
COPY pom.xml .

# Створюємо правильну структуру директорій
RUN mkdir -p src/main/java/com/lab4

# Копіюємо всі Java-файли
COPY *.java src/main/java/com/lab4/

# Збираємо проєкт
RUN mvn clean package -DskipTests

# Stage 2: Run (Використовуємо сучасний Eclipse Temurin)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Забираємо готовий jar-файл з першого етапу
COPY --from=build /app/target/streams-app-1.0-SNAPSHOT.jar app.jar

CMD ["sh", "-c", "echo 'Waiting for Kafka to be ready...' && sleep 30 && java -jar app.jar"]
