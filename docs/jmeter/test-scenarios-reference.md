# Test Scenarios Reference - QuizLive Load Testing

This document provides a quick reference for test scenarios and their configurations across different test types.

## Test Scenario Matrix

| Scenario ID | Name | Endpoints Tested | Smoke Config | Load Config | Stress Config |
|------------|------|------------------|--------------|-------------|---------------|
| **T-01** | Admin Quiz CRUD | `GET /qms/api/quizzes`<br>`POST /qms/api/quizzes`<br>`POST /qms/api/quizzes/{id}/questions`<br>`POST /qms/api/quizzes/{id}/start`<br>`POST /qms/api/quizzes/{id}/end`<br>`DELETE /qms/api/quizzes/{id}` | 5 users<br>10s ramp<br>1 step<br>60s hold | 8 users<br>20s ramp<br>2 steps<br>300s hold | 15 users<br>30s ramp<br>3 steps<br>300s hold |
| **T-02** | Public Quiz Fetch | `GET /qms/api/quizzes/{id}` (anonymous) | 10 users<br>15s ramp<br>1 step<br>60s hold | 40 users<br>45s ramp<br>4 steps<br>300s hold | 100 users<br>60s ramp<br>5 steps<br>300s hold |
| **T-03** | Gateway Auth List | `GET /qms/api/quizzes` (authenticated) | 5 users<br>10s ramp<br>1 step<br>60s hold | 20 users<br>30s ramp<br>3 steps<br>300s hold | 50 users<br>45s ramp<br>4 steps<br>300s hold |
| **T-04** | Quiz Lifecycle | `POST /qms/api/quizzes/{id}/start`<br>`POST /qms/api/quizzes/{id}/end` | 3 users<br>10s ramp<br>1 step<br>60s hold | 7 users<br>20s ramp<br>2 steps<br>300s hold | 15 users<br>30s ramp<br>3 steps<br>300s hold |
| **T-05** | WebSocket Participants | `ws://localhost:8080/ws/quiz/{id}`<br>STOMP: CONNECT, SUBSCRIBE, SEND | 10 users<br>15s ramp<br>1 step<br>60s hold | 25 users<br>30s ramp<br>3 steps<br>300s hold | 75 users<br>60s ramp<br>5 steps<br>300s hold |

## Total Concurrent Users by Test Type

| Test Type | Total Concurrent Users | Expected Duration |
|-----------|------------------------|-------------------|
| **Smoke** | ~33 users | ~1 minute |
| **Load** | ~100 users (system handles ~75 effectively) | ~5-6 minutes |
| **Stress** | ~255 users | ~7-10 minutes |

## Scenario Details

### T-01: Admin Quiz CRUD Operations
**Purpose**: Test complete admin workflow for quiz management

**Flow**:
1. List existing quizzes (GET)
2. Create new quiz (POST)
3. Extract quiz ID from response
4. Add question to quiz (POST)
5. Start quiz (POST)
6. End quiz (POST)
7. Delete quiz (DELETE)

**Success Criteria**:
- All requests return 200/201 status
- Quiz ID extraction successful
- No errors in workflow

### T-02: Public Quiz Fetch
**Purpose**: Test anonymous access to public quiz data

**Flow**:
1. Read quiz ID from CSV file
2. Fetch quiz details (GET, no auth)

**Success Criteria**:
- Returns 200 for valid quiz IDs
- Returns quiz data with questions
- No authentication required

### T-03: Gateway Authentication & Authorization
**Purpose**: Validate JWT authentication under load

**Flow**:
1. Send authenticated request with JWT token
2. Gateway validates token
3. Gateway forwards request to QMS
4. Return owner's quiz list

**Success Criteria**:
- JWT validation successful
- Gateway routing works correctly
- Response includes only owner's quizzes

### T-04: Quiz Lifecycle Management
**Purpose**: Test quiz state transitions (start/end)

**Flow**:
1. Start an active quiz (POST)
2. End the quiz (POST)

**Success Criteria**:
- State transitions successful
- No race conditions
- Proper error handling for invalid states

### T-05: WebSocket Real-Time Participation
**Purpose**: Test WebSocket connections and STOMP messaging

**Flow**:
1. Connect to WebSocket endpoint
2. Send STOMP CONNECT frame
3. SUBSCRIBE to leaderboard topic
4. SEND join message
5. SEND answer submission

**Success Criteria**:
- WebSocket connection established
- STOMP frames processed correctly
- Real-time updates received

**Known Issues**:
- Test configuration needs refinement
- Currently showing 100% error rate (test setup issue, not system)

## Mapping to Report Template

When filling out the load testing report template, use this mapping:

| Template Section | Source |
|------------------|--------|
| **Scenario ID** | T-01 through T-05 |
| **Description** | Scenario name from this document |
| **Expected Load Profile** | User counts from Load Config column |
| **Maximum VUs Achieved** | Actual concurrent users from test results |
| **Average Response Time** | From JMeter HTML report (per scenario) |
| **Error Rate** | From JMeter HTML report (per scenario) |
| **Did it Meet NFR?** | Compare against NFRs in report template |

## Quick Configuration Guide

To adjust test configuration in JMeter:

1. Open `load-test/quizlive.jmx` in JMeter GUI
2. Select the Thread Group (e.g., "Admin Quiz CRUD")
3. Adjust these fields:
   - **Target Level**: Number of concurrent users
   - **Ramp Up**: Time to reach target (seconds)
   - **Steps**: Number of ramp-up increments
   - **Hold**: Duration to maintain load (seconds)
4. Save and run

For detailed instructions, see `test-execution-guide.md`.

