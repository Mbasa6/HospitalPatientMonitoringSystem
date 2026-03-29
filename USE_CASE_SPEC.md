
---

# 📁 FILE 2: `USE_CASE_SPEC.md`

```markdown
# Use Case Specifications

## 1. View Patient Vitals
Actor: Doctor  
Precondition: Logged in  
Postcondition: Vitals displayed  

Basic Flow:
1. Doctor logs in  
2. Selects patient  
3. System displays vitals  

Alternative:
- Patient not found → error  

---

## 2. Record Patient Vitals
Actor: Nurse  
Precondition: Logged in  
Postcondition: Data saved  

Basic Flow:
1. Enter vitals  
2. Validate data  
3. Save to system  

Alternative:
- Invalid data → error  

---

## 3. Receive Alert
Actor: Doctor  
Precondition: System running  
Postcondition: Alert displayed  

Basic Flow:
1. System detects abnormal vitals  
2. Alert sent to doctor  

---

## 4. Trigger Alert
Actor: Nurse  
Precondition: Logged in  
Postcondition: Alert triggered  

Basic Flow:
1. Nurse identifies abnormal vitals  
2. Triggers alert  

---

## 5. Monitor Patients
Actor: Nurse  
Precondition: Logged in  
Postcondition: Patients monitored  

Basic Flow:
1. Open dashboard  
2. View patient list  

---

## 6. Manage Users
Actor: Admin  
Precondition: Logged in  
Postcondition: User updated  

Basic Flow:
1. Add/edit/remove user  

---

## 7. Configure System
Actor: Admin  
Precondition: Logged in  
Postcondition: Settings updated  

Basic Flow:
1. Change system settings  

---

## 8. Review Patient History
Actor: Doctor  
Precondition: Logged in  
Postcondition: History displayed  

Basic Flow:
1. Select patient  
2. View history  