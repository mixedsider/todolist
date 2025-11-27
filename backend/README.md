# CateQuest AI: 카테고리 기반 질문 생성 모델

## 📜 프로젝트 소개
이 프로젝트는 Spring Boot 백엔드 사용하여 만든 개인화된 할 일 관리(Todo List) 애플리케이션입니다.<br>
세션 기반의 인증 시스템을 구축하여 사용자는 회원가입 및 로그인을 할 수 있으며, 자신의 할 일 목록만 조회하고 관리(생성, 완료 토글)할 수 있습니다.

---

## 🛠️ 기술 스택

- `Language`: Java 17
- `Framework`: Spring Boot 4.0.0
- `Database`:
  - H2 (Test/Dev)
  - MySQL 8.0 (Production - Docker)
- `ORM`: Spring Data JPA
- `Build Tool`: Gradle
- `Container`: Docker, Docker Compose
- `Testing`: JUnit 5, Mockito, AssertJ

---

### 📂 프로젝트 구성
```
TodoList/
├── src
│   ├── main
│   │   ├── java/com/example/todolist
│   │   │   ├── config        # WebConfig (CORS, Filter 등록)
│   │   │   ├── domain        # Entity & Repository (Member, Todo)
│   │   │   ├── dto           # Request/Response DTO (Record)
│   │   │   ├── service       # 비즈니스 로직
│   │   │   ├── web           # Controller & Filter
│   │   └── resources
│   │       └── application.yml
│   └── test                  # JUnit 테스트 코드
└── docker-compose.yml        # MySQL 컨테이너 설정

```

---

## 🚀 사용 방법
### 1. 사전 요구 사항 (Prerequisites)
- Java 17 이상
- Docker & Docker Compose

### 2. 프로젝트 클론 (Clone)

```Bash
git clone [https://github.com/mixedsider/todolist.git](https://github.com/mixedsider/todolist.git)
cd todolist
```

### 2. .env 파일 생성
`.env` 파일을 생성하여 환경변수 설정을 해줍니다.

```.env
DB_URL={YOUR_DB_url}
DATABASE={YOUR_DATABASE_NAME}
DB_USERNAME={YOUR_DB_USERNAME}
DB_PASSWORD={YOUR_DB_PASSWORD}
DB_PORT={YOUR_DB_PORT}
```

### 3. 데이터베이스 실행 (Docker)
프로젝트 루트 경로에서 Docker Compose를 실행하여 MySQL 컨테이너를 구동합니다.<br>
(최초 실행 시 이미지를 다운로드하느라 시간이 소요될 수 있습니다.)

```Bash
docker-compose up -d
```
참고: docker-compose.yml과 .env 파일 설정을 확인해주세요.

### 4. 애플리케이션 실행 (Backend)
```Bash
./gradlew bootRun
```
서버가 정상적으로 실행되면 http://localhost:8080으로 접근 가능합니다.

---

## 📜API 문서(API Documentation)


상세한 API 명세는 별도의 문서로 관리됩니다. 아래 링크를 참조해주세요.<br>


👉 [API.md 바로가기](./API.md)

---

## 📑 프로젝트 버전
- v1.0 (2025-11-28)
    - MVP 기능 개발 완료
---

## ⚖️ 라이선스
This project is licensed under the MIT License.
