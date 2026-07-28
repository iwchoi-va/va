# VA / TA 분석 데이터 FLOW 정리 (SVC_MS 스케줄 기준)

> 소스 기준: `XCron/config/service/serviceconfig_msens.xml` (스케줄 정의), `webapps/resource/querys/sql_xcron.xml` (쿼리), `src/**` (WebAction)
> 각 단계는 **"어떤 FLAG 값이 세팅되어야 다음 단계 조회 대상에 잡히는지"**로 연결됩니다. (Push 방식이 아니라, 각 스케줄이 자기 조건에 맞는 대상을 Pull 하는 구조)

---

## 0. 전체 FLOW 한눈에 보기

![image-20260720140847562](C:\Users\Hansol\AppData\Roaming\Typora\typora-user-images\image-20260720140847562.png)

```
[SPDB] Sponsor_Callinfo
   │  ① SVC_MS_008 (getRealtimewebaction / GetRTinfoWebAction)
   │     대상: VA_FLAG IS NULL or 'N'
   ▼
ms_stt_bef_meta  ────────────────────────────┐
   │  ② SVC_MS_001 (sttmetawebaction / SttMetaWebAction)   │  (STT 엔진: 외부)
   │     대상: VA_FLAG='N' (당일분)                          │  대상: STT_FLAG='N'
   │     + TM DB(설계정보) JOIN                              ▼
   ▼                                              ms_stt_rslt_merge (CONTENT 원문)
ms_stt_meta                                             │  SVC_MS_012 (sttContentSplitWebaction)
   │                                                    │  대상: STT_SP_FLAG='N' AND ENC_FLAG='N'
   │  ◀── TA 엔진(외부): ms_stt_meta에서 TA_FLAG='N' 조회   │       AND ms_stt_rslt 미존재
   │      → 분석결과 기록 → TA_FLAG='Y' / SRC_FLAG='Y'      ▼
   │                                              ms_stt_rslt (문장별 STT_SENT)
   ├──▶ ms_regu_score   (③ TA 스크립트 유사도 점수, 외부 TA)
   └──▶ ms_search_score (④ 금칙어/필수멘트 검출, 외부 TA)
   │
   │  ⑤ SVC_MS_002 (scriptResultWebAction)  ← ms_regu_score 집계
   │     대상: STT_FLAG='Y' AND TA_FLAG='Y' AND GRD_FLAG='N' AND VRS_S_FLAG='N'
   │     결과: ms_regu_sum INSERT + ms_stt_meta.VRS_S_FLAG='Y'
   │
   │     SVC_MS_003 (searchResultWebAction) ← ms_search_score 집계
   │     대상: STT_FLAG='Y' AND SRC_FLAG='Y' AND BAN_S_FLAG='N'
   │     결과: ms_search_sum INSERT + ms_stt_meta.BAN_S_FLAG='Y'
   ▼
   │  ⑥ SVC_MS_004 (graderesultwebaction / GradeResultWebAction)
   │     대상: STT_FLAG='Y' AND TA_FLAG='Y' AND SRC_FLAG='Y'
   │           AND VRS_S_FLAG='Y' AND BAN_S_FLAG='Y' AND GRD_FLAG='N'
   │           AND ms_grade_sum 미존재
   ▼
ms_grade_sum (최종 등급/점수)
```

---

## 1. SVC_MS 스케줄 ↔ WebAction ↔ 쿼리 매핑

