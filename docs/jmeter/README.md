# JMeter Load Testing Documentation

This folder contains all documentation, test plans, and results from the JMeter load testing performed on QuizLive.

## 📚 Documentation Overview

### Core Documents

1. **`load-test-report-template.md`** ⭐ **START HERE**
   - Complete load testing report aligned with standard template
   - Includes system architecture, test results, bottleneck analysis, and recommendations
   - Ready to fill in with your test results

2. **`test-execution-guide.md`**
   - Step-by-step guide for running Smoke, Load, and Stress tests
   - Pre-test checklist and post-test analysis instructions
   - Troubleshooting guide

3. **`test-scenarios-reference.md`**
   - Quick reference for all test scenarios (T-01 through T-05)
   - Configuration matrix for different test types
   - Mapping to report template sections

4. **`jmeter-load-test-plan.md`**
   - Detailed technical documentation
   - Setup instructions and prerequisites
   - Test plan structure and execution steps

### Test Files

- **`quizlive.jmx`** - The actual JMeter test plan file used for all load tests. This file is ready to use and includes:
  - User Defined Variables (HOST, PORT, BASE_URL, ACCESS_TOKEN)
  - HTTP Header Manager with JWT authentication
  - 5 Thread Groups (T-01 through T-05):
    - Admin Quiz CRUD operations
    - Public Quiz Fetch
    - Gateway Auth List
    - Quiz Lifecycle (Start/End)
    - RTS WebSocket Participants
  - Response Assertions for validation
  - JSON Extractors for data chaining
  - WebSocket Sampler configuration

### Results & Reports

- **`QuizLive_LoadTest_Summary.xlsx`** - Comprehensive Excel workbook with all test results, endpoint breakdowns, and performance comparisons across different load levels (4, 75, 100, and 350 concurrent users).
- **`load-test-results-summary.csv`** - CSV version of the test results summary for programmatic analysis.

## 🚀 Quick Start

### Prerequisites
1. Apache JMeter 5.6+ installed
2. Plugins Manager installed (for Concurrency Thread Group and WebSocket Samplers)
3. Docker Compose environment running (from `infra/` directory)
4. Valid JWT token for authentication

### Running Tests

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

### Test Types

The test plan is currently configured for **Load Test** levels. To run different test types:

- **Smoke Test**: Adjust thread counts to lower values (see `test-execution-guide.md`)
- **Load Test**: Current configuration (50-75 effective concurrent users)
- **Stress Test**: Increase thread counts to find breaking point (see `test-execution-guide.md`)

For detailed configuration, see `test-execution-guide.md`.

## 📊 Test Results Summary

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

## 📋 Test Scenarios

| Scenario ID | Name | Description |
|------------|------|-------------|
| T-01 | Admin Quiz CRUD | Complete admin workflow (create, read, update, delete) |
| T-02 | Public Quiz Fetch | Anonymous access to public quiz data |
| T-03 | Gateway Auth List | JWT authentication validation under load |
| T-04 | Quiz Lifecycle | Quiz state transitions (start/end) |
| T-05 | WebSocket Participants | Real-time WebSocket connections and STOMP messaging |

For detailed scenario information, see `test-scenarios-reference.md`.

## 📝 Notes

- The `.jmx` file contains a hardcoded JWT token. For production use, replace with a property variable or use JMeter properties.
- All test results are based on local Docker Compose environment.
- Response times and throughput may vary based on hardware and network conditions.
- Thread group comments in `.jmx` file indicate current configuration (Load Test) and provide guidance for Smoke/Stress test adjustments.

## 🔗 Related Documentation

- See `docs/qa-testing-plan.md` for broader QA strategy
- See `docs/dev-contract.md` for API contracts

## 📖 Using the Load Testing Report Template

To generate a complete load testing report:

1. Run your tests (Smoke, Load, Stress)
2. Collect results from HTML reports (`out/jmeter/report-*/index.html`)
3. Fill in `load-test-report-template.md` with your findings
4. Use `test-scenarios-reference.md` to map scenarios to template sections
5. Include screenshots and graphs from JMeter HTML reports

The template is structured to match standard load testing report requirements and includes all necessary sections.
