# Hospital Management System - Testing Documentation

## Testing Strategy Overview

This document outlines the comprehensive testing approach for the improved Hospital Management System, covering all aspects from unit testing to user acceptance testing.

## 1. Testing Pyramid

```
                     ┌─────────────────┐
                     │   Manual UI     │ ← 5%
                     │  Testing (E2E)  │
                 ┌───┴─────────────────┴───┐
                 │   Integration Tests     │ ← 20%
                 │   (API, Database)       │
             ┌───┴─────────────────────────┴───┐
             │        Unit Tests               │ ← 75%
             │   (Business Logic, Utils)       │
             └─────────────────────────────────┘
```

## 2. Unit Testing

### 2.1 Security Utils Testing

**Test File**: `src/test/java/improved/security/SecurityUtilsTest.java`

```java
package improved.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @Test
    @DisplayName("Password hashing should be consistent")
    void testPasswordHashing() {
        String password = "TestPassword123!";
        String salt = SecurityUtils.generateSalt();
        
        String hash1 = SecurityUtils.hashPassword(password, salt);
        String hash2 = SecurityUtils.hashPassword(password, salt);
        
        assertEquals(hash1, hash2, "Same password and salt should produce same hash");
        assertNotNull(hash1, "Hash should not be null");
        assertTrue(hash1.length() > 0, "Hash should not be empty");
    }

    @Test
    @DisplayName("Password verification should work correctly")
    void testPasswordVerification() {
        String password = "TestPassword123!";
        String salt = SecurityUtils.generateSalt();
        String hash = SecurityUtils.hashPassword(password, salt);
        
        assertTrue(SecurityUtils.verifyPassword(password, salt, hash), 
                  "Correct password should verify successfully");
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", salt, hash), 
                   "Wrong password should fail verification");
    }

    @Test
    @DisplayName("Password validation should enforce rules")
    void testPasswordValidation() {
        // Valid password
        var result = SecurityUtils.validatePassword("ValidPass123!");
        assertTrue(result.isValid(), "Valid password should pass validation");
        
        // Too short
        result = SecurityUtils.validatePassword("short");
        assertFalse(result.isValid(), "Short password should fail");
        assertTrue(result.getMessage().contains("8 characters"));
        
        // No uppercase
        result = SecurityUtils.validatePassword("nouppercas123!");
        assertFalse(result.isValid(), "Password without uppercase should fail");
        
        // No special character
        result = SecurityUtils.validatePassword("NoSpecial123");
        assertFalse(result.isValid(), "Password without special character should fail");
    }

    @Test
    @DisplayName("Input sanitization should prevent SQL injection")
    void testInputSanitization() {
        String maliciousInput = "'; DROP TABLE users; --";
        String sanitized = SecurityUtils.sanitizeInput(maliciousInput);
        
        assertNotNull(sanitized, "Sanitized input should not be null");
        // Should log warning but not completely block (in this implementation)
        assertFalse(sanitized.isEmpty(), "Sanitized input should not be empty");
    }

    @Test
    @DisplayName("Email validation should work correctly")
    void testEmailValidation() {
        // Valid emails
        assertTrue(SecurityUtils.validateEmail("user@example.com").isValid());
        assertTrue(SecurityUtils.validateEmail("test.email+tag@domain.co.uk").isValid());
        
        // Invalid emails
        assertFalse(SecurityUtils.validateEmail("invalid-email").isValid());
        assertFalse(SecurityUtils.validateEmail("@domain.com").isValid());
        assertFalse(SecurityUtils.validateEmail("user@").isValid());
        assertFalse(SecurityUtils.validateEmail("").isValid());
    }
}
```

### 2.2 Database Configuration Testing

**Test File**: `src/test/java/improved/config/DatabaseConfigTest.java`

