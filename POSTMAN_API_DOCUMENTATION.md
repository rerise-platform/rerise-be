# Rerise API 테스트 문서 (Postman용)

## 기본 설정
- **Base URL**: `http://localhost:8080`
- **인증 방식**: JWT Bearer Token (로그인 후 토큰 사용)

---

## 1. 인증 관리 API (`/api/v1`)

### 1.1 회원가입
- **Method**: `POST`
- **URL**: `{{base_url}}/api/v1/signup`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Request Body**:
  ```json
  {
    "email": "test@example.com",
    "password": "password123",
    "passwordCheck": "password123",
    "nickname": "testuser",
    "birth": "1990-01-01"
  }
  ```
- **Response**: 
  - 성공: `"회원가입 성공"`
  - 실패: `"로그인 아이디가 중복됩니다."` / `"닉네임이 중복됩니다."` / `"바밀번호가 일치하지 않습니다."`

### 1.2 로그인
- **Method**: `POST`
- **URL**: `{{base_url}}/api/v1/login`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Request Body**:
  ```json
  {
    "email": "test@example.com",
    "password": "password123"
  }
  ```
- **Response**: 
  - 성공: JWT 토큰 문자열
  - 실패: `"로그인 아이디 또는 비밀번호가 틀렸습니다."`

---

## 2. 일기 기록 API (`/api/v1/records`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 2.1 일기 기록 생성/수정
- **Method**: `POST`
- **URL**: `{{base_url}}/api/v1/records`
- **Headers**:
  ```
  Content-Type: application/json
  Authorization: Bearer {{jwt_token}}
  ```
- **Request Body**:
  ```json
  {
    "emotion_level": 4,
    "keywords": ["행복", "성취감", "만족"],
    "memo": "오늘은 프로젝트를 성공적으로 완료해서 기분이 좋았다.",
    "recordedAt": "2024-01-15"
  }
  ```
- **Response**:
  ```json
  {
    "record_id": 1,
    "emotion_level": 4,
    "keywords": ["행복", "성취감", "만족"],
    "memo": "오늘은 프로젝트를 성공적으로 완료해서 기분이 좋았다.",
    "recordedAt": "2024-01-15"
  }
  ```

