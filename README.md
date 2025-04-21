# 📁 File Access Statistics System
파일 접근 로그를 수집, 파싱, 집계하여 사용자 및 정책 기반 통계를 제공하는 시스템입니다.
본 시스템은 보안 감사, 이상 행위 탐지, 정책 효율 분석 등을 위한 기초 데이터를 제공합니다.

## 📌 주요 기능

### 기능	설명
✅ 로그 수신 및 저장	압축된 로그 파일을 지정 디렉터리에 수신 및 저장 <br/>
✅ 로그 파싱 및 처리	콜론(:)으로 구분된 로그 포맷 파싱 후 DB 저장 <br/>
✅ 통계 집계	일자/시간/사용자/정책/디바이스별 업/다운로드 성공/실패 통계 집계 <br/>
✅ 테이블 자동 생성/정리	테이블 자동 생성 및 필요 시 데이터 클리어 지원 <br/>

## 📄 로그 포맷
로그 파일은 다음과 같은 구조의 텍스트 파일로 구성됩니다 (줄 단위 로그):

makefile
```
userId:policyId:filePath:accessType:accessResult:failReason:timestamp:deviceCode
```

예시:
```
user01:POL001:/home/file1.txt:UPLOAD:S::1713594500000:DEV001
```

## 필드	설명

| 필드명         | 설명                                         |
|----------------|----------------------------------------------|
| `userId`       | 사용자 ID                                    |
| `policyId`     | 접근 정책 ID                                 |
| `filePath`     | 접근 대상 파일 경로                          |
| `accessType`   | `UPLOAD` / `DOWNLOAD`                        |
| `accessResult` | `S` (성공), `F` (실패)                       |
| `failReason`   | 실패 사유 (`accessResult`가 `F`일 때)       |
| `timestamp`    | 접근 시간 (epoch milliseconds)               |
| `deviceCode`   | 장비 식별 코드                               |

## 🛠️ 시스템 구성
AuditReceiver
→ 압축 파일 수신 및 로그 파일 디스크 저장

FileAccessReader
→ 저장된 로그 파일을 라인 단위로 파싱

AuditRepository
→ 파싱된 로그를 DB에 저장하고, 집계 테이블로 변환

AuditSqlSession
→ MyBatis 기반의 DB 세션 및 쿼리 수행 핸들러