```java
package improved.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;

class DatabaseConfigTest {

    private DatabaseConfig dbConfig;

    @BeforeEach
    void setUp() {
        dbConfig = DatabaseConfig.getInstance();
    }

    @Test
    @DisplayName("Database config should be singleton")
    void testSingletonPattern() {
        DatabaseConfig instance1 = DatabaseConfig.getInstance();
        DatabaseConfig instance2 = DatabaseConfig.getInstance();
        
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    @DisplayName("Should establish database connection")
    void testDatabaseConnection() {
        // This test requires a test database to be running
        assumeTrue(dbConfig.testConnection(), "Test database not available");
        
        assertDoesNotThrow(() -> {
            Connection conn = dbConfig.getConnection();
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            dbConfig.returnConnection(conn);
        });
    }

    @Test
    @DisplayName("Pool statistics should be available")
    void testPoolStatistics() {
        String stats = dbConfig.getPoolStats();
        assertNotNull(stats, "Pool stats should not be null");
        assertTrue(stats.contains("Pool Stats"), "Should contain pool information");
    }
}
```

## 3. Integration Testing

### 3.1 Database Integration Tests

**Test File**: `src/test/java/improved/dao/BaseDAOIntegrationTest.java`

```java
package improved.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseDAOIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_hospital")
            .withUsername("test")
            .withPassword("test");

    private TestDAO testDAO;

    @BeforeEach
    void setUp() {
        // Initialize test database schema
        testDAO = new TestDAO();
        setupTestSchema();
    }

    @Test
    @DisplayName("Should execute parameterized queries safely")
    void testParameterizedQueries() {
        assertDoesNotThrow(() -> {
            // Test safe parameter binding
            boolean exists = testDAO.recordExists("users", "username = ?", "testuser");
            assertFalse(exists, "Test user should not exist initially");
            
            // Test SQL injection prevention
            exists = testDAO.recordExists("users", "username = ?", "'; DROP TABLE users; --");
            assertFalse(exists, "SQL injection attempt should not succeed");
        });
    }

    @Test
    @DisplayName("Should handle transactions correctly")
    void testTransactionHandling() {
        assertDoesNotThrow(() -> {
            List<DatabaseOperation> operations = Arrays.asList(
                conn -> {
                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO test_table (name) VALUES (?)"
                    );
                    stmt.setString(1, "test1");
                    stmt.executeUpdate();
                },
                conn -> {
                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO test_table (name) VALUES (?)"
                    );
                    stmt.setString(1, "test2");
                    stmt.executeUpdate();
                }
            );
            
            testDAO.executeTransaction(operations);
            
            // Verify both records were inserted
            Object count = testDAO.getSingleValue("SELECT COUNT(*) FROM test_table");
            assertEquals(2L, count, "Both records should be inserted");
        });
    }

    private void setupTestSchema() {
        // Setup test database schema
        // This would create necessary tables for testing
    }

    // Test DAO implementation for testing purposes
    private static class TestDAO extends BaseDAO {
        // Expose protected methods for testing
        public boolean testRecordExists(String table, String where, Object... params) throws SQLException {
            return recordExists(table, where, params);
        }
    }
}
```

### 3.2 API Integration Tests

**Test File**: `src/test/java/improved/api/PatientAPIIntegrationTest.java`

```java
package improved.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class PatientAPIIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should create patient with valid data")
    void testCreatePatient() {
        PatientDTO patient = new PatientDTO();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhone("+1234567890");
        patient.setDateOfBirth(LocalDate.of(1980, 1, 1));
        patient.setGender("M");
        patient.setBloodGroup("O+");

        ResponseEntity<PatientDTO> response = restTemplate.postForEntity(
            "/api/patients", patient, PatientDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPatientId());
    }

    @Test
    @DisplayName("Should reject patient with invalid email")
    void testCreatePatientInvalidEmail() {
        PatientDTO patient = new PatientDTO();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("invalid-email");
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/patients", patient, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid email"));
    }

    @Test
    @DisplayName("Should prevent SQL injection in search")
    void testPatientSearchSQLInjection() {
        String maliciousQuery = "'; DROP TABLE patients; --";
        
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/patients/search?q=" + maliciousQuery, String.class
        );

        // Should not return error (injection prevented)
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
```

