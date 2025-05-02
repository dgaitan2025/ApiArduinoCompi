# Imagen base oficial con Java 17 (o cámbiala por java 20 si lo necesitas)
FROM eclipse-temurin:17-jdk-alpine

# Directorio dentro del contenedor
WORKDIR /app

# Copiar archivos jar al contenedor
COPY build/libs/apiarduino.jar app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
