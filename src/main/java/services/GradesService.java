package services;

import domain.Grade;
import domain.GradeID;
import domain.Homework;
import domain.Student;
import exceptions.ValidationException;
import repositories.GradeRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class GradesService {
    private GradeRepository repo;



    public GradesService(GradeRepository repo) {
        this.repo = repo;

    }

    /**
     *
     * @param week -> the week in which the homework has been completed.
     * @param h-> homework
     * @return the maximum grade for a specific homework(integer).
     * @throws RuntimeException -> the id of homework doesn't exists.
     */
    public int getMaximumDifference(Homework h, boolean isMotivated, int delayTime,int currentWeek){
        int deadlineWeek = h.getDeadlineWeek();
        if(isMotivated)
            return 0;

        int addingWeek = currentWeek - delayTime;

        if(addingWeek < deadlineWeek)
            return 0;
        return addingWeek - deadlineWeek;

    }

    /**
     * Create the text for the file with the feedback.
     * @param homeworkID
     * @param gradeValue
     * @param currentWeek
     * @param deadlineWeek
     * @param feedback
     * @return data -> the text to put in the feedback text.
     */
    public String createFeedback(String homeworkID,int gradeValue, int currentWeek, int deadlineWeek,String[] feedback){
        String data = "";

        data += "Tema: "+homeworkID+"\n";
        data += "Nota: "+gradeValue+"\n";
        data += "Predata in saptamana: "+ currentWeek+"\n";
        data += "Deadline: "+deadlineWeek+"\n";
        data += "Feedback: \n";
        for(String s:feedback){
            if(s!=null)
                data += s + "\n";
        }
        data += "\n";
        return data;
    }

    /**
     *Create and append to the repo the grade.Also create the feedback note and append to an existent one(or creates a new one).
     * @param  s->student
     * @param h->homework
     * @param date-> the date the grade has been added.
     * @param teacher
     * @param value (between 1 and 10).
     * @return null if the grade has been added, otherwise the grade(if the grade already exists)
     * @throws ValidationException - if the grade is not valid
     * @throws IOException
     */
    public Grade addGrade(Student s, Homework h, LocalDate date, String teacher, int value) throws ValidationException {

        Grade g = new Grade(s.getId(),h.getId(),date,teacher,value);

        return repo.save(g);


    }

    /**
     *
     * @param studentID
     * @param homeworkID
     * @return the removed entity or null(the entity doesn't exists);
     */
    public Grade removeGrade(String studentID,String homeworkID){

        return repo.delete(new GradeID(studentID,homeworkID));

    }

    /**
     * Read the data for grades from a file
     * @throws IOException
     */


    public void readFromFile(String filePath) throws IOException {
        String file = filePath + "/note.csv";
        repo.readFromFile(file);
    }

    /**
     * Save the data from memory to a file
     * @throws IOException
     */
    public void writeToFile(String filePath) throws IOException {
        repo.writeToFile("filePath/note.csv");
    }

    /**
     *Create the file for every student(if this doesn't exists) and append the feedback for a specific grade.
     * @param s-Student(must be not null)
     * @param feedback - String(the feedback created on the function addGrade(...))
     * @throws IOException
     */
    public void createInfoData(Student s, String feedback) throws IOException {
        String file ="src/sample/infoData/"+ s.getFirstName()+"_"+s.getLastName()+".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));

        writer.write(feedback);
        writer.close();
    }


    public Iterable<Grade> getAllGrades(){
        return repo.findAll();
    }

    public Grade findGrade(String studentID,String homeworkID){
        return repo.findOne(new GradeID(studentID,homeworkID));
    }

    public String getStudentIDByGrade(GradeID id){
        return id.getStudentID();
    }

    public String getHomeworkIDByGrade(GradeID id){
        return id.getHomeworkID();
    }

    public void readFromXmlFile(String filePath){
        String file = filePath + "/note.xml";
        repo.readFromXmlFile(file,"nota");
    }

    public void writeToXmlFile(String filePath){
        String file = filePath + "/note.xml";
        repo.writeToXmlFile(file,"note","nota");
    }
}
