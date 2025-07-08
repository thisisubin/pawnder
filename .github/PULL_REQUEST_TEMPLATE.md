### 📌 PR 개요
<!-- 이 PR이 어떤 기능/수정/버그패치인지 간략하게 설명해주세요 -->
이메일 인증을 통한 회원가입 및 로그인 기능을 구현하였습니다.

---

### 🔨 작업 내용 요약
<!-- 주요 변경사항을 bullet 형식으로 요약해 주세요 -->
- 기능 추가: 이메일 인증 코드 전송 API (`/send-email`)
- 기능 추가: 이메일 인증 코드 검증 API (`/verify-email`)
- 기능 추가: 이메일 인증된 사용자에 한해 회원가입 허용 (`/signup`)
- 기능 추가: 로그인 API 구현 (`/login`)
- 리팩토링: 이메일 인증 상태 Redis에 저장(`EMAIL_CODE:`, `VERIFIED_EMAIL:` 키 사용)

---

### ✅ 상세 구현 내용
<!-- 상세하게 어떤 작업을 했는지 설명해주세요 -->
- 회원가입 전 이메일 인증 절차를 추가했습니다.
    - 인증 요청 시 6자리 코드를 생성하여 메일 전송
    - Redis에 인증 코드와 이메일 매핑 저장 (TTL: 2분)
- 인증 코드 입력 시 이메일과 매칭되는 코드가 존재할 경우 해당 이메일을 `VERIFIED_EMAIL:{email}` 키로 저장
- 회원가입 로직에서 Redis에 저장된 이메일 인증 상태(`VERIFIED_EMAIL:{email}`)를 확인 후 처리
- 인증된 사용자만 `User` 테이블에 저장되며, `user.setVerified(true)`로 인증 여부 DB 반영

---

### 🧪 테스트 방법
<!-- 어떻게 테스트했는지, 테스트 시나리오가 있다면 적어주세요 -->
1. Postman으로 `POST /api/users/send-email?email=your@email.com` 요청
2. Redis에서 발급된 `EMAIL_CODE:{code}` 확인
3. `POST /api/users/verify-email?code=xxxxxx` 요청 → 인증 완료
4. `POST /api/users/signup` 요청 → 회원가입 성공
5. `POST /api/users/login` 요청 → 로그인 성공

---

### 📷 스크린샷 (선택)
<!-- UI 작업을 했다면 캡처 첨부해주세요 -->


---

### 🔍 중점적으로 봐줬으면 하는 부분
<!-- 리뷰어가 특히 신경써서 봐야 할 코드나 로직이 있다면 적어주세요 -->
- Redis TTL 로직 및 키 네이밍 컨벤션 (`EMAIL_CODE:`, `VERIFIED_EMAIL:`)
- 이메일 인증 상태 확인 로직의 안정성
- 회원가입 흐름과 인증 흐름 간의 연결이 자연스러운지

---

### 📎 관련 이슈
- Resolved: #이메일-인증-회원가입