| SVC ID | Description (config) | RequestGroup / WebAction | 대상 조회 쿼리 |
|--------|----------------------|--------------------------|----------------|
| `SVC_MS_008` | RealTime 분석 현황 수집 | `getRealtimewebaction` → `GetRTinfoWebAction` | `getrealtimeinfo_1` |
| `SVC_MS_001` | RealTime DB + AiGEN 정보 연동 | `sttmetawebaction` → `SttMetaWebAction` | `setsttmetawebaction_1` |
| `SVC_MS_012` | STT CONTENT SPLIT 작업 | `sttContentSplitWebaction` → `SttContentSplitWebaction` | `getSttContents.sel` |
| `SVC_MS_002` | 설계번호별 스크립트 결과 데이터 저장 | `scriptResultWebAction` → `ScriptResultWebAction` | `setscriptresultwebaction_11` (대상목록) + `_1` (상세) |
| `SVC_MS_003` | 설계번호별 금칙어/필수멘트 결과 저장 | `searchResultWebAction` → `SearchResultWebAction` | `setsearchresultwebaction_11`(대상목록) + `_1` |
| `SVC_MS_004` | 설계번호별 Grade 결과 데이터 저장 | `graderesultwebaction` → `GradeResultWebAction` | `setgraderesultwebaction_1_1 / _1_2 / _1_3` |
| `SVC_MS_014` | STT 일괄 암호화 작업 | `sttBulkWebAction` → `SttBulkBatchWebAction` | `setbulkbatchencwebaction_2` |

> `_1`(예: `SVC_MS_001_1`), `_2` 접미사 서비스는 동일 로직의 **병렬 처리용 2번째 인스턴스**입니다.

---

## 2. 단계별 상세

### ① SVC_MS_008 — 녹취정보 수집 (SPDB → ms_stt_bef_meta)

- **WebAction**: `GetRTinfoWebAction` (`getRealtimewebaction`)
- **참조 테이블**: `Sponsor_Callinfo` (SPDB, datasource `aig_rec`)
- **다음으로 넘어가는 조건** (`getrealtimeinfo_1`):
  - `VA_FLAG IS NULL OR VA_FLAG = 'N'` ← **핵심 대상 조건**
  - `RecStartDT` : (오늘-105일) ~ 오늘
  - `TPANO <> ''` AND `RecStartDT IS NOT NULL`
  - `SpCode <> '30384'`
  - `BATCH_YN IN ('N','Y')`
  - `Items` 유효값 (`<> '' / 'CS' / 'Plan_no'`)
- **산출**: `ms_stt_bef_meta` 적재 (수집 완료 시 원본 `VA_FLAG` 처리)

### (외부) STT 엔진 — 녹취 → ms_stt_rslt_merge

- **주체**: 외부 STT 엔진
- **대상**: `STT_FLAG = 'N'` 인 콜
- **산출**: `ms_stt_rslt_merge.CONTENT` (콜 단위 인식 원문), 완료 시 `STT_FLAG='Y'`

### ② SVC_MS_001 — META 생성 (ms_stt_bef_meta + TM DB → ms_stt_meta)

- **WebAction**: `SttMetaWebAction` (`sttmetawebaction`)
- **대상 조회 조건** (`setsttmetawebaction_1`):
  - `FROM ms_stt_bef_meta WHERE VA_FLAG = 'N'`
  - `REG_DATE` = 당일(`yyyyMMdd 000000 ~ 235959`)
  - `TOP(80)` 설계번호(TPANO), `ORDER BY MAX(REG_DATE) ASC`
- **처리 중 선점**: `setsttmetawebaction_2` → `ms_stt_bef_meta.VA_FLAG = 'S'`
- **참조**: TM DB 설계 기본정보(`TB_INSU_PLAN_MAST` 등) JOIN (`setsttmetawebaction_4`)
- **넘어가는 조건**: 설계번호에 엮인 **모든 콜의 STT 분석이 완료**되어야 META 생성 (`콜건수 == STT_Y_CNT`, [SttMetaWebAction.java:114](src/xcron/com/webaction/SttMetaWebAction.java))
- **산출**: `ms_stt_meta` INSERT (`setsttmetawebaction_6`), 완료분 `ms_stt_bef_meta` DELETE, 미완료분 `VA_FLAG='N'` 원복

### SVC_MS_012 — STT 문장 분리 (ms_stt_rslt_merge → ms_stt_rslt)

- **WebAction**: `SttContentSplitWebaction`
- **대상 조회 조건** (`getSttContents.sel`):
  - `FROM ms_stt_rslt_merge S1 LEFT JOIN ms_stt_rslt S2 ... WHERE S2.UCID IS NULL` (아직 분리 안 됨)
  - `STT_SP_FLAG = 'N'`
  - `ENC_FLAG = 'N'`
  - `TOP(100)`, `ORDER BY reg_date DESC`
