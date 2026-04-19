# 실행 가이드

1. 사전 요구 사항
    - Docker 설치
    - Java 21 및 Gradle 설치   
     
2. 빌드 및 배포 순서
   모든 마이크로서비스를 실행하기 위해 아래 명령어를 순서대로 입력하시오.   
                   
    1) 소스 코드 빌드 (JAR 생성)
       각 모듈의 소스 코드를 컴파일하여 실행 가능한 JAR 파일을 생성.

       전체 모듈 빌드 (테스트 생략)   
       ./gradlew clean build -x test
                                   
    2) 인프라 및 서비스 가동
       상황에 따라 두 가지 방식 중 하나를 선택하여 실행하시오.
        - 방법 1:전체 시스템 실행    
          DB, Kafka, 그리고 모든 마이크로서비스 컨테이너를 빌드하고 실행.

          docker compose up --build -d

        - 방법 2: 선택적 서비스 실행  
          본인이 담당한 모듈이나 필요한 인프라만 골라서 실행.

          docker-compose up -d --build user-service
       
          [형식] docker compose up -d [서비스명] [DB명] [인프라명] [api-gateway]
          - 예: 기도 서비스와 연관 인프라만 실행할 경우   
            docker compose up -d prayer-service prayer-db kafka api-gateway
    
    3) 실행 상태 확인    
     컨테이너들이 정상적으로 Up 상태인지 확인.   
     docker compose ps

