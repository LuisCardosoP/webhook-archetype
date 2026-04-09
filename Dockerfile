FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test -x check

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8239
CMD ["sh", "-c", "java -jar -Dserver.port=${PORT:-8239} app.jar"]
