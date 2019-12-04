package repositories;

import domain.Homework;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import validators.Validator;

public class HomeworkRepository extends AbstractRepository<String, Homework> {
    public HomeworkRepository(Validator validator) {
        super(validator);
    }

    @Override
    public String entityToString(Homework homework) {
        return homework.toString();
    }

    @Override
    public Homework stringToEntity(String[] data) {
        if(data.length != 4)
            return null;
        return new Homework(data[0],data[1],Integer.parseInt(data[2]),Integer.parseInt(data[3]));
    }

    @Override
    public Homework elementToEntity(Element e) {
        return new Homework(e.getAttribute("id"),
                e.getElementsByTagName("description").item(0).getTextContent(),
                Integer.parseInt(e.getElementsByTagName("startWeek").item(0).getTextContent()),
                Integer.parseInt(e.getElementsByTagName("deadlineWeek").item(0).getTextContent()));
    }

    @Override
    public Element[] entityToListOfElements(Homework homework, Document doc) {
        Element[] args = new Element[3];

        args[0] = doc.createElement("description");
        args[0].appendChild(doc.createTextNode(homework.getDescription()));

        args[1] = doc.createElement("startWeek");
        args[1].appendChild(doc.createTextNode(String.valueOf(homework.getStartWeek())));

        args[2] = doc.createElement("deadlineWeek");
        args[2].appendChild(doc.createTextNode(String.valueOf(homework.getDeadlineWeek())));

        return args;
    }

}
