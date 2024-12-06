import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class NewClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        System.out.print("Enter the number of students: ");
        int soLuongHocSinh = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < soLuongHocSinh; i++) {
            System.out.println("Enter information for student number " + (i + 1));
            int id;
            while (true) {
                System.out.print("ID: ");
                id = scanner.nextInt();
                scanner.nextLine();
                if (manager.isIdExists(id)) {
                    System.out.println("ID already exists. Please enter a unique ID.");
                } else {
                    break;
                }
            }

            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Score: ");
            double diem = scanner.nextDouble();
            if (diem > 10.0) {
                System.out.println("Score cannot exceed 10.0. Please enter a valid score.");
                i--;
            } else {
                manager.addStudent(new Student(id, name, diem));
            }
        }

        int choice;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Display the list of students");
            System.out.println("2. Add a student");
            System.out.println("3. Edit student information");
            System.out.println("4. Delete a student");
            System.out.println("5. Search for a student by ID");
            System.out.println("6. Undo the last operation");
            System.out.println("7. Exit");
            System.out.print("Select: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manager.sortStudents();
                    manager.displayStudents();
                    break;
                case 2:
                    System.out.print("Enter ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    while (manager.isIdExists(id)) {
                        System.out.println("ID already exists. Please enter a unique ID.");
                        id = scanner.nextInt();
                        scanner.nextLine();
                    }
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Score: ");
                    double diem = scanner.nextDouble();
                    if (diem > 10.0) {
                        System.out.println("Score cannot exceed 10.0.");
                    } else {
                        manager.addStudent(new Student(id, name, diem));
                    }
                    break;
                case 3:
                    System.out.print("Enter the ID of the student to edit: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (!manager.isIdExists(id)) {
                        System.out.println("Student with ID " + id + " not found. Please try again.");
                        break;
                    }
                    System.out.print("Enter a new name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter the new score: ");
                    diem = scanner.nextDouble();
                    if (diem > 10.0) {
                        System.out.println("Score cannot exceed 10.0.");
                    } else {
                        manager.editStudent(id, name, diem);
                    }
                    break;
                case 4:
                    System.out.print("Enter the ID of the student to delete: ");
                    id = scanner.nextInt();
                    if (!manager.isIdExists(id)) {
                        System.out.println("Student with ID " + id + " not found. Cannot delete.");
                        break;
                    }
                    manager.deleteStudent(id);
                    break;
                case 5:
                    System.out.print("Enter the ID of the student to search for: ");
                    id = scanner.nextInt();
                    Student found = manager.findStudent(id);
                    if (found != null) {
                        System.out.println(found);
                    } else {
                        System.out.println("Student with ID " + id + " not found.");
                    }
                    break;
                case 6:
                    manager.undo();
                    break;
                case 7:
                    System.out.println("Exit the program...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);

        scanner.close();
    }
}

class Student {
    private int id;
    private String name;
    private double diem;
    private String rank;

    public Student(int id, String name, double diem) {
        this.id = id;
        this.name = name;
        this.diem = diem;
        this.rank = xepHang(diem);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiem() {
        return diem;
    }

    public void setDiem(double diem) {
        this.diem = diem;
        this.rank = xepHang(diem);
    }

    public String getRank() {
        return rank;
    }

    private String xepHang(double diem) {
        if (diem < 5.0) {
            return "Failed";
        } else if (diem < 6.5) {
            return "Average";
        } else if (diem < 7.5) {
            return "Good";
        } else if (diem < 9.0) {
            return "Very good";
        } else {
            return "Excellent";
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Diem: " + diem + ", Rank: " + rank;
    }
}

class StudentManager {
    private ArrayList<Student> students;
    private Stack<UndoOperation> undoStack;

    public StudentManager() {
        students = new ArrayList<>();
        undoStack = new Stack<>();
    }

    public void addStudent(Student student) {
        students.add(student);
        undoStack.push(new UndoOperation("add", student));
    }

    public void editStudent(int id, String name, double diem) {
        for (Student student : students) {
            if (student.getId() == id) {
                undoStack.push(new UndoOperation("edit", new Student(student.getId(), student.getName(), student.getDiem())));
                student.setName(name);
                student.setDiem(diem);
                return;
            }
        }
        System.out.println("Student not found with the given ID " + id);
    }

    public void deleteStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                undoStack.push(new UndoOperation("delete", student));
                students.remove(student);
                return;
            }
        }
        System.out.println("Student with ID " + id + " not found. Cannot delete.");
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("No operations to undo.");
            return;
        }
        UndoOperation lastOperation = undoStack.pop();
        switch (lastOperation.getOperationType()) {
            case "add":
                students.removeIf(s -> s.getId() == lastOperation.getStudent().getId());
                break;
            case "edit":
                Student original = lastOperation.getStudent();
                for (Student student : students) {
                    if (student.getId() == original.getId()) {
                        student.setName(original.getName());
                        student.setDiem(original.getDiem());
                        return;
                    }
                }
                break;
            case "delete":
                students.add(lastOperation.getStudent());
                break;
        }
    }

    public boolean isIdExists(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void sortStudents() {
        for (int i = 1; i < students.size(); i++) {
            Student key = students.get(i);
            int j = i - 1;
            while (j >= 0 && students.get(j).getDiem() < key.getDiem()) {
                students.set(j + 1, students.get(j));
                j--;
            }
            students.set(j + 1, key);
        }
    }

    public Student findStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        System.out.println("Student not found!");
        return null;
    }

    public void displayStudents() {
        for (Student student : students) {
            System.out.println(student);
        }
    }
}

class UndoOperation {
    private String operationType;
    private Student student;

    public UndoOperation(String operationType, Student student) {
        this.operationType = operationType;
        this.student = student;
    }

    public String getOperationType() {
        return operationType;
    }

    public Student getStudent() {
        return student;
    }
}
