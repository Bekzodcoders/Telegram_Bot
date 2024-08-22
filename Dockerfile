FROM openjdk:22-jdk
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests





CMD ["java", "-jar", "target/Telegram_Bot.jar"]