- **FLAG 생명주기**: `STT_SP_FLAG` `N` →(시작)`S`(`getSttContens.flag.upd`) →(성공)`Y`(`getSttContens.endFlag.upd`) / (에러)`N`(`error.flag.upd`)
- **산출**: `ms_stt_rslt` INSERT (문장별 `STT_SENT` = CONTENT를 `\n`으로 분리한 값)

### (외부) TA 엔진 — 텍스트 분석 (ms_stt_meta → ms_regu_score / ms_search_score)

- **주체**: 외부 TA/분석 엔진(AiGEN)
- **대상**: `ms_stt_meta` 의 `TA_FLAG = 'N'`, `ENC_FLAG = 'N'` (다이어그램 ③)
- **산출**:
  - ③ `ms_regu_score` : 스크립트 문장별 유사도 점수(`TA_SCORE`, `SCH_SCORE`) → 완료 시 `TA_FLAG = 'Y'`
  - ④ `ms_search_score` : 금칙어/필수멘트 검출 결과 → 완료 시 `SRC_FLAG = 'Y'`

### ⑤ SVC_MS_002 — 스크립트(VRS) 준수율 집계 (ms_regu_score → ms_regu_sum)

- **WebAction**: `ScriptResultWebAction`
- **대상 목록 조회 조건** (`setscriptresultwebaction_11`):
  - 설계번호 단위로 `STT_FLAG='Y' AND TA_FLAG='Y' AND GRD_FLAG='N' AND VRS_S_FLAG='N'` 모두 충족 시 대상
  - `TOP(100)`
- **처리 중 선점**: `setscriptresultwebaction_9` → `ms_stt_meta.VRS_S_FLAG = 'S'`
- **참조**: `ms_regu_score` r + `ms_script_sent` s JOIN (`setscriptresultwebaction_1`)
- **문장 점수 계산**: [ScriptResultWebAction.java:148](src/sens/src/script/ScriptResultWebAction.java) — `QST_RATE(임계값) > TA_SCORE` 이면 문장점수 0 (누락 아님, 0점 저장)
- **산출**:
  - `ms_regu_score.SENT_SCORE` UPDATE (`_2`)
  - **`ms_regu_sum` INSERT** (`setscriptresultwebaction_4`)
  - `ms_stt_meta.VRS_S_FLAG = 'Y'` UPDATE (`setscriptresultwebaction_10`) ← **다음 단계 전제 FLAG**

### SVC_MS_003 — 금칙어/필수멘트 집계 (ms_search_score → ms_search_sum)

- **WebAction**: `SearchResultWebAction`
- **대상 목록 조회 조건** (`setsearchresultwebaction_11`):
  - 설계번호 단위로 `STT_FLAG='Y' AND SRC_FLAG='Y' AND BAN_S_FLAG='N'` 모두 충족 시 대상 (`ms_search_sum` 미존재)
- **처리 중 선점**: `setsearchresultwebaction_3` → `ms_stt_meta.BAN_S_FLAG = 'S'`
- **참조**: `ms_search_score` S + `ms_stt_meta` M (`setsearchresultwebaction_1`)
- **산출**:
  - **`ms_search_sum` INSERT** (`setsearchresultwebaction_2`)
  - `ms_stt_meta.BAN_S_FLAG = 'Y'` UPDATE ← **다음 단계 전제 FLAG**

### ⑥ SVC_MS_004 — 최종 등급 산정 (→ ms_grade_sum)

