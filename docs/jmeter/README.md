# JMeter Load Testing Documentation

This folder contains all documentation, test plans, and results from the JMeter load testing performed on QuizLive.

## Contents

### 📄 Documentation
- **`jmeter-load-test-plan.md`** - Complete guide to the load testing setup, execution, and results. Updated to reflect the actual workflow used during testing.

### 🧪 Test Files
- **`quizlive.jmx`** - The actual JMeter test plan file used for all load tests. This file is ready to use and includes:
  - User Defined Variables (HOST, PORT, BASE_URL, ACCESS_TOKEN)
  - HTTP Header Manager with JWT authentication
  - Multiple Thread Groups:
    - Admin Quiz CRUD operations
    - Admin Quiz Lifecycle (start/end)
    - Public Quiz Fetch
    - Gateway Auth List
    - RTS WebSocket Participants
  - Response Assertions for validation
  - JSON Extractors for data chaining
  - WebSocket Sampler configuration

### 📊 Results & Reports
- **`QuizLive_LoadTest_Summary.xlsx`** - Comprehensive Excel workbook with all test results, endpoint breakdowns, and performance comparisons across different load levels (4, 75, 100, and 350 concurrent users).
- **`load-test-results-summary.csv`** - CSV version of the test results summary for programmatic analysis.

## Quick Start

### Prerequisites
1. Apache JMeter 5.6+ installed
2. Plugins Manager installed (for Concurrency Thread Group and WebSocket Samplers)
3. Docker Compose environment running (from `infra/` directory)
4. Valid JWT token for authentication

### Running the Tests

1. **Update JWT Token**: Edit `quizlive.jmx` and update the `ACCESS_TOKEN` variable in User Defined Variables, or use JMeter properties:
   ```bash
   -JACCESS_TOKEN=your_jwt_token_here
   ```

2. **GUI Mode (for testing/debugging)**:
   ```bash
   jmeter -t docs/jmeter/quizlive.jmx
   ```

3. **Non-GUI Mode (for actual load tests)**:
   ```bash
   jmeter -n -t docs/jmeter/quizlive.jmx -l out/jmeter/results.jtl -e -o out/jmeter/report -f
   ```

### Test Configuration

The test plan uses **Concurrency Thread Groups** (bzm plugin) with the following configurations:

- **Admin Quiz CRUD**: 8 concurrent users, 20s ramp-up, 2 steps, 300s hold
- **Admin Quiz Lifecycle**: 7 concurrent users, 20s ramp-up, 2 steps, 300s hold  
- **Public Quiz Fetch**: 40 concurrent users, 45s ramp-up, 4 steps, 300s hold
- **Gateway Auth List**: 20 concurrent users, 30s ramp-up, 3 steps, 300s hold
- **RTS WebSocket**: Configured but requires test data fixes

## Test Results Summary

### Load Levels Tested
1. **Low Load (4 users)**: Baseline test - Excellent performance
2. **75 Users**: Recommended production capacity - Good performance
3. **Medium Load (100 users)**: Marginal performance - Requires monitoring
4. **High Load (350 users)**: System degradation - Not recommended

### Key Findings
- System performs well up to 75 concurrent users
- HTTP endpoints show <1% error rate at moderate load
- Performance degrades significantly beyond 100 users
- Recommended production capacity: **50-75 concurrent users**

### Known Issues
- WebSocket sampler configuration needs refinement (test config issue, not system issue)
- CSV data file needs populated quiz IDs for public quiz fetch tests

## Notes

- The `.jmx` file contains a hardcoded JWT token. For production use, replace with a property variable or use JMeter properties.
- All test results are based on local Docker Compose environment.
- Response times and throughput may vary based on hardware and network conditions.

## Related Documentation

- See `docs/qa-testing-plan.md` for broader QA strategy
- See `docs/dev-contract.md` for API contracts

