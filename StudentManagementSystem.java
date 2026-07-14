// ============================================================
// STUDENT MANAGEMENT SYSTEM - SINGLE FILE VERSION
// ============================================================
// This is a complete Student Management System in one file.
// Copy and paste this code, then compile and run.
// ============================================================

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// ============================================================
// MAIN CLASS
// ============================================================
public class StudentManagementSystem {
    public static void main(String[] args) {
        // Initialize repositories
        StudentRepository studentRepo = new InMemoryStudentRepository();
        CourseRepository courseRepo = new InMemoryCourseRepository();
        GradeRepository gradeRepo = new InMemoryGradeRepository();
        
        // Initialize services
        GPACalculator gpaCalc = new GPACalculator(gradeRepo, studentRepo);
        StudentService studentService = new StudentService(studentRepo);
        CourseService courseService = new CourseService(courseRepo);
        GradeService gradeService = new GradeService(gradeRepo, studentRepo, courseRepo, gpaCalc);
        
        // Initialize UI
        ConsoleUI ui = new ConsoleUI(studentService, courseService, gradeService);
        ui.start();
    }
}

// ============================================================
// MODEL CLASSES
// ============================================================

class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private List<Course> enrolledCourses;
    private List<Grade> grades;
    
    public Student() {
        this.enrolledCourses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }
    
    public Student(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public List<Course> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<Course> enrolledCourses) { this.enrolledCourses = enrolledCourses; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    
    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Email: %s", id, getFullName(), email);
    }
}

class Course {
    private Long id;
    private String code;
    private String name;
    private Integer credits;
    private String description;
    
    public Course() {}
    
    public Course(String code, String name, Integer credits) {
        this.code = code;
        this.name = name;
        this.credits = credits;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d credits)", code, name, credits);
    }
}

class Grade {
    private Long id;
    private Student student;
    private Course course;
    private String gradeValue;
    private String semester;
    private LocalDateTime assignedDate;
    
    public Grade() {
        this.assignedDate = LocalDateTime.now();
    }
    
    public Grade(Student student, Course course, String gradeValue, String semester) {
        this();
        this.student = student;
        this.course = course;
        this.gradeValue = gradeValue;
        this.semester = semester;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public String getGradeValue() { return gradeValue; }
    public void setGradeValue(String gradeValue) { this.gradeValue = gradeValue; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
    
    @Override
    public String toString() {
        return String.format("%s - %s: %s (%s)", student.getFullName(), course.getName(), gradeValue, semester);
    }
}

// ============================================================
// REPOSITORY INTERFACES
// ============================================================

interface StudentRepository {
    Student save(Student student);
    Optional<Student> findById(Long id);
    List<Student> findAll();
    void deleteById(Long id);
    List<Student> search(String query);
}

interface CourseRepository {
    Course save(Course course);
    Optional<Course> findById(Long id);
    List<Course> findAll();
    void deleteById(Long id);
    Optional<Course> findByCode(String code);
}

interface GradeRepository {
    Grade save(Grade grade);
    Optional<Grade> findById(Long id);
    List<Grade> findAll();
    List<Grade> findByStudentId(Long studentId);
    void deleteById(Long id);
}

// ============================================================
// IN-MEMORY REPOSITORY IMPLEMENTATIONS
// ============================================================

class InMemoryStudentRepository implements StudentRepository {
    private final Map<Long, Student> studentMap = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(nextId++);
        }
        studentMap.put(student.getId(), student);
        return student;
    }
    
    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(studentMap.get(id));
    }
    
    @Override
    public List<Student> findAll() {
        return new ArrayList<>(studentMap.values());
    }
    
    @Override
    public void deleteById(Long id) {
        studentMap.remove(id);
    }
    
    @Override
    public List<Student> search(String query) {
        List<Student> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Student s : studentMap.values()) {
            if (s.getFullName().toLowerCase().contains(lowerQuery) ||
                s.getEmail().toLowerCase().contains(lowerQuery)) {
                results.add(s);
            }
        }
        return results;
    }
}

class InMemoryCourseRepository implements CourseRepository {
    private final Map<Long, Course> courseMap = new HashMap<>();
    private final Map<String, Course> courseCodeMap = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(nextId++);
        }
        courseMap.put(course.getId(), course);
        courseCodeMap.put(course.getCode(), course);
        return course;
    }
    
    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(courseMap.get(id));
    }
    
    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courseMap.values());
    }
    
    @Override
    public void deleteById(Long id) {
        Course course = courseMap.remove(id);
        if (course != null) {
            courseCodeMap.remove(course.getCode());
        }
    }
    
    @Override
    public Optional<Course> findByCode(String code) {
        return Optional.ofNullable(courseCodeMap.get(code));
    }
}