- **WebAction**: `GradeResultWebAction`
- **대상 조회 조건** (`setgraderesultwebaction_1_1`, TA+SRC 모두 포함 케이스):
  ```
  STT_FLAG='Y' AND TA_FLAG='Y' AND SRC_FLAG='Y'
  AND GRD_FLAG='N'
  AND VRS_S_FLAG='Y' AND BAN_S_FLAG='Y'
  AND ms_grade_sum 에 아직 없음 (G.CON_ENT_DGN_NO IS NULL)
  → 설계번호의 모든 콜이 위 조건 충족(GRD_FLAG='Y' 산출) 시 TOP(50) 대상
  ```
  - `_1_2` : TA만 포함 (`STT_FLAG='Y' AND TA_FLAG='Y' AND GRD_FLAG='N' AND VRS_S_FLAG='Y'`)
  - `_1_3` : SRC만 포함 (`STT_FLAG='Y' AND SRC_FLAG='Y' AND GRD_FLAG='N' AND BAN_S_FLAG='Y'`)
  - 어떤 `_1_x`를 쓸지는 운영 설정(`ta_yn`, `src_yn`)에 따라 결정 ([GradeResultWebAction.java:83](src/sens/src/grade/GradeResultWebAction.java))
- **산출**: 항목별 점수 × 가중치 합산 → `GRADE_SUM` → **`ms_grade_sum` INSERT** (`setgraderesultwebaction_6`)
- ※ 문장 임계값 미달이어도 **적재 여부에는 영향 없음**(0점으로 합산). 적재 게이트는 위 FLAG 조합.

---

## 3. ms_stt_meta FLAG 생명주기 (다음 단계 진입 열쇠)

| FLAG | 의미 | `N`(대기) | `S`(처리중) | `Y`(완료) | 세팅 주체 |
|------|------|-----------|-------------|-----------|-----------|
| `VA_FLAG` | (bef_meta) 녹취정보 수집/META화 | 초기 | SVC_MS_001 선점 | META 생성 완료 | SVC_MS_008 / 001 |
| `STT_FLAG` | STT 인식 | 초기 | 인식중 | 인식완료 | **외부 STT 엔진** |
| `STT_SP_FLAG` | 문장 분리(merge→rslt) | 초기 | 분리중 | 분리완료 | SVC_MS_012 |
| `TA_FLAG` | 텍스트분석(유사도) | 초기 | - | 분석완료 | **외부 TA 엔진** |
| `SRC_FLAG` | 금칙어/필수멘트 검출 | 초기 | - | 검출완료 | **외부 TA 엔진** |
| `VRS_S_FLAG` | 스크립트 준수율 집계 | 초기 | SVC_MS_002 선점 | 집계완료 | SVC_MS_002 |
| `BAN_S_FLAG` | 금칙어 준수 집계 | 초기 | SVC_MS_003 선점 | 집계완료 | SVC_MS_003 |
| `GRD_FLAG` | 등급 산정 | 초기 | - | 산정완료 | SVC_MS_004 |
| `ENC_FLAG` | CONTENT 암호화 | 초기 | 처리중 | 암호화완료 | SVC_MS_004(실시간)/014(배치) |

---

## 4. 최종 `ms_grade_sum` 적재까지 필요한 조건 종합

한 설계번호(계약)가 `ms_grade_sum`에 쌓이려면 **엮인 모든 콜**이 순서대로 아래를 모두 통과해야 합니다.

1. `VA_FLAG='Y'` — 녹취정보 수집 + META 생성 (SVC_MS_008 → 001)
2. `STT_FLAG='Y'` — STT 인식 완료 (외부)
3. `STT_SP_FLAG='Y'` — 문장 분리 완료 (SVC_MS_012)
4. `TA_FLAG='Y'` — 텍스트 분석 완료 (외부 TA)
5. `SRC_FLAG='Y'` — 금칙어/필수멘트 검출 완료 (외부 TA)
6. `VRS_S_FLAG='Y'` — 스크립트 준수율 집계 완료 (SVC_MS_002)
7. `BAN_S_FLAG='Y'` — 금칙어 준수 집계 완료 (SVC_MS_003)
8. `GRD_FLAG='N'` AND `ms_grade_sum` 미존재 — 아직 등급 미산정
9. → SVC_MS_004 가 위 전부 충족 콜만 골라 `ms_grade_sum` INSERT 후 `GRD_FLAG='Y'`

**하나라도 `N`/`S` 이면 그 단계 이후로 진행되지 않고 대기**합니다.
