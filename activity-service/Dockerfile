# 1. 자바 실행 환경을 가져오시오
FROM eclipse-temurin:21-alpine

# 2. 컨테이너 내 작업 디렉토리를 만드시오
WORKDIR /app

# 3. Gradle로 빌드된 .jar 파일을 컨테이너 내부로 복사하시오
# (미리 ./gradlew build를 실행하여 jar 파일이 생성되어 있어야 하오)
COPY activity-service/build/libs/*.jar app.jar

# 4. 컨테이너가 시작될 때 실행할 명령을 적으시오
ENTRYPOINT ["java", "-jar", "app.jar"]
