package controllers;

import domain.Entities;
import domain.MessageAlert;
import domain.Student;
import exceptions.ValidationException;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import observer.Observer;
import services.ServiceMaster;

import services.StudentsService;

import java.io.IOException;

public class StudentCtr extends AbstractController implements Observer {

    @FXML
    TableView<Student> table;
    @FXML
    TextField textID;
    @FXML TextField textFirstName;
    @FXML TextField textLastName;
    @FXML TextField textGroup;
    @FXML TextField textEmail;
    @FXML TextField textTeacher;



    public StudentCtr(){}



@Override
    public void initialize(ServiceMaster master){
        super.initialize(master);
        getMaster().addObs(this);

        initTable();
        setTextFieldsNull();

    }

    private void setTextFieldsNull(){
        textID.setText("");
        textTeacher.setText("");
        textGroup.setText("");
        textEmail.setText("");
        textLastName.setText("");
        textFirstName.setText("");

    }

    public void initTable(){
        table.getItems().clear();
        getMaster().getAllStudents().forEach(x->table.getItems().add(x));
    }

    @FXML
    public void getDataFromTable(){
        Student s = table.getSelectionModel().getSelectedItem();
        if(s == null){
            setTextFieldsNull();
            return;
        }
        textID.setText(s.getId());
        textFirstName.setText(s.getFirstName());
        textLastName.setText(s.getLastName());
        textEmail.setText(s.getEmail());
        textGroup.setText(String.valueOf(s.getGroup()));
        textTeacher.setText(s.getGuidingTeacher());
    }

    @FXML
    public void addStudent(){
        String id = textID.getText();
        String firstName = textFirstName.getText();
        String lastName = textLastName.getText();
        String email = textEmail.getText();
        int group = 0;
        try {
            group = Integer.parseInt(textGroup.getText());
        }catch(NumberFormatException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"NumberFormatException","The group must be an integer!");
            return;
        }
        String teacher = textTeacher.getText();

        try {
            Student s = getMaster().addStudent(id,firstName,lastName,group,email,teacher);
            if(s==null){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Saved","The student has been saved");
            }
            else
            {
                MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Warning","The student with specific id or data already exists");
            }

        } catch (ValidationException | IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Validation Error",e.getLocalizedMessage());
        }


    }
    @FXML
    public void deleteStudent() throws IOException {
        String id = textID.getText();

        Student s = getMaster().removeStudent(id);
        if(s == null)
            MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Warning","The id doesn't exists!");
        else
        {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Done","The student has been deleted");
            setTextFieldsNull();
        }
    }
    @FXML
    public void updateStudent(){
        String id = textID.getText();
        String firstName = textFirstName.getText();
        String lastName = textLastName.getText();
        String email = textEmail.getText();
        String teacher = textTeacher.getText();
        int group = 0;
        try{
            group = Integer.parseInt(textGroup.getText());
        }catch (NumberFormatException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"NumberFormatException","The group must be a number");
            return;
        }

        try {
            Student s = getMaster().updateStudent(id,firstName,lastName,group,email,teacher);
            if(s==null) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "The student has been updated");
            }
            else
                MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Failed","The student hasn't been updated");
        } catch (ValidationException | IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Validation Error", e.getLocalizedMessage());
        }

    }

    @FXML
    public void onCancel(){
        setTextFieldsNull();
    }


    @Override
    public void update() {
        try {
            getMaster().saveData(Entities.STUDENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initTable();

    }
}