### 2.2 특정 날짜 일기 기록 조회
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/records/date/2024-01-15`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **Response**:
  ```json
  {
    "record_id": 1,
    "emotion_level": 4,
    "keywords": ["행복", "성취감", "만족"],
    "memo": "오늘은 프로젝트를 성공적으로 완료해서 기분이 좋았다.",
    "recordedAt": "2024-01-15"
  }
  ```

---

## 3. 온보딩 테스트 API (`/api/v1/test`)

### 3.1 온보딩 테스트 완료
- **Method**: `POST`
- **URL**: `{{base_url}}/api/v1/test/complete`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Request Body**:
  ```json
  {
    "answers": [
      {
        "questionNumber": 1,
        "selectedOption": 2
      },
      {
        "questionNumber": 2,
        "selectedOption": 1
      },
      {
        "questionNumber": 3,
        "selectedOption": 3
      },
      {
        "questionNumber": 4,
        "selectedOption": 2
      },
      {
        "questionNumber": 5,
        "selectedOption": 1
      },
      {
        "questionNumber": 6,
        "selectedOption": 3
      },
      {
        "questionNumber": 7,
        "selectedOption": 2
      },
      {
        "questionNumber": 8,
        "selectedOption": 1
      }
    ]
  }
  ```
- **Response**:
  ```json
  {
    "characterId": 1,
    "userId": 123,
    "characterType": "모험가",
    "description": "새로운 도전을 즐기는 활발한 성격",
    "keywords": ["도전", "활발", "창의적"],
    "energyLevel": 85,
    "adaptability": 90,
    "Resilience": 75
  }
  ```

---

## 4. 일일 미션 API (`/api/missions`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 4.1 일일 미션 생성 (AI 분석 기반 선별)
- **Method**: `POST`
- **URL**: `{{base_url}}/api/missions/daily`
- **Headers**:
  ```
  Content-Type: application/json
  Authorization: Bearer {{jwt_token}}
  ```
- **동작 방식**:
  1. 사용자 입력 + 최근 7일 일기 데이터 분석
  2. AI(Gemini)가 적합한 테마 3개 추천 
  3. 심리학적 이론 1개 선택
  4. 기존 198개 미션 중 조건에 맞는 5개 선별
- **Request Body**:
  ```json
  {
    "userInput": "오늘 하루 기분이 우울해요. 집에만 있고 싶어요."
  }
  ```
- **Response**:
  ```json
  [
    {
      "userDailyMissionId": 1,
      "missionId": 15,
      "content": "5분간 간단한 스트레칭으로 몸 풀어주기",
      "theme": "몸돌보기",
      "theory": "BEHAVIORAL_ACTIVATION",
      "rewardExp": 15,
      "status": "PENDING",
      "assignedDate": "2024-08-23",
      "completedAt": null
    },
    {
      "userDailyMissionId": 2,
      "missionId": 8,
      "content": "지금 느끼는 감정에 \"안녕\"이라고 인사해보세요",
      "theme": "마음보기",
      "theory": "MINDFULNESS",
      "rewardExp": 15,
      "status": "PENDING",
      "assignedDate": "2024-08-23",
      "completedAt": null
    }
  ]
  ```
- **가능한 테마**: `마음보기`, `몸돌보기`, `마음나누기`, `공간만들기`, `사람연결`
- **가능한 이론**: `BEHAVIORAL_ACTIVATION`, `COGNITIVE_RESTRUCTURING`, `MINDFULNESS`, `SOCIAL_CONNECTION`, `GRATITUDE_PRACTICE`

### 4.2 오늘의 미션 조회
- **Method**: `GET`
- **URL**: `{{base_url}}/api/missions/today`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **Response**:
  ```json
  [
    {
      "userDailyMissionId": 1,
      "missionId": 15,
      "content": "5분간 간단한 스트레칭으로 몸 풀어주기",
      "theme": "몸돌보기",
      "theory": "BEHAVIORAL_ACTIVATION",
      "rewardExp": 15,
      "status": "PENDING",
      "assignedDate": "2024-08-23",
      "completedAt": null
    }
  ]
  ```

### 4.3 미션 완료
- **Method**: `POST`
- **URL**: `{{base_url}}/api/missions/complete`
- **Headers**:
  ```
  Content-Type: application/json
  Authorization: Bearer {{jwt_token}}
  ```
- **Request Body**:
  ```json
  {
    "userDailyMissionId": 1
  }
  ```
- **Response**:
  ```json
  {
    "userDailyMissionId": 1,
    "missionId": 15,
    "content": "5분간 간단한 스트레칭으로 몸 풀어주기",
    "theme": "몸돌보기",
    "theory": "BEHAVIORAL_ACTIVATION",
    "rewardExp": 15,
    "status": "COMPLETED",
    "assignedDate": "2024-08-23",
    "completedAt": "2024-08-23T14:30:00"
  }
  ```

### 4.4 테스트 엔드포인트
- **Method**: `GET`
- **URL**: `{{base_url}}/api/missions/test`
- **Headers**: 없음
- **Response**: `"Daily Mission API is working!"`

---

## Postman 환경 변수 설정

### Environment Variables
1. `base_url`: `http://localhost:8080`
2. `jwt_token`: 로그인 API에서 받은 JWT 토큰

### 토큰 자동 설정 스크립트
로그인 API의 Tests 탭에 추가:
```javascript
if (pm.response.code === 200) {
    pm.environment.set("jwt_token", pm.response.text());
}
```

---

## 테스트 시나리오 예시

### 1. 기본 플로우
1. **회원가입** → 계정 생성
2. **로그인** → JWT 토큰 획득
3. **온보딩 테스트** → 캐릭터 타입 결정
4. **일기 기록 작성** → 감정 상태 기록
5. **일일 미션 생성** → AI 분석으로 기존 미션에서 개인화 선별
6. **미션 완료** → 경험치 획득

### 2. 일일 사용 플로우
1. **로그인**
2. **오늘의 미션 조회**
3. **일기 기록 작성**
4. **미션 완료**

---

## 주의사항

1. **인증이 필요한 API**는 반드시 Authorization 헤더에 Bearer Token을 포함해야 합니다.
2. **날짜 형식**은 `YYYY-MM-DD` 형식을 사용합니다.
3. **미션 상태**는 `ASSIGNED`, `COMPLETED` 등이 있습니다.
4. **에러 응답**의 경우 적절한 HTTP 상태 코드와 에러 메시지가 반환됩니다.

---

