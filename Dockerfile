# ========================================
# ETAPA 1: BUILD (Compilación)
# ========================================
# Usar imagen con Gradle y JDK ya instalados
FROM gradle:8.5-jdk17-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle primero (para aprovechar caché de Docker)
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Descargar dependencias (se cachea si no cambian los archivos de configuración)
RUN gradle dependencies --no-daemon || true

# Copiar el resto del código fuente
COPY src src

# Compilar y generar el JAR ejecutable
# bootJar: tarea de Spring Boot que genera un "fat JAR" con todas las dependencias
# --no-daemon: no usar proceso Gradle en segundo plano (mejor para Docker)
# -x test: omitir tests durante el build (más rápido, tests se ejecutan en CI/CD)
RUN gradle bootJar --no-daemon -x test

# ========================================
# ETAPA 2: RUNTIME (Ejecución)
# ========================================
# Imagen base con SOLO el runtime de Java (sin herramientas de compilación)
# Amazon Corretto es una distribución OpenJDK oficial mantenida por AWS
# Esto reduce el tamaño de la imagen final de ~500MB a ~200MB
FROM amazoncorretto:17-alpine

# Documentar que la aplicación escucha en el puerto 8080
# IMPORTANTE: esto NO abre el puerto, solo es documentación
# El puerto se mapea con: docker run -p 8080:8080
EXPOSE 8080

# Copiar el JAR generado en la ETAPA 1 (build) a la imagen final
# --from=build: tomar archivo de la etapa "build" anterior
# Solo se copia el JAR, NO el código fuente ni herramientas de compilación
# Esto mantiene la imagen final pequeña y segura
COPY --from=build /app/build/libs/ExamenMercado-1.0-SNAPSHOT.jar ./app.jar

# Comando que se ejecuta cuando el contenedor inicia
# ENTRYPOINT (no CMD) asegura que siempre se ejecute la aplicación
# ["java", "-jar", "app.jar"]: formato exec (preferido sobre shell)
ENTRYPOINT ["java", "-jar", "app.jar"]