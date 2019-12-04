package controllers;

import domain.Entities;
import domain.Homework;
import domain.MessageAlert;
import domain.Student;
import exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import observer.Observer;
import org.w3c.dom.Text;
import services.GradesService;
import services.ServiceMaster;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;

public class GradeCtr extends AbstractController implements Observer {



    @FXML
    ComboBox<Homework> homeworkComboBox;
    @FXML
    ComboBox<Student> studentComboBox;
    @FXML
    RadioButton isMotivatedBtn;
    @FXML
    RadioButton isDelayedBtn;
    @FXML
    TextField delayTxt;

    @FXML TextArea feedbackArea;

    @FXML TextField teacherTxt;
    @FXML
    MenuButton presetFeedbackMenu;

    @FXML TextField valueTxt;

    private final String presetFeedback1 = "- nu exista observatii!";
    private final String presetFeedback2 = "- necesita modificari minore la:  ";
    private final String presetFeedback3 = "- necesita modificari majore la:  ";
    private final String presetFeedback4 = "- tema nu resepcta cerinta  ";


    public GradeCtr(){

    }




    @Override
    public void update()  {

        try {
            getMaster().saveData(Entities.GRADE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initHomeworkList();
        initStudentsList();
    }


    private void initHomeworkList(){
        homeworkComboBox.getItems().clear();

        getMaster().getAllHomework().forEach(x->homeworkComboBox.getItems().add(x));
        homeworkComboBox.getSelectionModel().select(getMaster().getCurrentHomework());
    }

    private void initFeedbackPresets(){
        presetFeedbackMenu.getItems().clear();
        presetFeedbackMenu.getItems().add(new MenuItem(presetFeedback1));
        presetFeedbackMenu.getItems().add(new MenuItem(presetFeedback2));
        presetFeedbackMenu.getItems().add(new MenuItem(presetFeedback3));
        presetFeedbackMenu.getItems().add(new MenuItem(presetFeedback4));

        for(MenuItem it :presetFeedbackMenu.getItems()){
            it.setOnAction(a-> feedbackArea.setText(feedbackArea.getText() + "\n" + it.getText()));
        }
    }

    private void initButtonsAndTextFields(){
        //delay txt and delay btn
        delayTxt.setText("0");
        delayTxt.setDisable(true);
    }

    public void initialize(ServiceMaster master) {
        super.initialize(master);
        getMaster().addObs(this);
        initHomeworkList();
        initStudentsList();
        initFeedbackPresets();
        initButtonsAndTextFields();
    }

    @FXML
    public void initStudentsList(){
        //System.out.println("Hello");
        studentComboBox.getItems().clear();
        Homework h = homeworkComboBox.getValue();
        getMaster().allStudentsWithoutHomework(h.getId()).forEach(s->studentComboBox.getItems().add(s));
        studentComboBox.getSelectionModel().select(0);

        addFeedbackDelay();
    }

    @FXML
    public void setStatusDelayTxt(){

        if(isDelayedBtn.isSelected() == true){
            delayTxt.setDisable(false);
            delayTxt.setText("0");
        }

        else{
            delayTxt.setText("0");
            delayTxt.setDisable(true);
        }
    }

@FXML
    public void addGrade() {

       try{
           Student s = studentComboBox.getValue();
           Homework h = homeworkComboBox.getValue();

           int delayWeek = 0;
           int value = 10;

           try{
               delayWeek = Integer.parseInt(delayTxt.getText());
           }catch (NumberFormatException e){
               MessageAlert.showMessage(null, Alert.AlertType.ERROR,"NumberFormatException", "Delay week must be an integer");
               return;
           }

           try{
               value = Integer.parseInt(valueTxt.getText());
               if(value < 1 || value > 10) {
                   MessageAlert.showMessage(null, Alert.AlertType.ERROR, "ValidationException", "Value must be an integer between 1 and 10");
                    return;
               }
           }catch (NumberFormatException e){
               MessageAlert.showMessage(null, Alert.AlertType.ERROR,"NumberFormatException", "Value must be an integer");
               return;
           }

           /*
           int dif = master.getMaximumDifference(h,isMotivatedBtn.isSelected(),delayWeek);
           if(dif > 2)
           {
               MessageAlert.showMessage(null, Alert.AlertType.ERROR,"ValidationException","The deadline week has been exceded for to long!");
                return;
           }

            */

            String teacher = teacherTxt.getText();
            String feedback = feedbackArea.getText();

            Alert confirmWindow = MessageAlert.getConfirmationAlert(null,"Add grade", createInfoData(h,s,isMotivatedBtn.isSelected(),delayWeek,teacher,value,feedback));
            confirmWindow.showAndWait();

            if(confirmWindow.getResult() == ButtonType.OK){
                System.out.println("Good");
                if(getMaster().addGrade(s.getId(),h.getId(),teacher,value,feedback,isMotivatedBtn.isSelected(),delayWeek) != null){
                    MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error" ,"The student already have a grade at this homework!");
                }
                else
                {
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Done","The grade has been added");
                }
           }



       }catch (ValidationException | IOException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Validation Exception" , e.getMessage());
            return;
       }
    }

    @FXML
    public void clearFeedbackArea(){
        feedbackArea.setText("");
    }



    @FXML
    public void addFeedbackDelay(){


        int delayWeek = 0;
        if(isDelayedBtn.isSelected() == true)
            try {
                delayWeek = Integer.parseInt(delayTxt.getText());
            }
        catch (Exception e){
                delayWeek = 0;
        }

        int dif = getMaster().getMaximumDifference(homeworkComboBox.getValue(),isMotivatedBtn.isSelected(),delayWeek);
        if(dif == 0) {
            clearFeedbackArea();
            return;
        }
        String feedback = "-nota a fost micsorata cu " + dif + " puncte datorita intarzierii!";
        clearFeedbackArea();
        feedbackArea.setText(feedbackArea.getText() + "\n" + feedback);




    }


    private String createInfoData(Homework homework,Student student, boolean isMotivated, int delayWeek, String teacher, int value, String feedback){
        String infoData ="";

        infoData += "Please verify all the information and click confirm if the data is correct!\n\n";

        infoData += "Homework: " + homework.getDescription() + "\n";
        infoData += "Student: " + student.toString() + "\n";
        if(isMotivated)
            infoData += "Is motivated: yes" + "\n";
        else
            if(delayWeek > 0) {
            infoData += "Is delayed: yes" + "\n";
            infoData += "Delay Time(in weeks):  " + delayWeek + "\n";
            }
        infoData += "Absolute value(without penalisation): " + value + "\n";
        infoData += "Penalisation: " + String.valueOf(getMaster().getMaximumDifference(homework, isMotivated, delayWeek)) + "\n";
        infoData += "Value(with penalisation): " + String.valueOf(value - getMaster().getMaximumDifference(homework, isMotivated, delayWeek)) + "\n";

        infoData += "Teacher: " + teacher +"\n";
        infoData += "Feedback: " + feedback + "\n";

        return infoData;

    }

}
