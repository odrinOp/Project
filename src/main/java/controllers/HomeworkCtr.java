package controllers;

import domain.Entities;
import domain.Homework;
import domain.MessageAlert;
import exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import observer.Observer;
import services.ServiceMaster;

import java.io.IOException;

public class HomeworkCtr extends AbstractController implements Observer {

    //to get the master, type getMaster()

    @FXML private TableView<Homework> table;
    @FXML private TextField idText;
    @FXML private  TextField endWeekText;
    @FXML private  TextField descriptionText;


    public HomeworkCtr(){}

    @Override
    public void update() throws IOException {
        getMaster().saveData(Entities.HOMEWORK);
        initTable();
    }


    public void initialize(ServiceMaster master){
        super.initialize(master);
        getMaster().addObs(this);

        initTable();
        setTextFieldsNull();

    }

    @FXML public void getDataFromTable(){
        Homework h = table.getSelectionModel().getSelectedItem();
        if(h==null)
            setTextFieldsNull();
        else
        {
            idText.setText(h.getId());
            endWeekText.setText(String.valueOf(h.getDeadlineWeek()));
            descriptionText.setText(h.getDescription());
        }

    }

    @FXML public void addHomework(){
        String id = idText.getText();
        int deadlineWeek = Integer.parseInt(endWeekText.getText());
        String description = descriptionText.getText();


        try {
            Homework h =getMaster().addHomework(id,description,deadlineWeek);
            if(h == null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Done","The homework has been saved");
            else
                MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","The homework already exists!Check id");
        } catch (ValidationException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"ValidationException",e.getLocalizedMessage());
        } catch (IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"IOException",e.getLocalizedMessage());
        }


    }

    @FXML public void deleteHomework(){
        Homework h = table.getSelectionModel().getSelectedItem();
        if(h == null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","No homework has been selected!");
            return;
        }

        try {
            getMaster().removeHomework(h.getId());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Dome","The homework has been deleted");
        } catch (IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"IOException",e.getLocalizedMessage());
        }


    }

    @FXML public void updateHomework(){

        Homework h = table.getSelectionModel().getSelectedItem();
        if(h == null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","No homework has been selected!");
            return;
        }

        int deadlineWeek = Integer.parseInt(endWeekText.getText());
        String description = descriptionText.getText();


        try {
            Homework res = getMaster().updateHomework(h.getId(),description,deadlineWeek);
            if(res == null){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Done","The homework has been updated");

            }
            else

                MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","The homework doesn't exists!Check id");
        } catch (ValidationException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"ValidationException",e.getLocalizedMessage());
        } catch (IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"IOException",e.getLocalizedMessage());
        }


    }

    @FXML public void cancel(){
        setTextFieldsNull();
    }



    private void initTable(){
        table.getItems().clear();
        getMaster().getAllHomework().forEach(h->table.getItems().add(h));

    }

    private void setTextFieldsNull(){
        idText.setText("");
        endWeekText.setText("");
        descriptionText.setText("");
    }



}
