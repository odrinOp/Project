package repositories;

import domain.Grade;
import domain.GradeID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import validators.Validator;

import java.time.LocalDate;

public class GradeRepository extends AbstractRepository<GradeID, Grade> {


    public GradeRepository(Validator validator) {
        super(validator);
    }

    @Override
    public String entityToString(Grade grade) {
        return grade.toString();
    }

    @Override
    public Grade stringToEntity(String[] data) {
        if(data.length != 5)
            return null;

        String date[] = data[2].split("-");
        if(date.length != 3)
            return null;
        LocalDate currentDate = LocalDate.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
        return new Grade(data[0],data[1],currentDate,data[3],Integer.parseInt(data[4]));
    }

    @Override
    public Grade elementToEntity(Element e) {

        String id = e.getAttribute("id");
        String ids[] = id.split("/");

        String date = e.getElementsByTagName("date").item(0).getTextContent();
        String teacher = e.getElementsByTagName("teacher").item(0).getTextContent();
        String value = e.getElementsByTagName("value").item(0).getTextContent();

        String[] args= {ids[0],ids[1],date,teacher,value};
        return stringToEntity(args);
    }

    @Override
    public Element[] entityToListOfElements(Grade grade, Document document) {
        Element[] args = new Element[3];

        args[0] = document.createElement("date");
        args[0].appendChild(document.createTextNode(grade.getDate().toString()));

        args[1] = document.createElement("teacher");
        args[1].appendChild(document.createTextNode(grade.getTeacher()));

        args[2] = document.createElement("value");
        args[2].appendChild(document.createTextNode(String.valueOf(grade.getValue())));

        return args;
    }


}