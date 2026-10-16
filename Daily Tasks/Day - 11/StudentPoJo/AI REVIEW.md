# AI Code Review — Day 11: AI Integration (Core Java)

## 1. Student POJO: Vague Prompt vs. Specific Prompt

| Issue                              | Expected Behavior                    | Actual Output                           | Fix                                                                              |
|------------------------------------|--------------------------------------|-----------------------------------------|----------------------------------------------------------------------------------|
| Specific prompt not fully followed | Validate that student ID is positive | `setId()` accepts zero and negative IDs | Add validation to `setId()` and update the default constructor to use a valid ID |
| Missing requested comments         | Explain important design decisions   | No explanatory comments were generated  | Add comments where they clarify important design choices                         |

## 2. StudentService: Issues Found and Fixed

| Issue                             | Original Code                                 | Fix Applied                                              |
|-----------------------------------|-----------------------------------------------|----------------------------------------------------------|
| Null student accepted             | `students.add(student)`                       | Check for null before adding a student                   |
| Invalid student ID accepted       | No ID validation before adding                | Reject IDs less than or equal to zero                    |
| Duplicate IDs allowed             | Adds every student directly                   | Check for an existing ID before adding                   |
| Null update causes exception      | Accesses `updatedStudent.getId()` immediately | Validate the updated student before accessing its fields |
| Invalid ID accepted during update | No ID validation during updates               | Reject non-positive IDs before updating                  |

## 3. Review Summary

The AI-generated code provided a useful starting point, but it required additional review to ensure that student IDs were valid and unique and that null inputs were handled safely.

The vague and specific prompts generated similar POJO implementations. The specific prompt did not fully satisfy the requested ID validation and comments, demonstrating the importance of checking AI-generated code against the requirements.

The StudentService implementation was improved by adding input validation, duplicate-ID checks, and safer update handling.

**Verification:** Compile and test the corrected code to confirm that the fixes work as expected.

## 4. Test Cases

| No. | Test Case                               | Expected Result                   | Status  |
|----:|-----------------------------------------|-----------------------------------|---------|
|   1 | Add a valid student                     | Student is added successfully     | Pending |
|   2 | Add a null student                      | Throws `IllegalArgumentException` | Pending |
|   3 | Add two students with the same ID       | Second student is rejected        | Pending |
|   4 | Update with a null student              | Throws `IllegalArgumentException` | Pending |
|   5 | Update a student with a non-existing ID | Returns `false`                   | Pending |
|   6 | Delete an existing student              | Returns `true`                    | Pending |

**Note:** Update the status to `Passed` or `Failed` after running each test.

## 5. Additional Observation

The original `Student` POJO allows zero and negative IDs through its `setId()` method. Although `StudentService` now validates IDs when adding and updating students, the POJO should also enforce the positive-ID rule to maintain consistency.

## 6. Final Deliverables Checklist

- [ ] `Student.java` — vague prompt version
- [ ] `StudentSpecific.java` — specific prompt version
- [ ] `StudentService.java` — corrected implementation
- [ ] `AI-REVIEW.md` — prompt comparison, issues, fixes, and test results
- [ ] `AI-USAGE.md` — at least three actual AI-usage entries
- [ ] Compile the Java files successfully
- [ ] Run the test cases and update their statuses
- [ ] Create the Git commit: `day-11: ai-integration-core-java`