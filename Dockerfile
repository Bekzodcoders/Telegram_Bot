FROM openjdk:17-jdk-slim

COPY . /app

WORKDIR /app

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "Telegram_Bot.jar"]
