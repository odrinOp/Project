package repositories;

import domain.Student;
import org.w3c.dom.Element;
import validators.Validator;

public class StudentRepository extends AbstractRepository<String, Student> {
    public StudentRepository(Validator validator) {
        super(validator);
    }

    @Override
    public String entityToString(Student student) {
        return student.toString();
    }

    @Override
    public Student stringToEntity(String[] data) {
        if(data.length == 6){
            return new Student(data[0],data[1],data[2],Integer.parseInt(data[3]),data[4],data[5]);
        }
        return null;
    }

    @Override
    public Student elementToEntity(Element e) {
        return new Student(e.getAttribute("id"),
                e.getElementsByTagName("firstName").item(0).getTextContent(),
                e.getElementsByTagName("lastName").item(0).getTextContent(),
                Integer.parseInt(e.getElementsByTagName("group").item(0).getTextContent()),
                e.getElementsByTagName("email").item(0).getTextContent(),
                e.getElementsByTagName("guidingTeacher").item(0).getTextContent());
    }

    @Override
    public Element[] entityToListOfElements(Student student, org.w3c.dom.Document document) {
        Element[] args = new Element[5];
        args[0] = document.createElement("firstName");
        args[0].appendChild(document.createTextNode(student.getFirstName()));

        args[1] = document.createElement("lastName");
        args[1].appendChild(document.createTextNode(student.getLastName()));

        args[2] = document.createElement("group");
        args[2].appendChild(document.createTextNode(String.valueOf(student.getGroup())));

        args[3] = document.createElement("email");
        args[3].appendChild(document.createTextNode(student.getEmail()));

        args[4] = document.createElement("guidingTeacher");
        args[4].appendChild(document.createTextNode(student.getGuidingTeacher()));

        return args;

    }


}