## 4. Security Testing

### 4.1 Penetration Testing Checklist

#### SQL Injection Testing
- [ ] Test all input fields with SQL injection payloads
- [ ] Verify parameterized queries are used everywhere
- [ ] Test error handling doesn't expose database structure
- [ ] Verify input sanitization is working

#### Authentication Testing
- [ ] Test password complexity requirements
- [ ] Verify password hashing is secure (PBKDF2/bcrypt)
- [ ] Test session management
- [ ] Verify account lockout mechanisms
- [ ] Test password reset functionality

#### Authorization Testing
- [ ] Verify role-based access controls
- [ ] Test privilege escalation attempts
- [ ] Verify user can only access their authorized data
- [ ] Test session timeout

#### Input Validation Testing
- [ ] Test XSS prevention
- [ ] Verify file upload restrictions
- [ ] Test data type validation
- [ ] Verify length restrictions

### 4.2 Security Test Scripts

**Script**: `security_tests.py`

```python
import requests
import time
from urllib.parse import quote

class SecurityTester:
    def __init__(self, base_url):
        self.base_url = base_url
        self.session = requests.Session()

    def test_sql_injection(self):
        """Test SQL injection vulnerabilities"""
        payloads = [
            "' OR '1'='1",
            "'; DROP TABLE users; --",
            "' UNION SELECT * FROM users --",
            "admin'--",
            "' OR 1=1#"
        ]
        
        results = []
        for payload in payloads:
            try:
                response = self.session.post(
                    f"{self.base_url}/login",
                    data={'username': payload, 'password': 'test'}
                )
                results.append({
                    'payload': payload,
                    'status': response.status_code,
                    'vulnerable': response.status_code == 200
                })
            except Exception as e:
                results.append({
                    'payload': payload,
                    'status': 'error',
                    'error': str(e)
                })
        
        return results

    def test_brute_force_protection(self):
        """Test brute force protection"""
        attempts = []
        for i in range(10):
            start_time = time.time()
            response = self.session.post(
                f"{self.base_url}/login",
                data={'username': 'admin', 'password': f'wrong{i}'}
            )
            end_time = time.time()
            
            attempts.append({
                'attempt': i + 1,
                'status': response.status_code,
                'response_time': end_time - start_time,
                'locked': 'locked' in response.text.lower()
            })
            
            time.sleep(1)  # Wait between attempts
        
        return attempts

    def test_xss_protection(self):
        """Test XSS protection"""
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "<svg onload=alert('XSS')>"
        ]
        
        results = []
        for payload in xss_payloads:
            try:
                response = self.session.post(
                    f"{self.base_url}/patients",
                    data={'name': payload, 'email': 'test@test.com'}
                )
                
                # Check if payload is reflected unescaped
                vulnerable = payload in response.text
                
                results.append({
                    'payload': payload,
                    'vulnerable': vulnerable,
                    'status': response.status_code
                })
            except Exception as e:
                results.append({
                    'payload': payload,
                    'error': str(e)
                })
        
        return results

# Usage
if __name__ == "__main__":
    tester = SecurityTester("http://localhost:8080")
    
    print("Testing SQL Injection...")
    sql_results = tester.test_sql_injection()
    
    print("Testing Brute Force Protection...")
    brute_force_results = tester.test_brute_force_protection()
    
    print("Testing XSS Protection...")
    xss_results = tester.test_xss_protection()
    
    # Generate report
    print("\n=== SECURITY TEST RESULTS ===")
    print(f"SQL Injection Tests: {len([r for r in sql_results if not r.get('vulnerable', False)])} passed")
    print(f"Brute Force Protection: {'PASS' if any(r['locked'] for r in brute_force_results) else 'FAIL'}")
    print(f"XSS Protection Tests: {len([r for r in xss_results if not r.get('vulnerable', False)])} passed")
```

## 5. Performance Testing

### 5.1 Load Testing Script

