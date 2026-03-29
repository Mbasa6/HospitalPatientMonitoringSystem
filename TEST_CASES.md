# Test Cases

## Functional Test Cases

| Test ID | Requirement | Description | Steps | Expected Result | Status |
|--------|------------|------------|------|----------------|--------|
| TC-001 | FR-001 | View vitals | Login → select patient | Data displays | Pass |
| TC-002 | FR-002 | Record vitals | Enter data | Saved successfully | Pass |
| TC-003 | FR-003 | Trigger alert | Input abnormal vitals | Alert generated | Pass |
| TC-004 | FR-004 | Receive alert | System sends alert | Doctor notified | Pass |
| TC-005 | FR-005 | Monitor patients | Open dashboard | Data visible | Pass |
| TC-006 | FR-006 | Manage users | Add user | User created | Pass |
| TC-007 | FR-007 | Configure system | Change settings | Updated | Pass |
| TC-008 | FR-008 | View history | Select patient | History displayed | Pass |

## Non-Functional Tests

### Performance Test
- Simulate 1000 users  
- Expected: Response time < 2 seconds  

### Security Test
- Attempt unauthorized access  
- Expected: Access denied  