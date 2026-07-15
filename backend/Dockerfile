# ---------- Build faza ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Prvo samo pom.xml -> kesiranje zavisnosti (brzi rebuild-ovi)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Zatim izvorni kod i pakovanje (bez testova u imidzu)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Runtime faza ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Ne-root korisnik iz bezbednosnih razloga
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