class InMemoryGradeRepository implements GradeRepository {
    private final Map<Long, Grade> gradeMap = new HashMap<>();
    private final Map<Long, List<Grade>> studentGrades = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Grade save(Grade grade) {
        if (grade.getId() == null) {
            grade.setId(nextId++);
        }
        gradeMap.put(grade.getId(), grade);
        studentGrades.computeIfAbsent(grade.getStudent().getId(), k -> new ArrayList<>()).add(grade);
        return grade;
    }
    
    @Override
    public Optional<Grade> findById(Long id) {
        return Optional.ofNullable(gradeMap.get(id));
    }
    
    @Override
    public List<Grade> findAll() {
        return new ArrayList<>(gradeMap.values());
    }
    
    @Override
    public List<Grade> findByStudentId(Long studentId) {
        return studentGrades.getOrDefault(studentId, new ArrayList<>());
    }
    
    @Override
    public void deleteById(Long id) {
        Grade grade = gradeMap.remove(id);
        if (grade != null) {
            List<Grade> grades = studentGrades.get(grade.getStudent().getId());
            if (grades != null) {
                grades.removeIf(g -> g.getId().equals(id));
            }
        }
    }
}

// ============================================================
// SERVICE CLASSES
// ============================================================

class StudentService {
    private final StudentRepository repository;
    
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }
    
    public Student addStudent(Student student) {
        if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (student.getLastName() == null || student.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return repository.save(student);
    }
    
    public Student updateStudent(Long id, Student studentDetails) {
        Student existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        
        if (studentDetails.getFirstName() != null) existing.setFirstName(studentDetails.getFirstName());
        if (studentDetails.getLastName() != null) existing.setLastName(studentDetails.getLastName());
        if (studentDetails.getEmail() != null) existing.setEmail(studentDetails.getEmail());
        if (studentDetails.getPhone() != null) existing.setPhone(studentDetails.getPhone());
        if (studentDetails.getDateOfBirth() != null) existing.setDateOfBirth(studentDetails.getDateOfBirth());
        
        return repository.save(existing);
    }
    
    public void deleteStudent(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new RuntimeException("Student not found with ID: " + id);
        }
        repository.deleteById(id);
    }
    
    public Optional<Student> getStudentById(Long id) {
        return repository.findById(id);
    }
    
    public List<Student> getAllStudents() {
        return repository.findAll();
    }
    
    public List<Student> searchStudents(String query) {
        return repository.search(query);
    }
}

class CourseService {
    private final CourseRepository repository;
    
    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }
    
    public Course addCourse(Course course) {
        if (course.getCode() == null || course.getCode().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (course.getName() == null || course.getName().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (course.getCredits() == null || course.getCredits() <= 0) {
            throw new IllegalArgumentException("Credits must be greater than 0");
        }
        return repository.save(course);
    }
    
    public List<Course> getAllCourses() {
        return repository.findAll();
    }
    
    public Optional<Course> getCourseByCode(String code) {
        return repository.findByCode(code);
    }
}

class GradeService {
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GPACalculator gpaCalculator;
    
    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository,
                       CourseRepository courseRepository, GPACalculator gpaCalculator) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.gpaCalculator = gpaCalculator;
    }
    
    public Grade assignGrade(Long studentId, String courseCode, String gradeValue, String semester) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        
        Course course = courseRepository.findByCode(courseCode)
            .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
        
        Grade grade = new Grade(student, course, gradeValue, semester);
        return gradeRepository.save(grade);
    }
    
    public List<Grade> getStudentGrades(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }
    
    public double calculateGPA(Long studentId) {
        return gpaCalculator.calculateGPA(studentId);
    }
}

class GPACalculator {
    private final Map<String, Double> gradePoints;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    
    public GPACalculator(GradeRepository gradeRepository, StudentRepository studentRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.gradePoints = new HashMap<>();
        initializeGradePoints();
    }
    
    private void initializeGradePoints() {
        gradePoints.put("A", 4.0);
        gradePoints.put("A+", 4.3);
        gradePoints.put("A-", 3.7);
        gradePoints.put("B", 3.0);
        gradePoints.put("B+", 3.3);
        gradePoints.put("B-", 2.7);
        gradePoints.put("C", 2.0);
        gradePoints.put("C+", 2.3);
        gradePoints.put("C-", 1.7);
        gradePoints.put("D", 1.0);
        gradePoints.put("D+", 1.3);
        gradePoints.put("D-", 0.7);
        gradePoints.put("F", 0.0);
    }
    
