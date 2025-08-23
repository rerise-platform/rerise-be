# 리라이즈(Rerise) API 명세서 - 프론트엔드용

## 📋 목차
1. [기본 정보](#기본-정보)
2. [인증 시스템](#인증-시스템)
3. [API 엔드포인트](#api-엔드포인트)
   - [인증 관련 API](#인증-관련-api)
   - [온보딩 API](#온보딩-api)
   - [메인 화면 API](#메인-화면-api)
   - [일기 기록 API](#일기-기록-api)
   - [미션 시스템 API](#미션-시스템-api)
   - [캐릭터 성장 API](#캐릭터-성장-api)
4. [DTO 구조](#dto-구조)
5. [에러 코드](#에러-코드)

---

## 기본 정보

**Base URL**: `http://localhost:8080`  
**Content-Type**: `application/json`  
**인코딩**: UTF-8

---

## 인증 시스템

### JWT 토큰 방식
- 로그인 성공 시 JWT 토큰 반환
- 인증이 필요한 API 호출 시 헤더에 토큰 포함
- 토큰 만료 시간: 1시간 (3600초)

```http
Authorization: Bearer {JWT_TOKEN}
```

### 공개 엔드포인트
- `/api/v1/signup` (회원가입)
- `/api/v1/login` (로그인)
- `/api/v1/test/**` (온보딩)

---

## API 엔드포인트

### 인증 관련 API

#### 1. 회원가입
```http
POST /api/v1/signup
```

**요청 바디:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "passwordCheck": "password123",
  "nickname": "사용자닉네임",
  "birth": "1990-01-01"
}
```

**응답:**
```http
Status: 200 OK
Content-Type: text/plain

"회원가입 성공" 또는 에러 메시지
```

#### 2. 로그인
```http
POST /api/v1/login
```

**요청 바디:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답 (성공):**
```http
Status: 200 OK
Content-Type: text/plain

eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjMwMDAwMDAwLCJleHAiOjE2MzAwMDM2MDB9...
```

**응답 (실패):**
```http
Status: 200 OK
Content-Type: text/plain

"아이디 또는 비밀번호가 올바르지 않습니다."
```

---

### 온보딩 API

#### 1. 온보딩 테스트 완료
```http
POST /api/v1/test/complete
```

**요청 바디:**
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
      "selectedOption": 4
    },
    {
      "questionNumber": 7,
      "selectedOption": 2
    },
    {
      "questionNumber": 8,
      "selectedOption": 3
    }
  ]
}
```

**응답:**
```json
{
  "userId": 1,
  "characterId": 2,
  "characterType": "mony",
  "description": "급격한 변화를 피하고, 익숙하고 안정적인 환경에서 자신의 에너지를 조용히 채워나갑니다...",
  "keywords": ["내향성", "안정추구", "섬세함"],
  "energyLevel": 3,
  "adaptability": 2,
  "resilience": 3
}
```

---

### 메인 화면 API

#### 1. 메인 화면 정보 조회
```http
GET /api/v1/main
Authorization: Bearer {JWT_TOKEN}
```

**응답 (온보딩 완료 사용자):**
```json
{
  "nickname": "사용자닉네임",
  "characterType": "mony",
  "characterStage": 1,
  "level": 5,
  "growthRate": 65.5
}
```

**응답 (온보딩 미완료 사용자):**
```json
{
  "nickname": "사용자닉네임",
  "characterType": null,
  "characterStage": null,
  "level": null,
  "growthRate": null
}
```

---

### 일기 기록 API

#### 1. 일기 기록 생성/수정
```http
POST /api/v1/records
Authorization: Bearer {JWT_TOKEN}
```

**요청 바디:**
```json
{
  "emotion_level": 4,
  "keywords": "행복, 성취감, 만족",
  "memo": "오늘은 정말 좋은 하루였다. 목표했던 일을 모두 완료했고...",
  "recordedAt": "2025-08-24"
}
```

**응답:**
```json
{
  "record_id": 123,
  "emotion_level": 4,
  "keywords": "행복, 성취감, 만족",
  "memo": "오늘은 정말 좋은 하루였다. 목표했던 일을 모두 완료했고...",
  "recordedAt": "2025-08-24"
}
```

#### 2. 특정 날짜 일기 조회
```http
GET /api/v1/records/date/{date}
Authorization: Bearer {JWT_TOKEN}
```

**경로 매개변수:**
- `date`: ISO 날짜 형식 (예: 2025-08-24)

**응답:**
```json
{
  "record_id": 123,
  "emotion_level": 4,
  "keywords": "행복, 성취감, 만족",
  "memo": "오늘은 정말 좋은 하루였다. 목표했던 일을 모두 완료했고...",
  "recordedAt": "2025-08-24"
}
```

---

### 미션 시스템 API

#### 1. 일일 미션 생성
```http
POST /api/missions/daily
Authorization: Bearer {JWT_TOKEN}
```

**요청 바디:**
```json
{
  "userInput": "오늘은 스트레스가 많았고, 운동을 하고 싶다"
}
```

**응답:**
```json
[
  {
    "userDailyMissionId": 1,
    "missionId": 45,
    "content": "15분 동안 동네 산책하기",
    "theme": "몸돌보기",
    "theory": "BEHAVIORAL_ACTIVATION",
    "rewardExp": 15,
    "status": "ASSIGNED",
    "assignedDate": "2025-08-24",
    "completedDate": null
  },
  {
    "userDailyMissionId": 2,
    "missionId": 12,
    "content": "5분 동안 나의 호흡을 그대로 관찰해보세요",
    "theme": "마음보기",
    "theory": "MINDFULNESS",
    "rewardExp": 15,
    "status": "ASSIGNED",
    "assignedDate": "2025-08-24",
    "completedDate": null
  },
  {
    "userDailyMissionId": 3,
    "missionId": 78,
    "content": "감정일기 3줄 쓰기",
    "theme": "마음보기",
    "theory": "COGNITIVE_RESTRUCTURING",
    "rewardExp": 15,
    "status": "ASSIGNED",
    "assignedDate": "2025-08-24",
    "completedDate": null
  }
]
```

#### 2. 오늘의 미션 조회
```http
GET /api/missions/today
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```json
[
  {
    "userDailyMissionId": 1,
    "missionId": 45,
    "content": "15분 동안 동네 산책하기",
    "theme": "몸돌보기",
    "theory": "BEHAVIORAL_ACTIVATION",
    "rewardExp": 15,
    "status": "ASSIGNED",
    "assignedDate": "2025-08-24",
    "completedDate": null
  }
]
```

#### 3. 미션 완료 처리
```http
POST /api/missions/complete
Authorization: Bearer {JWT_TOKEN}
```

**요청 바디:**
```json
{
  "userDailyMissionId": 1
}
```

**응답:**
```json
{
  "userDailyMissionId": 1,
  "missionId": 45,
  "content": "15분 동안 동네 산책하기",
  "theme": "몸돌보기",
  "theory": "BEHAVIORAL_ACTIVATION",
  "rewardExp": 15,
  "status": "COMPLETED",
  "assignedDate": "2025-08-24",
  "completedDate": "2025-08-24"
}
```

#### 4. 미션 API 테스트
```http
GET /api/missions/test
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```http
Status: 200 OK
Content-Type: text/plain

"Daily Mission API is working!"
```

---

### 캐릭터 성장 API

#### 1. 경험치 추가 (테스트용)
```http
POST /api/v1/{userId}/addExp
Authorization: Bearer {JWT_TOKEN}
```

**경로 매개변수:**
- `userId`: 사용자 ID (숫자)

**요청 바디:**
```json
{
  "experience": 50
}
```

**응답:**
```json
{
  "userCharacterId": 1,
  "level": 5,
  "experience": 450,
  "stage": 1,
  "characterName": "모니"
}
```

---

## DTO 구조

### 캐릭터 타입
- `mony`: 모니 (내향성, 안정추구, 섬세함)
- `tory`: 토리 (휴식필요, 현실적, 자기돌봄)
- `pory`: 포리 (경험추구, 호기심, 표현력)
- `koko`: 코코 (목표지향, 계획, 논리적)

### 미션 상태
- `ASSIGNED`: 할당됨
- `COMPLETED`: 완료됨

### 미션 테마
- `마음보기`: 감정, 생각, 상태 인지
- `몸돌보기`: 신체 움직임과 돌봄
- `마음나누기`: 감사와 긍정적 마음
- `공간만들기`: 환경과 일상 정리
- `사람연결`: 인간관계와 소통

### 미션 이론
- `MINDFULNESS`: 마음챙김
- `COGNITIVE_RESTRUCTURING`: 인지 재구성
- `BEHAVIORAL_ACTIVATION`: 행동 활성화
- `GRATITUDE_PRACTICE`: 감사 실천
- `SOCIAL_CONNECTION`: 사회적 연결

---

## 에러 코드

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 필요 또는 토큰 만료
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

### 일반적인 에러 메시지
```json
{
  "error": "User not found with email: user@example.com"
}
```

```json
{
  "error": "UserCharacter not found for the user"
}
```

```json
{
  "error": "Character type not found in database: invalid_type"
}
```

---

## 사용 예시

### 1. 회원가입 → 로그인 → 온보딩 → 메인 화면
```javascript
// 1. 회원가입
const signup = await fetch('/api/v1/signup', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123',
    passwordCheck: 'password123',
    nickname: '테스트유저',
    birth: '1990-01-01'
  })
});

// 2. 로그인
const login = await fetch('/api/v1/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123'
  })
});
const token = await login.text();

// 3. 온보딩
const onboarding = await fetch('/api/v1/test/complete', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    answers: [
      { questionNumber: 1, selectedOption: 2 },
      { questionNumber: 2, selectedOption: 1 },
      // ... 8개 질문 모두
    ]
  })
});

// 4. 메인 화면 정보
const main = await fetch('/api/v1/main', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const mainData = await main.json();
```

### 2. 미션 시스템 사용
```javascript
// 1. 일일 미션 생성
const createMissions = await fetch('/api/missions/daily', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    userInput: '스트레스를 받았고 운동을 하고 싶다'
  })
});

// 2. 오늘의 미션 조회
const todayMissions = await fetch('/api/missions/today', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// 3. 미션 완료
const completeMission = await fetch('/api/missions/complete', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    userDailyMissionId: 1
  })
});
```

---

## 주의사항

1. **JWT 토큰 관리**: 토큰을 안전하게 저장하고, 만료 시 재로그인 필요
2. **날짜 형식**: ISO 8601 형식 사용 (YYYY-MM-DD)
3. **한글 인코딩**: UTF-8 사용 필수
4. **에러 처리**: HTTP 상태 코드와 응답 메시지를 모두 확인
5. **온보딩 순서**: 회원가입 → 로그인 → 온보딩 → 일반 기능 사용

---

## 업데이트 히스토리

- **v1.0** (2025-08-24): 초기 API 명세 작성
  - 인증, 온보딩, 메인 화면, 일기, 미션, 캐릭터 성장 API 포함