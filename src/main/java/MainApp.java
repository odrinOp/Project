import config.ApplicationContext;
import controllers.GradeCtr;
import controllers.MenuCtr;
import controllers.StudentCtr;
import domain.Entities;
import domain.YearStructure;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import repositories.GradeRepository;
import repositories.HomeworkRepository;
import repositories.StudentRepository;
import services.GradesService;
import services.HomeworkService;
import services.ServiceMaster;
import services.StudentsService;
import validators.GradeValidation;
import validators.HomeworkValidation;
import validators.StudentValidation;

import java.io.IOException;

public class MainApp extends Application {

    private StudentValidation studentValidation;
    private StudentRepository studentRepository;
    private StudentsService studentsService;

    private HomeworkValidation homeworkValidation;
    private HomeworkRepository homeworkRepository;
    private  HomeworkService homeworkService;

    private GradeValidation gradeValidation;
    private  GradeRepository gradeRepository;
    private  GradesService gradesService;

    private YearStructure structure;

    private ServiceMaster master;

    @Override
    public void start(Stage stage) throws Exception {
        studentValidation = new StudentValidation();
        studentRepository = new StudentRepository(studentValidation);
        studentsService = new StudentsService(studentRepository);


        homeworkValidation = new HomeworkValidation();
        homeworkRepository = new HomeworkRepository(homeworkValidation);
        homeworkService = new HomeworkService(homeworkRepository);


        gradeValidation = new GradeValidation();
        gradeRepository =new GradeRepository(gradeValidation);
        gradesService = new GradesService(gradeRepository);

        structure = new YearStructure("2019");

        String fileN = ApplicationContext.getPROPERTIES().getProperty("data.entities.MainFolder");
        master = new ServiceMaster(gradesService,studentsService,homeworkService,structure,fileN);


        init(stage);
        stage.show();
    }


    public void init(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/menuView.fxml"));
        BorderPane primaryRoot = loader.load();
        primaryStage.setScene(new Scene(primaryRoot));


        MenuCtr controller = loader.getController();
        controller.initialize(master);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
