package services;

import domain.*;
import exceptions.ValidationException;
import observer.Observable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceMaster extends Observable {
    private GradesService serviceGrades;
    private StudentsService serviceStudents;
    private HomeworkService serviceHomework;
    private YearStructure yearStructure;

    private FileType type;
    private String filePath;

    public ServiceMaster(GradesService serviceGrades, StudentsService serviceStudents, HomeworkService serviceHomework, YearStructure yr, String filePath) {
        this.serviceGrades = serviceGrades;
        this.serviceStudents = serviceStudents;
        this.serviceHomework = serviceHomework;
        this.yearStructure = yr;
        this.filePath = filePath;
        this.type = FileType.CSV;
        setFileType();
    }


    //  --STUDENT FUNCTIONS --//

    /**
     * @param id             - the id of the student
     * @param firstName      - first name of the student
     * @param lastName       - last name of the student
     * @param group          - the group number of the student
     * @param email          - the email of the student
     * @param guidingTeacher - the guiding teacher of the student
     * @return null-the student has been saved, otherwise return the student.
     * @throws ValidationException - student is not valid(eg. id invalid)
     */
    public Student addStudent(String id, String firstName, String lastName, int group, String email, String guidingTeacher) throws ValidationException, IOException {
        Student s = serviceStudents.addStudent(id, firstName, lastName, group, email, guidingTeacher);
        if (s == null)
            notifyObs();
        return s;
    }

    /**
     * @param id - the id of the student (must be not null)
     * @return null if the student doesn't exists, otherwise the student which we deleted.
     */
    public Student removeStudent(String id) throws IOException {
        Student s = serviceStudents.removeStudent(id);
        if (s != null)
            notifyObs();
        return s;
    }

    /**
     * @param id                - the id of an existing student
     * @param newFirstName      - the first name of the student
     * @param newLastName       - the last name of the student
     * @param newGroup          - the group number of the student
     * @param newEmail          - the email of the student
     * @param newGuidingTeacher - the guiding teacher of the student
     * @return null if the student has been updated, otherwise the student.
     */
    public Student updateStudent(String id, String newFirstName, String newLastName, int newGroup, String newEmail, String newGuidingTeacher) throws ValidationException, IOException {
        Student ss = findStudent(id);
        String old_file = "";
        if (ss != null) {
            old_file = ss.getFirstName() + "_" + ss.getLastName();
        }

        Student s = serviceStudents.updateStudent(id, newFirstName, newLastName, newGroup, newEmail, newGuidingTeacher);

        if (s == null) {
            renameFile(old_file, newFirstName + "_" + newLastName);
            notifyObs();
        }
        return s;
    }

    /**
     * @param id - id of the student
     * @return null if the student doesn't exists, otherwise the student
     */
    public Student findStudent(String id) {
        return serviceStudents.findStudent(id);
    }

    /**
     * @return all the students
     */
    public Iterable<Student> getAllStudents() {
        return serviceStudents.getAllStudents();
    }


//  --HOMEWORK FUNCTIONS    --//

    /**
     * @param id           - the id of the homework
     * @param description  - the description of the homework
     * @param deadlineWeek - the deadline week of the homework
     * @return null if the homework has been saved, otherwise return the homework
     * @throws ValidationException - the homework is not valid(eg. invalid id)
     */
    public Homework addHomework(String id, String description, int deadlineWeek) throws ValidationException, IOException {
        int startWeek = yearStructure.getWeekByDate(LocalDate.now());
        Homework h = serviceHomework.addHomework(id, description, startWeek, deadlineWeek);
        if (h == null)
            notifyObs();
        return h;
    }

    /**
     * @param id - the id of an existing homework
     * @return null if the homework doesn't exists, otherwise return the deleted homework
     */
    public Homework removeHomework(String id) throws IOException {
        Homework h = serviceHomework.deleteHomework(id);
        if (h != null)
            notifyObs();
        return h;
    }

    /**
     * @param id           - the id of the homeowrk
     * @param description  - the description of the homework
     * @param deadlineWeek - the deadline week of the homework
     * @return null if the homework has been updated,otherwise return the homework
     * @throws ValidationException - if the new homework is invalid.
     */
    public Homework updateHomework(String id, String description, int deadlineWeek) throws ValidationException, IOException {
        LocalDate now = LocalDate.now();

        if (deadlineWeek < yearStructure.getWeekByDate(now))
            throw new ValidationException("Invalid deadline week!");

        Homework h = serviceHomework.updateHomework(id, description, deadlineWeek);
        if (h == null)
            notifyObs();

        return h;
    }

    /**
     * @param id - the id of an existing homework
     * @return null if the homework doesn't exists, otherwise return the homework with specific id
     */
    public Homework findHomework(String id) {
        return serviceHomework.findHomework(id);
    }

    /**
     * @return all homework.
     */
    public Iterable<Homework> getAllHomework() {
        return serviceHomework.getAllHomework();
    }


//  --GRADES FUNCTIONS--    //

    /**
     * @param studentID  - the id of the existing student
     * @param homeworkID - the id of the existing homework
     * @param teacher    - the teacher which added the grade
     * @param value      - the value of the grade
     * @param feedback   - the feedback which the teacher gave it to this grade
     * @return null if the grade has been saved, otherwise return the grade
     * @throws ValidationException - the student/homework doesn't exists, or the grade is not valid
     * @throws IOException         - can't open the feedback file for a student
     */
    public Grade addGrade(String studentID, String homeworkID, String teacher, int value, String feedback, boolean isMotivated, int delayTime) throws ValidationException, IOException {

        if (!contains(value, 1, 10))
            throw new ValidationException("The value must be between 1 and 10!");


        Student s = findStudent(studentID);
        Homework h = findHomework(homeworkID);

        if (s == null)
            throw new ValidationException("The student doesn't exists");

        if (h == null)
            throw new ValidationException("The homework doesn;t exists!");


        int addingWeek = yearStructure.getWeekByDate(LocalDate.now()) - delayTime;
        int dif = getMaximumDifference(h, isMotivated, delayTime);

        if (dif > 2)
            throw new ValidationException("The deadline week for this homework has been exceeded by 3 weeks or more\n" +
                    "DeadlineWeek = " + h.getDeadlineWeek() + "\n" +
                    "AddingWeek = " + addingWeek + "\n" +
                    "Difference = " + String.valueOf(addingWeek - h.getDeadlineWeek()) + "\n");


        value -= dif;
        if (!contains(value, 1, 10))
            value = 1;


        LocalDate addingDate = yearStructure.getDateByWeek(addingWeek, yearStructure.getSemesterByDate(LocalDate.now()));
        Grade g = serviceGrades.addGrade(s, h, addingDate, teacher, value);
        if(g==null)
            notifyObs();

        return g;


    }

    /**
     * Calculate the maximum value for a grade
     *
     * @return value(between 1 and 10).
     */
    public int getMaximumDifference(Homework h, boolean isMotivated, int delayWeek) {
        return serviceGrades.getMaximumDifference(h, isMotivated, delayWeek, yearStructure.getWeekByDate(LocalDate.now()));
    }

    /**
     * @param studentID  - the id of an existing student
     * @param homeworkID - the id of an existing homework
     * @return null if the grade doesn't exists, otherwise return the deleted grade
     */
    public Grade removeGrade(String studentID, String homeworkID) {
        return serviceGrades.removeGrade(studentID, homeworkID);
    }

    /**
     * @param studentID  - the id of an existing student
     * @param homeworkID - the id of an exsting homework
     * @return null if the grade doesn't exists, otherwise return the grade
     */
    public Grade findGrade(String studentID, String homeworkID) {
        return serviceGrades.findGrade(studentID, homeworkID);
    }

    /**
     * @return all grades
     */
    public Iterable<Grade> getAllGrades() {
        return serviceGrades.getAllGrades();
    }


    //  --COMBINED FUNCTIONS--  //
    public YearStructure getYearStructure() {
        return yearStructure;
    }


    /**
     * load data from a file for every entity
     *
     * @throws IOException
     */


    public Student getStudentByGradeID(GradeID id) {
        return serviceStudents.findStudent(serviceGrades.getStudentIDByGrade(id));
    }

    public Homework getHomeworkByGradeID(GradeID id) {
        return serviceHomework.findHomework(serviceGrades.getHomeworkIDByGrade(id));
    }


    /**
     * @return the homework which is on this time, or null if is holiday or there aren't any homework on current week
     */
    public Homework getCurrentHomework() {
        LocalDate now = LocalDate.now();
        int currentWeek = yearStructure.getWeekByDate(now);
        if (currentWeek == -1)
            return null;

        for (Homework h : serviceHomework.getAllHomework()) {
            if (h.getStartWeek() <= currentWeek && h.getDeadlineWeek() >= currentWeek)
                return h;
        }
        return null;
    }


    // -- FILTER FUNCTIONS -- //


    public List<Student> allStudentsWithoutHomework(String homeworkID) {
        List<Student> ss = new ArrayList<>();
        List<Student> studentsWithHomework = allStudentsWithSpecificHomework(homeworkID);

        for (Student s : getAllStudents()) {
            if (!studentsWithHomework.contains(s))
                ss.add(s);
        }
        return ss;
    }

    /**
     * @param group_number - the group number of the students
     * @return all the students from a specific group.
     */
    public List<Student> allStudentsFromSpecificGroup(int group_number) {
        List<Student> students = new ArrayList<>();
        serviceStudents.getAllStudents().forEach(students::add);

        return students.stream().filter(s -> s.getGroup() == group_number).collect(Collectors.toList());
    }

    /**
     * @param homeworkID - the existing id of a homework
     * @return all the students which have a grade for this homework
     */
    public List<Student> allStudentsWithSpecificHomework(String homeworkID) {
        List<Grade> grades = new ArrayList<>();
        serviceGrades.getAllGrades().forEach(grades::add);

        return grades.stream().filter(g -> g.getHomework().equals(homeworkID)).map(g -> getStudentByGradeID(g.getId())).collect(Collectors.toList());
    }

    /**
     * @param homeworkID - the existing id of the homework
     * @param teacher    - the name of the teacher which gave the grade
     * @return all students whcih have a grade for this homework from a specific teacher
     */
    public List<Student> allStudentsWithSpecificHomeworkAndTeacher(String homeworkID, String teacher) {
        List<Grade> grades = new ArrayList<>();
        serviceGrades.getAllGrades().forEach(grades::add);

        return grades.stream().filter(g -> g.getHomework().equals(homeworkID) && g.getTeacher().equals(teacher)).map(g -> getStudentByGradeID(g.getId())).collect(Collectors.toList());
    }

    /**
     * @param homeworkID - the existing id of the homework
     * @param week       - the week in which the grade has been added
     * @return all the grades for a specific homework which have been added in a specific week.
     */
    public List<Grade> allGradesFromSpecificHomeworkAndWeek(String homeworkID, int week) {
        List<Grade> grades = new ArrayList<>();
        serviceGrades.getAllGrades().forEach(grades::add);
        return grades.stream().filter(g -> g.getHomework().equals(homeworkID) && yearStructure.getWeekByDate(g.getDate()) == week).collect(Collectors.toList());
    }


    /**
     * @param old_name - the current name of the file
     * @param new_name - the new name of the file
     * @return true, the file has been renamed, false otherwise.
     */
    public boolean renameFile(String old_name, String new_name) {
        File f1 = new File("src/sample/infoData/" + old_name + ".txt");
        File f2 = new File("src/sample/infoData/" + new_name + ".txt");

        return f1.renameTo(f2);
    }


    public boolean deleteFile(String filename) {
        File f = new File("src/sample/infoData/" + filename + ".txt");
        return f.delete();
    }


    /**
     * Verifies if x is between 2 values.
     *
     * @param x   -> the value which we want to verify
     * @param min -> the value for which we evaluate x>=min
     * @param max -> the value for which we evaluate x<=max
     * @return true-> if min<=x<=max, false otherwise.
     */
    private boolean contains(int x, int min, int max) {
        return x >= min && x <= max;
    }


    private void setFileType() {
        String[] path = filePath.split("/");
        String fileType = path[path.length - 1];
        if (fileType.equals("xmlFiles"))
            type = FileType.XML;

        else
            type = FileType.CSV;

    }


    public void saveData(Entities entityType) throws IOException {
        if (entityType == Entities.STUDENT || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceStudents.writeToFile(filePath);
            if (type == FileType.XML)
                serviceStudents.writeToXmlFile(filePath);
        }

        if (entityType == Entities.HOMEWORK || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceHomework.writeToFile(filePath);
            if (type == FileType.XML)
                serviceHomework.writeToXmlFile(filePath);
        }

        if (entityType == Entities.GRADE || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceGrades.writeToFile(filePath);
            if (type == FileType.XML)
                serviceGrades.writeToXmlFile(filePath);
        }
    }

    public void loadData(Entities entityType) throws IOException {
        if (entityType == Entities.STUDENT || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceStudents.readFromFile(filePath);
            if (type == FileType.XML)
                serviceStudents.readFromXmlFile(filePath);
        }

        if (entityType == Entities.HOMEWORK || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceHomework.readFromFile(filePath);
            if (type == FileType.XML)
                serviceHomework.readFromXmlFile(filePath);
        }

        if (entityType == Entities.GRADE || entityType == Entities.ALL) {
            if (type == FileType.CSV)
                serviceGrades.readFromFile(filePath);
            if (type == FileType.XML)
                serviceGrades.readFromXmlFile(filePath);
        }
    }
}