## 5. 메인 화면 API (`/api/v1`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 5.1 메인 화면 정보 조회
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/main`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **설명**: 메인 화면에 필요한 사용자 정보, 캐릭터 정보, 최근 일기, 오늘의 미션 등을 종합적으로 조회
- **Response**:
  ```json
  {
    "userId": 123,
    "nickname": "testuser",
    "characterInfo": {
      "characterId": 1,
      "characterName": "모니",
      "characterType": "mony",
      "level": 5,
      "experience": 120,
      "stage": 2
    },
    "recentRecord": {
      "recordId": 10,
      "emotionLevel": 4,
      "keywords": ["행복", "성취감"],
      "memo": "오늘은 좋은 하루였다",
      "recordedAt": "2024-08-24"
    },
    "todayMissions": [
      {
        "userDailyMissionId": 1,
        "missionId": 15,
        "content": "5분간 간단한 스트레칭으로 몸 풀어주기",
        "theme": "몸돌보기",
        "theory": "BEHAVIORAL_ACTIVATION",
        "status": "PENDING"
      }
    ]
  }
  ```

---

## 6. 주간 미션 API (`/api/v1/missions`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 6.1 주간 미션 생성/조회 (AI 분석 기반)
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/missions/weekly`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **설명**: 
  - 사용자의 최근 7일간 일기 데이터를 AI(Gemini)로 분석
  - 개인화된 주간 미션 5개를 자동 생성 및 조회
  - 이번 주 미션이 없으면 자동으로 새로 생성
- **Response**:
  ```json
  {
    "userId": 123,
    "summaryMessage": "최근 일주일간 업무 스트레스와 피로감이 많이 보입니다. 몸과 마음의 안정을 찾는 것에 집중해보세요.",
    "recommendedTheory": "MINDFULNESS",
    "themes": ["마음보기", "몸돌보기", "공간만들기"],
    "missions": [
      {
        "missionId": 15,
        "content": "5분간 간단한 스트레칭으로 몸 풀어주기",
        "theme": "몸돌보기",
        "missionLevel": 1,
        "theory": "BEHAVIORAL_ACTIVATION",
        "rewardExp": 15
      },
      {
        "missionId": 8,
        "content": "지금 느끼는 감정에 \"안녕\"이라고 인사해보세요",
        "theme": "마음보기",
        "missionLevel": 1,
        "theory": "MINDFULNESS",
        "rewardExp": 15
      }
    ]
  }
  ```

---

## 7. 헬스체크 API (`/api/v1`)

