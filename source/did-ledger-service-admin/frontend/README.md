# OpenDID Ledger Service Admin Console

OpenDID Ledger Service의 관리자 도구입니다.

## 기능

- ✅ Dashboard - 시스템 현황 및 통계
- 🔐 Admin Authentication - 관리자 인증
- 📊 System Monitoring - 시스템 모니터링 (예정)

## 기술 스택

- **Frontend**: React 19, TypeScript
- **UI Library**: Material-UI (MUI)
- **Framework**: Toolpad Core
- **Router**: React Router v7
- **Build Tool**: Vite

## 개발 환경 실행

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 빌드
npm run build

# 프리뷰
npm run preview
```

## 프로젝트 구조

```
src/
├── components/       # 재사용 컴포넌트
├── context/         # React Context
├── pages/           # 페이지 컴포넌트
│   ├── auth/        # 인증 관련 페이지
│   └── dashboard/   # 대시보드
├── utils/           # 유틸리티 함수
├── apis/            # API 호출 함수
├── theme.ts         # MUI 테마 설정
└── App.tsx          # 메인 앱 컴포넌트
```

## 변경 사항 (v1.0.0)

- 기존 Wallet Service 의존성 제거
- 단순화된 네비게이션 (Dashboard만 포함)
- 서버 상태 체크 로직 제거
- 깔끔한 초기 구조 구성

## TODO

- [ ] Ledger Service API 연동
- [ ] 시스템 모니터링 기능
- [ ] 사용자 관리 기능
- [ ] 로그 관리 기능
