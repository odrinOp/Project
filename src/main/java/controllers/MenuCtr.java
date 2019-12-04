package controllers;

import domain.Entities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import services.ServiceMaster;

import javax.print.attribute.standard.Severity;
import java.io.IOException;

public class MenuCtr extends AbstractController{

public void MenuCtr(){}


    @Override
    public void initialize(ServiceMaster master){
            super.initialize(master);
        try {
            getMaster().loadData(Entities.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void studentsBtn(){

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/studentView.fxml"));


            try {
                BorderPane root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Students Menu");
                Scene scene = new Scene(root);

                stage.setScene(scene);

                StudentCtr controller = loader.getController();
                controller.initialize(getMaster());
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    @FXML
    private void homeworkBtn(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/homeworkView.fxml"));


        try {
            BorderPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Homework Menu");
            Scene scene = new Scene(root);

            stage.setScene(scene);

            HomeworkCtr controller = loader.getController();
            controller.initialize(getMaster());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML void gradesBtn(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/gradeView.fxml"));


        try {
            BorderPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Grade Menu");
            Scene scene = new Scene(root);

            stage.setScene(scene);

            GradeCtr controller = loader.getController();
            controller.initialize(getMaster());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