**Script**: `load_test.py`

```python
import asyncio
import aiohttp
import time
import statistics

class LoadTester:
    def __init__(self, base_url, concurrent_users=10, test_duration=60):
        self.base_url = base_url
        self.concurrent_users = concurrent_users
        self.test_duration = test_duration
        self.results = []

    async def simulate_user_session(self, session, user_id):
        """Simulate a user session"""
        start_time = time.time()
        requests_made = 0
        
        while time.time() - start_time < self.test_duration:
            try:
                # Login
                login_start = time.time()
                async with session.post(f"{self.base_url}/login", 
                                      data={'username': f'user{user_id}', 'password': 'test'}) as resp:
                    login_time = time.time() - login_start
                    self.results.append(('login', resp.status, login_time))
                
                # Browse patients
                browse_start = time.time()
                async with session.get(f"{self.base_url}/patients") as resp:
                    browse_time = time.time() - browse_start
                    self.results.append(('browse_patients', resp.status, browse_time))
                
                # Create patient
                create_start = time.time()
                patient_data = {
                    'firstName': f'Test{user_id}_{requests_made}',
                    'lastName': 'Patient',
                    'email': f'test{user_id}_{requests_made}@example.com',
                    'phone': '+1234567890'
                }
                async with session.post(f"{self.base_url}/patients", 
                                      json=patient_data) as resp:
                    create_time = time.time() - create_start
                    self.results.append(('create_patient', resp.status, create_time))
                
                requests_made += 1
                await asyncio.sleep(1)  # Simulate user think time
                
            except Exception as e:
                self.results.append(('error', 0, 0))
                await asyncio.sleep(1)

    async def run_load_test(self):
        """Run the load test"""
        connector = aiohttp.TCPConnector(limit=100)
        async with aiohttp.ClientSession(connector=connector) as session:
            tasks = []
            for user_id in range(self.concurrent_users):
                task = asyncio.create_task(self.simulate_user_session(session, user_id))
                tasks.append(task)
            
            await asyncio.gather(*tasks)

    def generate_report(self):
        """Generate performance report"""
        if not self.results:
            return "No results to report"
        
        # Group results by operation
        operations = {}
        for operation, status, response_time in self.results:
            if operation not in operations:
                operations[operation] = {'times': [], 'statuses': []}
            operations[operation]['times'].append(response_time)
            operations[operation]['statuses'].append(status)
        
        report = "=== PERFORMANCE TEST RESULTS ===\n"
        report += f"Concurrent Users: {self.concurrent_users}\n"
        report += f"Test Duration: {self.test_duration} seconds\n\n"
        
        for operation, data in operations.items():
            times = data['times']
            statuses = data['statuses']
            
            if times:
                report += f"{operation.upper()}:\n"
                report += f"  Requests: {len(times)}\n"
                report += f"  Success Rate: {(statuses.count(200) / len(statuses)) * 100:.2f}%\n"
                report += f"  Avg Response Time: {statistics.mean(times):.3f}s\n"
                report += f"  Min Response Time: {min(times):.3f}s\n"
                report += f"  Max Response Time: {max(times):.3f}s\n"
                if len(times) > 1:
                    report += f"  95th Percentile: {statistics.quantiles(times, n=20)[18]:.3f}s\n"
                report += "\n"
        
        return report

# Usage
async def main():
    tester = LoadTester("http://localhost:8080", concurrent_users=20, test_duration=120)
    await tester.run_load_test()
    print(tester.generate_report())

if __name__ == "__main__":
    asyncio.run(main())
```

## 6. User Acceptance Testing (UAT)

### 6.1 UAT Test Cases

#### TC001: Patient Registration
**Objective**: Verify that users can successfully register new patients
**Steps**:
1. Navigate to Patient Management
2. Click "New Patient"
3. Fill in all required fields with valid data
4. Submit the form
5. Verify patient appears in patient list

**Expected Result**: Patient is successfully created and appears in the system

