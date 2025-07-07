# Cluvr Batch

Spring Batch 기반의 데이터 처리 및 ETL(Extract, Transform, Load) 작업을 위한 백엔드 프로젝트입니다.

## 주요 기능

- 다양한 배치 잡(Job) 구성 (예: 로그, 리뷰, 유저 활동, 조회수 등)
- Redis, DB 등 다양한 데이터 소스 연동
- Docker 및 Docker Compose 지원
- Grafana, Prometheus를 통한 모니터링
- Jenkins를 통한 CI/CD 파이프라인

## 폴더 구조

```
cluvr-batch/
├── build.gradle                # Gradle 빌드 설정
├── docker-compose.yml          # 서비스 오케스트레이션
├── docker-local-compose.yml    # 로컬 개발용 Docker Compose
├── Dockerfile                  # Docker 이미지 빌드 파일
├── Jenkinsfile                 # Jenkins CI/CD 파이프라인
├── prometheus.yml              # Prometheus 설정
├── grafana/                    # Grafana 대시보드 및 프로비저닝
├── src/
│   └── main/
│       ├── java/com/example/cluvrbatch/
│       │   ├── config/         # 설정 파일
│       │   ├── job/            # 배치 잡 및 스텝
│       │   ├── openai/         # OpenAI 연동 서비스
│       └── resources/
│           └── application.yml # 환경설정
└── ...
```

## 실행 방법

### 1. 로컬 실행

```bash
./gradlew build
java -jar build/libs/cluvr-batch-0.0.1-SNAPSHOT.jar
```

### 2. Docker로 실행

```bash
docker build -t cluvr-batch .
docker-compose up
```

### 3. 배치 잡 실행

- Spring Batch 잡은 스케줄러에 의해 자동 실행됩됩니다.  
- 스케쥴에 대한 상세 동작은은 `src/main/java/com/example/cluvrbatch/job/launcher/Scheduler.java` 참고.

## 환경 변수 및 설정

- 주요 설정 파일: `src/main/resources/application.yml`
- DB, Redis, 외부 API 키 등은 환경 변수 또는 `application.yml`에서 관리합니다.


## 상세 잡 설명 및 내부 동작

### 1. CloverLogJob (클로버 로그 적재)
- **동작 개요:**  
  Redis에 임시 저장된 클로버 이벤트 로그를 읽어와, RDS(DB)에 일괄 적재합니다.
- **Reader:**  
  - `CloverLogItemReader`
    - Redis에서 `CLOVER_LOG`로 시작하는 모든 키를 조회
    - 각 키의 리스트 값을 모두 읽어와 JSON → DTO로 변환, 큐에 적재
    - 읽은 후 해당 Redis 키는 삭제
    - 큐에서 하나씩 반환, 더 이상 없으면 null 반환(=배치 종료)
- **Writer:**  
  - `CloverLogItemWriter`
    - 청크 단위로 받은 DTO 리스트를 DB에 batch insert
- **주요 목적:**  
  대량의 클로버 이벤트 로그를 안정적으로 DB에 적재

---

### 2. GemLogJob (젬 로그 적재)
- **동작 개요:**  
  Redis에 임시 저장된 젬 이벤트 로그를 읽어와, RDS(DB)에 일괄 적재합니다.
- **Reader:**  
  - `GemLogItemReader`
    - Redis에서 `GEM_LOG`로 시작하는 모든 키를 조회
    - 각 키의 리스트 값을 모두 읽어와 JSON → DTO로 변환, 큐에 적재
    - 읽은 후 해당 Redis 키는 삭제
    - 큐에서 하나씩 반환, 더 이상 없으면 null 반환(=배치 종료)
- **Writer:**  
  - `GemLogItemWriter`
    - 청크 단위로 받은 DTO 리스트를 DB에 batch insert
- **주요 목적:**  
  대량의 젬 이벤트 로그를 안정적으로 DB에 적재

---

### 3. ReviewRequestJob (리뷰 요청 자동화)
- **동작 개요:**  
  리뷰가 필요한 TIL(Today I Learned) 데이터를 찾아, OpenAI를 통해 피드백/요약/점수를 생성하고 DB에 반영합니다.
- **Tasklet:**  
  - `ReviewRequestTasklet`
    1. 리뷰되지 않은 TIL 목록을 DB에서 조회
    2. 각 TIL에 대해 OpenAI API로 피드백/요약/점수 생성 시도
    3. 성공 시 결과를 DTO에 반영, 실패 시 실패 메시지로 대체
    4. 각 결과를 DB에 업데이트(비동기 Flux로 병렬 처리, 최대 10개 동시)
    5. 전체 완료 후 로그 출력
- **주요 목적:**  
  AI 기반 자동 리뷰 및 결과 저장, 리뷰 자동화

---

### 4. BoardStatJob (게시판 통계 적재)
- **동작 개요:**  
  Redis에 임시 저장된 게시판 활동 통계(답변수, 질문수, 클로버수, 티어 등)를 읽어와, RDS(DB)에 일괄 적재합니다.
- **Reader:**  
  - `BoardStatItemReader`
    - Redis에서 `BOARD_ACTIVITY_LOG`로 시작하는 모든 키를 조회
    - 각 키의 해시(Hash) 값을 읽어와 DTO로 변환
    - 티어 정보는 추가 로직(Tier.checkAndUpgrade)으로 갱신
    - 읽은 후 해당 Redis 키는 삭제
    - 큐에서 하나씩 반환, 더 이상 없으면 null 반환(=배치 종료)
- **Writer:**  
  - `BoardStatItemWriter`
    - 청크 단위로 받은 DTO 리스트를 DB에 batch insert
- **주요 목적:**  
  게시판 활동 통계의 대량 적재 및 티어 자동 갱신

---

### 5. ViewCountJob (게시글 조회수 적재)
- **동작 개요:**  
  Redis에 임시 저장된 게시글별 조회수를 읽어와, RDS(DB)에 일괄 적재합니다.
- **Reader:**  
  - `RedisViewCountReader`
    - Redis에서 `board:*:views` 패턴의 모든 키를 조회
    - 각 키에서 게시글 ID와 조회수 추출, DTO로 변환
    - 큐에서 하나씩 반환, 더 이상 없으면 null 반환(=배치 종료)
- **Writer:**  
  - `ViewCountWriter`
    - 청크 단위로 받은 DTO 리스트를 DB에 batch update
- **주요 목적:**  
  게시글별 조회수의 대량 적재 및 집계
