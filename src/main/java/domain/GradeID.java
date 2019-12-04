package domain;

import java.util.Objects;

public class GradeID{
    private String studentID;
    private String homeworkID;

    public GradeID(String studentID, String homeworkID) {
        this.studentID = studentID;
        this.homeworkID = homeworkID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getHomeworkID() {
        return homeworkID;
    }

    public void setHomeworkID(String homeworkID) {
        this.homeworkID = homeworkID;
    }

    @Override
    public String toString() {
        return studentID + "/" + homeworkID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeID gradeID = (GradeID) o;
        return Objects.equals(studentID, gradeID.studentID) &&
                Objects.equals(homeworkID, gradeID.homeworkID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, homeworkID);
    }
}