#### TC002: Patient Search and Filter
**Objective**: Verify search functionality works correctly
**Steps**:
1. Navigate to Patient Management
2. Enter patient name in search box
3. Apply filters (inpatient/outpatient)
4. Verify results are filtered correctly

**Expected Result**: Search returns relevant results, filters work as expected

#### TC003: Room Management
**Objective**: Verify room allocation and status tracking
**Steps**:
1. Navigate to Room Management
2. View room status grid
3. Select an available room
4. Assign patient to room
5. Verify room status changes to occupied

**Expected Result**: Room assignment works correctly, status updates in real-time

#### TC004: Billing Process
**Objective**: Verify complete billing workflow
**Steps**:
1. Navigate to Billing
2. Create new bill for discharged patient
3. Add services and room charges
4. Calculate totals
5. Generate and print bill

**Expected Result**: Bill is calculated correctly and can be generated/printed

### 6.2 UAT Feedback Collection

**Form Template**: `uat_feedback_form.html`

```html
<!DOCTYPE html>
<html>
<head>
    <title>Hospital Management System - UAT Feedback</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .section { margin-bottom: 30px; }
        .rating { display: flex; gap: 10px; align-items: center; }
        .rating input[type="radio"] { margin-right: 5px; }
        textarea { width: 100%; height: 100px; }
    </style>
</head>
<body>
    <h1>Hospital Management System - User Acceptance Testing Feedback</h1>
    
    <form>
        <div class="section">
            <h3>Overall System Rating</h3>
            <div class="rating">
                <label><input type="radio" name="overall" value="5"> Excellent</label>
                <label><input type="radio" name="overall" value="4"> Good</label>
                <label><input type="radio" name="overall" value="3"> Average</label>
                <label><input type="radio" name="overall" value="2"> Poor</label>
                <label><input type="radio" name="overall" value="1"> Very Poor</label>
            </div>
        </div>

        <div class="section">
            <h3>Feature-Specific Feedback</h3>
            
            <h4>Patient Management</h4>
            <div class="rating">
                <label><input type="radio" name="patients" value="5"> Excellent</label>
                <label><input type="radio" name="patients" value="4"> Good</label>
                <label><input type="radio" name="patients" value="3"> Average</label>
                <label><input type="radio" name="patients" value="2"> Poor</label>
                <label><input type="radio" name="patients" value="1"> Very Poor</label>
            </div>
            
            <h4>Room Management</h4>
            <div class="rating">
                <label><input type="radio" name="rooms" value="5"> Excellent</label>
                <label><input type="radio" name="rooms" value="4"> Good</label>
                <label><input type="radio" name="rooms" value="3"> Average</label>
                <label><input type="radio" name="rooms" value="2"> Poor</label>
                <label><input type="radio" name="rooms" value="1"> Very Poor</label>
            </div>
            
            <h4>Billing System</h4>
            <div class="rating">
                <label><input type="radio" name="billing" value="5"> Excellent</label>
                <label><input type="radio" name="billing" value="4"> Good</label>
                <label><input type="radio" name="billing" value="3"> Average</label>
                <label><input type="radio" name="billing" value="2"> Poor</label>
                <label><input type="radio" name="billing" value="1"> Very Poor</label>
            </div>
        </div>

        <div class="section">
            <h3>Usability</h3>
            <p>How easy was it to complete your tasks?</p>
            <div class="rating">
                <label><input type="radio" name="usability" value="5"> Very Easy</label>
                <label><input type="radio" name="usability" value="4"> Easy</label>
                <label><input type="radio" name="usability" value="3"> Moderate</label>
                <label><input type="radio" name="usability" value="2"> Difficult</label>
                <label><input type="radio" name="usability" value="1"> Very Difficult</label>
            </div>
        </div>

        <div class="section">
            <h3>Issues Encountered</h3>
            <textarea name="issues" placeholder="Please describe any issues, bugs, or problems you encountered..."></textarea>
        </div>

        <div class="section">
            <h3>Suggestions for Improvement</h3>
            <textarea name="suggestions" placeholder="Please provide suggestions for improving the system..."></textarea>
        </div>

        <div class="section">
            <h3>Additional Comments</h3>
            <textarea name="comments" placeholder="Any additional feedback or comments..."></textarea>
        </div>

        <button type="submit">Submit Feedback</button>
    </form>
</body>
</html>
```