    public double calculateGPA(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        if (grades.isEmpty()) return 0.0;
        
        double totalPoints = 0.0;
        int totalCredits = 0;
        
        for (Grade grade : grades) {
            Double points = gradePoints.get(grade.getGradeValue().toUpperCase());
            if (points == null) continue;
            int credits = grade.getCourse().getCredits();
            totalPoints += points * credits;
            totalCredits += credits;
        }
        
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }
}

// ============================================================
// UI CLASS
// ============================================================

class ConsoleUI {
    private final StudentService studentService;
    private final CourseService courseService;
    private final GradeService gradeService;
    private final Scanner scanner;
    
    public ConsoleUI(StudentService studentService, CourseService courseService, GradeService gradeService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.gradeService = gradeService;
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            handleMenuChoice(choice);
        }
    }
    
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  STUDENT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("  1. Student Management");
        System.out.println("  2. Course Management");
        System.out.println("  3. Grade Management");
        System.out.println("  4. Reports");
        System.out.println("  5. Exit");
        System.out.println("=".repeat(50));
    }
    
    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                studentMenu();
                break;
            case 2:
                courseMenu();
                break;
            case 3:
                gradeMenu();
                break;
            case 4:
                reportsMenu();
                break;
            case 5:
                System.out.println("Thank you for using Student Management System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void studentMenu() {
        while (true) {
            System.out.println("\nSTUDENT MANAGEMENT");
            System.out.println("  1. Add Student");
            System.out.println("  2. View All Students");
            System.out.println("  3. Search Student");
            System.out.println("  4. Update Student");
            System.out.println("  5. Delete Student");
            System.out.println("  6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    
    private void addStudent() {
        System.out.println("\nADD NEW STUDENT");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        
        Student student = new Student(firstName, lastName, email);
        student.setPhone(phone);
        
        try {
            Student saved = studentService.addStudent(student);
            System.out.println("Student added successfully! ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewAllStudents() {
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        System.out.println("\nALL STUDENTS");
        System.out.println("-".repeat(60));
        System.out.printf("%-5s %-25s %-25s%n", "ID", "Name", "Email");
        System.out.println("-".repeat(60));
        for (Student s : students) {
            System.out.printf("%-5d %-25s %-25s%n", s.getId(), s.getFullName(), s.getEmail());
        }
        System.out.println("-".repeat(60));
    }
    
    private void searchStudent() {
        System.out.print("\nEnter search term (name or email): ");
        String query = scanner.nextLine();
        List<Student> results = studentService.searchStudents(query);
        
        if (results.isEmpty()) {
            System.out.println("No students found matching: " + query);
            return;
        }
        
        System.out.println("\nSEARCH RESULTS");
        System.out.println("-".repeat(60));
        System.out.printf("%-5s %-25s %-25s%n", "ID", "Name", "Email");
        System.out.println("-".repeat(60));
        for (Student s : results) {
            System.out.printf("%-5d %-25s %-25s%n", s.getId(), s.getFullName(), s.getEmail());
        }
        System.out.println("-".repeat(60));
    }
    
    private void updateStudent() {
        System.out.print("\nEnter Student ID to update: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
        Student student = studentService.getStudentById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        System.out.println("Current: " + student);
        System.out.println("\nLeave blank to keep current value");
        System.out.print("New First Name (" + student.getFirstName() + "): ");
        String firstName = scanner.nextLine();
        if (!firstName.isEmpty()) student.setFirstName(firstName);
        
        System.out.print("New Last Name (" + student.getLastName() + "): ");
        String lastName = scanner.nextLine();
        if (!lastName.isEmpty()) student.setLastName(lastName);
        
        System.out.print("New Email (" + student.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) student.setEmail(email);
        
        try {
            studentService.updateStudent(id, student);
            System.out.println("Student updated successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void deleteStudent() {
        System.out.print("\nEnter Student ID to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            try {
                studentService.deleteStudent(id);
                System.out.println("Student deleted successfully!");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void courseMenu() {
        while (true) {
            System.out.println("\nCOURSE MANAGEMENT");
            System.out.println("  1. Add Course");
            System.out.println("  2. View All Courses");
            System.out.println("  3. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addCourse();
                    break;
                case 2:
                    viewAllCourses();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    
    private void addCourse() {
        System.out.println("\nADD NEW COURSE");
        System.out.print("Course Code: ");
        String code = scanner.nextLine();
        System.out.print("Course Name: ");
        String name = scanner.nextLine();
        System.out.print("Credits: ");
        int credits = scanner.nextInt();
        scanner.nextLine();
        
        Course course = new Course(code, name, credits);
        try {
            Course saved = courseService.addCourse(course);
            System.out.println("Course added successfully! ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        
        System.out.println("\nALL COURSES");
        System.out.println("-".repeat(60));
        System.out.printf("%-10s %-30s %-10s%n", "Code", "Name", "Credits");
        System.out.println("-".repeat(60));
        for (Course c : courses) {
            System.out.printf("%-10s %-30s %-10d%n", c.getCode(), c.getName(), c.getCredits());
        }
        System.out.println("-".repeat(60));
    }
    
    private void gradeMenu() {
        while (true) {
            System.out.println("\nGRADE MANAGEMENT");
            System.out.println("  1. Assign Grade");
            System.out.println("  2. View Student Grades");
            System.out.println("  3. Calculate GPA");
            System.out.println("  4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    assignGrade();
                    break;
                case 2:
                    viewStudentGrades();
                    break;
                case 3:
                    calculateGPA();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    
    private void assignGrade() {
        System.out.println("\nASSIGN GRADE");
        System.out.print("Student ID: ");
        Long studentId = scanner.nextLong();
        scanner.nextLine();
        
        System.out.println("\nAvailable Courses:");
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            System.out.println("  " + c.getCode() + " - " + c.getName());
        }
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine();
        System.out.print("Grade (A, B, C, D, F): ");
        String gradeValue = scanner.nextLine().toUpperCase();
        System.out.print("Semester (Fall 2024, Spring 2025, etc.): ");
        String semester = scanner.nextLine();
        
        try {
            Grade grade = gradeService.assignGrade(studentId, courseCode, gradeValue, semester);
            System.out.println("Grade assigned successfully! ID: " + grade.getId());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewStudentGrades() {
        System.out.print("\nEnter Student ID: ");
        Long studentId = scanner.nextLong();
        scanner.nextLine();
        
        List<Grade> grades = gradeService.getStudentGrades(studentId);
        if (grades.isEmpty()) {
            System.out.println("No grades found for this student.");
            return;
        }
        
        System.out.println("\nSTUDENT GRADES");
        System.out.println("-".repeat(60));
        System.out.printf("%-20s %-10s %-10s%n", "Course", "Grade", "Semester");
        System.out.println("-".repeat(60));
        for (Grade g : grades) {
            System.out.printf("%-20s %-10s %-10s%n", g.getCourse().getName(), g.getGradeValue(), g.getSemester());
        }
        System.out.println("-".repeat(60));
    }
    
    private void calculateGPA() {
        System.out.print("\nEnter Student ID: ");
        Long studentId = scanner.nextLong();
        scanner.nextLine();
        
        try {
            double gpa = gradeService.calculateGPA(studentId);
            System.out.printf("GPA: %.2f%n", gpa);
            
            if (gpa >= 4.0) System.out.println("Excellent! (A+)");
            else if (gpa >= 3.5) System.out.println("Very Good! (A)");
            else if (gpa >= 3.0) System.out.println("Good! (B)");
            else if (gpa >= 2.0) System.out.println("Satisfactory (C)");
            else if (gpa >= 1.0) System.out.println("Needs Improvement (D)");
            else System.out.println("Failing (F)");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void reportsMenu() {
        while (true) {
            System.out.println("\nREPORTS");
            System.out.println("  1. Show All Students");
            System.out.println("  2. Show All Courses");
            System.out.println("  3. Student GPA Report");
            System.out.println("  4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    viewAllStudents();
                    break;
                case 2:
                    viewAllCourses();
                    break;
                case 3:
                    gpaReport();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
    
    private void gpaReport() {
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        System.out.println("\nGPA REPORT");
        System.out.println("-".repeat(60));
        System.out.printf("%-5s %-25s %-15s%n", "ID", "Name", "GPA");
        System.out.println("-".repeat(60));
        for (Student s : students) {
            double gpa = gradeService.calculateGPA(s.getId());
            System.out.printf("%-5d %-25s %-15.2f%n", s.getId(), s.getFullName(), gpa);
        }
        System.out.println("-".repeat(60));
    }
    
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
            System.out.print(prompt);
        }
        return scanner.nextInt();
    }
}