
import java.util.ArrayList;

public class StudentService {
    private final ArrayList<Student> students = new ArrayList<>();

    // 1. Reject null students and duplicate or invalid IDs
    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }

        if (student.getId() <= 0) {
            throw new IllegalArgumentException("Student ID must be positive.");
        }

        if (findStudentById(student.getId()) != null) {
            throw new IllegalArgumentException("Student ID already exists.");
        }

        students.add(student);
    }

    // 2. Return null when no student is found
    public Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    // Returns a copy, protecting the internal list
    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // 3. Reject null updates and invalid IDs
    public boolean updateStudent(Student updatedStudent) {
        if (updatedStudent == null) {
            throw new IllegalArgumentException(
                    "Updated student cannot be null."
            );
        }

        if (updatedStudent.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Student ID must be positive."
            );
        }

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == updatedStudent.getId()) {
                students.set(i, updatedStudent);
                return true;
            }
        }

        return false;
    }

    // 4. Delete the matching student
    public boolean deleteStudent(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == id) {
                students.remove(i);
                return true;
            }
        }
        return false;
    }
}
