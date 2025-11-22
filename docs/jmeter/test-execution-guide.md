# JMeter Test Execution Guide - QuizLive

This guide explains how to execute different types of load tests (Smoke, Load, Stress) using the QuizLive JMeter test plan.

## Test Types Overview

| Test Type | Purpose | Total Concurrent Users | Duration | When to Run |
|-----------|---------|------------------------|----------|-------------|
| **Smoke Test** | Verify basic functionality | 5-20 | ~1 minute | Before every load test |
| **Load Test** | Test under expected load | 50-75 | ~5-6 minutes | Regular performance validation |
| **Stress Test** | Find breaking point | 100-350 | ~7-10 minutes | Before major releases |

## Test Scenarios Configuration

The test plan includes 5 scenarios that run concurrently. Adjust thread counts per scenario based on test type:

### Scenario T-01: Admin Quiz CRUD
- **Smoke**: 5 users, 10s ramp, 1 step, 60s hold
- **Load**: 8 users, 20s ramp, 2 steps, 300s hold
- **Stress**: 15 users, 30s ramp, 3 steps, 300s hold

### Scenario T-02: Public Quiz Fetch
- **Smoke**: 10 users, 15s ramp, 1 step, 60s hold
- **Load**: 40 users, 45s ramp, 4 steps, 300s hold
- **Stress**: 100 users, 60s ramp, 5 steps, 300s hold

### Scenario T-03: Gateway Auth List
- **Smoke**: 5 users, 10s ramp, 1 step, 60s hold
- **Load**: 20 users, 30s ramp, 3 steps, 300s hold
- **Stress**: 50 users, 45s ramp, 4 steps, 300s hold

### Scenario T-04: Quiz Lifecycle
- **Smoke**: 3 users, 10s ramp, 1 step, 60s hold
- **Load**: 7 users, 20s ramp, 2 steps, 300s hold
- **Stress**: 15 users, 30s ramp, 3 steps, 300s hold

### Scenario T-05: WebSocket Participants
- **Smoke**: 10 users, 15s ramp, 1 step, 60s hold
- **Load**: 25 users, 30s ramp, 3 steps, 300s hold
- **Stress**: 75 users, 60s ramp, 5 steps, 300s hold

**Total Concurrent Users**:
- Smoke: ~33 users
- Load: ~100 users (but system handles ~75 effectively)
- Stress: ~255 users

## How to Adjust Test Configuration

### Option 1: Manual Configuration in JMeter GUI

1. Open `docs/jmeter/quizlive.jmx` in JMeter
2. For each Thread Group, adjust:
   - **Target Level**: Number of concurrent users
   - **Ramp Up**: Time to reach target (seconds)
   - **Steps**: Number of ramp-up steps
   - **Hold**: Duration to maintain load (seconds)
3. Save the file
4. Run the test

### Option 2: Use JMeter Properties (Recommended)

Create property files for each test type:

**`load-test/smoke-test.properties`**:
```properties
# Smoke Test Configuration
admin.crud.target=5
admin.crud.ramp=10
admin.crud.steps=1
admin.crud.hold=60

public.fetch.target=10
public.fetch.ramp=15
public.fetch.steps=1
public.fetch.hold=60

gateway.auth.target=5
gateway.auth.ramp=10
gateway.auth.steps=1
gateway.auth.hold=60

lifecycle.target=3
lifecycle.ramp=10
lifecycle.steps=1
lifecycle.hold=60

websocket.target=10
websocket.ramp=15
websocket.steps=1
websocket.hold=60
```

Then update the `.jmx` file to use `${__P(admin.crud.target,8)}` instead of hardcoded values.

### Option 3: Use Test Scripts (Easiest)

Use the provided PowerShell scripts (see below).

## Test Execution Scripts

### Smoke Test
```powershell
cd C:\Users\School\tools\apache-jmeter-5.6.3\bin
.\jmeter.bat -n `
  -t "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\load-test\quizlive.jmx" `
  -l "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\results-smoke.jtl" `
  -e -o "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\report-smoke" `
  -f
```

### Load Test
```powershell
cd C:\Users\School\tools\apache-jmeter-5.6.3\bin
.\jmeter.bat -n `
  -t "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\load-test\quizlive.jmx" `
  -l "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\results-load.jtl" `
  -e -o "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\report-load" `
  -f
```

### Stress Test
```powershell
cd C:\Users\School\tools\apache-jmeter-5.6.3\bin
.\jmeter.bat -n `
  -t "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\load-test\quizlive.jmx" `
  -l "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\results-stress.jtl" `
  -e -o "C:\Users\School\OneDrive - University of Limerick\Desktop\CS4297\QuizLive-main\out\jmeter\report-stress" `
  -f
```

**Note**: Before running stress test, update thread group configurations in JMeter GUI to stress test values.

## Pre-Test Checklist

- [ ] Docker Compose stack is running (`cd infra; docker compose up`)
- [ ] All services are healthy (check logs)
- [ ] JWT token is valid and updated in test plan
- [ ] Test data is seeded (quizzes exist in database)
- [ ] CSV file has valid quiz IDs (`load-test/data/quizzes.csv`)
- [ ] JMeter plugins are installed (Concurrency Thread Group, WebSocket Samplers)

## Post-Test Analysis

1. **Check HTML Report**: Open `out/jmeter/report-*/index.html`
2. **Review Key Metrics**:
   - Error rate (should be <1% for load test)
   - Response time (p90 should be <500ms for load test)
   - Throughput (should be stable)
3. **Identify Bottlenecks**: Look for endpoints with high response times
4. **Compare Results**: Use Excel summary to compare across test runs

## Troubleshooting

**High Error Rates**:
- Check if services are running
- Verify JWT token is valid
- Check database connection pool
- Review service logs

**Slow Response Times**:
- Monitor database query performance
- Check Redis connectivity
- Review Docker resource limits

**WebSocket Failures**:
- Verify WebSocket endpoint is accessible
- Check STOMP frame format
- Review RTS service logs

## Next Steps

After running tests:
1. Document results in `load-test-results-summary.csv`
2. Update Excel summary with new data
3. Compare against previous runs
4. Identify regression or improvements
5. Update load test report template with findings