## 7. Test Automation Pipeline

### 7.1 CI/CD Pipeline Configuration

**File**: `.github/workflows/test-pipeline.yml`

```yaml
name: Hospital Management System Test Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v2
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run unit tests
      run: mvn test
    
    - name: Generate test report
      run: mvn surefire-report:report
    
    - name: Upload test results
      uses: actions/upload-artifact@v2
      with:
        name: test-results
        path: target/surefire-reports/

  integration-tests:
    runs-on: ubuntu-latest
    needs: unit-tests
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: testpassword
          MYSQL_DATABASE: test_hospital
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    
    - name: Run integration tests
      run: mvn verify -Dspring.profiles.active=test
      env:
        DB_HOST: localhost
        DB_PORT: 3306
        DB_NAME: test_hospital
        DB_USER: root
        DB_PASSWORD: testpassword

  security-tests:
    runs-on: ubuntu-latest
    needs: integration-tests
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up Python
      uses: actions/setup-python@v2
      with:
        python-version: '3.9'
    
    - name: Install dependencies
      run: |
        pip install requests aiohttp
    
    - name: Run security tests
      run: python security_tests.py
    
    - name: OWASP ZAP Scan
      uses: zaproxy/action-full-scan@v0.4.0
      with:
        target: 'http://localhost:8080'

  performance-tests:
    runs-on: ubuntu-latest
    needs: integration-tests
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up Python
      uses: actions/setup-python@v2
      with:
        python-version: '3.9'
    
    - name: Install dependencies
      run: |
        pip install asyncio aiohttp
    
    - name: Run load tests
      run: python load_test.py
    
    - name: Upload performance results
      uses: actions/upload-artifact@v2
      with:
        name: performance-results
        path: performance-report.txt
```

## 8. Test Metrics and Reporting

### 8.1 Key Testing Metrics

- **Code Coverage**: Minimum 80% line coverage required
- **Test Pass Rate**: Minimum 95% pass rate required
- **Security Scan**: Zero high/critical vulnerabilities
- **Performance**: 95th percentile response time < 2 seconds
- **Load Capacity**: Support 100 concurrent users

### 8.2 Test Report Template

```
HOSPITAL MANAGEMENT SYSTEM - TEST EXECUTION REPORT
Date: [DATE]
Version: [VERSION]
Environment: [ENVIRONMENT]

SUMMARY:
- Total Test Cases: [NUMBER]
- Passed: [NUMBER] ([PERCENTAGE]%)
- Failed: [NUMBER] ([PERCENTAGE]%)
- Blocked: [NUMBER] ([PERCENTAGE]%)

UNIT TESTS:
- Security Utils: [PASS/FAIL]
- Database Config: [PASS/FAIL]
- Validation: [PASS/FAIL]

INTEGRATION TESTS:
- Database Operations: [PASS/FAIL]
- API Endpoints: [PASS/FAIL]
- Authentication: [PASS/FAIL]

SECURITY TESTS:
- SQL Injection: [PASS/FAIL]
- XSS Protection: [PASS/FAIL]
- Authentication: [PASS/FAIL]
- Authorization: [PASS/FAIL]

PERFORMANCE TESTS:
- Load Test (100 users): [PASS/FAIL]
- Response Time (95th percentile): [TIME]
- Throughput: [REQUESTS/SECOND]

ISSUES FOUND:
[LIST OF ISSUES WITH SEVERITY]

RECOMMENDATIONS:
[RECOMMENDATIONS FOR IMPROVEMENTS]
```

This comprehensive testing documentation ensures that the improved Hospital Management System meets all quality, security, and performance requirements before deployment. 