### 7.1 서버 상태 확인
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/health`
- **Headers**: 없음
- **설명**: 서버의 현재 상태와 버전 정보를 확인
- **Response**:
  ```json
  {
    "status": "UP",
    "message": "Rerise Backend is running",
    "timestamp": "2024-08-25T02:15:30.123456",
    "version": "1.0.0"
  }
  ```

---

## 8. 캐릭터 경험치 API (`/api/v1`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 8.1 경험치 추가
- **Method**: `POST`
- **URL**: `{{base_url}}/api/v1/{userId}/addExp`
- **Headers**:
  ```
  Content-Type: application/json
  Authorization: Bearer {{jwt_token}}
  ```
- **Path Variables**: 
  - `userId`: 사용자 ID (Long)
- **Request Body**:
  ```json
  {
    "userCharacterId": null,
    "level": null,
    "experience": 100,
    "stage": null,
    "characterName": null
  }
  ```
- **Response**:
  ```json
  {
    "userCharacterId": 1,
    "level": 2,
    "experience": 150,
    "stage": 1,
    "characterName": "모니"
  }
  ```

---

## 9. 보안 및 권한 관리

### 9.1 인증이 불필요한 엔드포인트
- `/api/v1/signup` (회원가입)
- `/api/v1/login` (로그인)  
- `/api/v1/test/**` (온보딩 테스트)
- `/api/v1/health` (헬스체크)

### 9.2 JWT 인증이 필요한 엔드포인트
- `/api/v1/main` (메인 화면)
- `/api/v1/records/**` (일기 관련)
- `/api/missions/**` (일일 미션 관련)
- `/api/v1/missions/weekly` (주간 미션)
- `/api/v1/{userId}/addExp` (경험치 관련)

### 9.3 관리자 권한이 필요한 엔드포인트
- `/api/v1/admin/**` (관리자 전용 기능)

---

## 10. 데이터베이스 구조 참고

### 10.1 주요 테이블
- **users**: 사용자 정보
- **characters**: 캐릭터 마스터 데이터 (4개: 모니, 토리, 포리, 코코)
- **user_character**: 사용자별 캐릭터 진행 상황
- **daily_record**: 일기 데이터
- **missions**: 미션 마스터 데이터 (198개, 4단계 레벨)
- **user_daily_missions**: 사용자별 일일 미션 할당/완료 상태
- **onboarding_answer**: 온보딩 테스트 답변

### 10.2 캐릭터 타입
1. **모니 (mony)**: 조심스럽고 안정적, 변화에 천천히 적응
2. **토리 (tory)**: 활발하고 에너지 넘침, 새로운 도전 선호
3. **포리 (pory)**: 차분하고 안정감, 꾸준함 중시
4. **코코 (koko)**: 호기심 많고 창의적, 유연한 사고

### 10.3 미션 레벨 체계 (4단계)
1. **입문 (Level 1)**: 기초적인 미션들
2. **기본 (Level 2)**: 실천 중심 미션들  
3. **중급 (Level 3)**: 심화 미션들
4. **고급 (Level 4)**: 전문가 레벨, 타인 도움, 리더십 미션들

### 10.4 미션 테마 (5가지)
- **마음보기**: 감정, 생각, 상태 인지
- **몸돌보기**: 신체 움직임과 돌봄
- **마음나누기**: 감사와 긍정적 마음
- **공간만들기**: 환경과 일상 정리
- **사람연결**: 인간관계와 소통

---

## 11. 장소 및 프로그램 추천 API (`/api/v1/recommendation`)

> **인증 필요**: Bearer Token 헤더 추가 필요

### 11.1 서울 서초구 장소 추천
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/recommendation/places/seocho`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **설명**: 사용자의 감정 상태, 키워드, 메모, 성향을 기반으로 서울 서초구 지역의 장소를 AI(퍼플렉시티)가 추천
- **Response**:
  ```json
  {
    "success": true,
    "message": "장소 추천이 성공적으로 완료되었습니다.",
    "data": "서초구 추천 장소 정보가 담긴 텍스트 형태의 AI 응답"
  }
  ```
- **Error Response**:
  - **401 Unauthorized**: 
    ```json
    {
      "success": false,
      "message": "로그인이 필요합니다.",
      "data": null
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "success": false,
      "message": "사용자 정보를 찾을 수 없습니다.",
      "data": null
    }
    ```
  - **500 Internal Server Error**:
    ```json
    {
      "success": false,
      "message": "장소 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      "data": null
    }
    ```

### 11.2 사용자 맞춤 프로그램 추천
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/recommendation/programs`
- **Headers**:
  ```
  Authorization: Bearer {{jwt_token}}
  ```
- **설명**: 사용자의 레벨과 성향에 맞는 프로그램 3개를 추천 (청년/문화 프로그램 구분)
- **Response**:
  ```json
  {
    "programs": [
      {
        "programName": "서초구 청년 취업 지원 프로그램",
        "category": "청년",
        "target": "만 18~39세 청년",
        "recruitmentPeriod": "2024-03-01 ~ 2024-03-31",
        "location": "서초구 청년센터",
        "url": "https://example.com/program1"
      },
      {
        "programName": "서초문화원 문화체험 프로그램",
        "category": "문화",
        "target": "서초구민 누구나",
        "recruitmentPeriod": "상시모집",
        "location": "서초문화원",
        "url": "https://example.com/program2"
      }
    ],
    "recommendationReason": "회원님의 레벨이 높아 취업 및 커리어 관련 청년 프로그램을 우선적으로 추천드렸습니다.",
    "success": true,
    "message": "프로그램 추천이 성공적으로 완료되었습니다."
  }
  ```
- **Error Response**:
  - **401 Unauthorized**: 
    ```json
    {
      "programs": null,
      "recommendationReason": null,
      "success": false,
      "message": "로그인이 필요합니다."
    }
    ```
  - **404 Not Found**:
    ```json
    {
      "programs": null,
      "recommendationReason": null,
      "success": false,
      "message": "사용자 정보를 찾을 수 없습니다."
    }
    ```
  - **500 Internal Server Error**:
    ```json
    {
      "programs": null,
      "recommendationReason": null,
      "success": false,
      "message": "프로그램 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
    }
    ```

### 11.3 추천 서비스 상태 확인
- **Method**: `GET`
- **URL**: `{{base_url}}/api/v1/recommendation/health`
- **Headers**: 없음
- **설명**: 장소 및 프로그램 추천 서비스의 현재 상태를 확인
- **Response**: `"장소 및 프로그램 추천 서비스가 정상적으로 작동 중입니다."`