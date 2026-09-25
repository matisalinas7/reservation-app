# Usamos una imagen oficial de Java 21
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el jar compilado al contenedor
# Asumimos que primero corres ./mvnw clean package
COPY target/reservation-backend-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto
EXPOSE 8081

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]