package validators;

import domain.Student;
import exceptions.ValidationException;

public class StudentValidation implements Validator<Student> {
    @Override
    public void validate(Student entity) throws ValidationException {
        String errors = "";

        if(entity.getId() == null || entity.getId().equals(""))
            errors += "Invalid ID!\n";

        if(entity.getFirstName() == null ||  entity.getFirstName().equals("") )
            errors += "Invalid first name!\n";

        if(entity.getLastName() == null || entity.getLastName().equals("") )
            errors += "Invalid last name!\n";

        if(entity.getGroup() <= 100 || entity.getGroup() > 999)
            errors += "Invalid group number!\n";

        if(entity.getEmail() == null || entity.getEmail().equals(""))
            errors += "Invalid email!\n";

        if(entity.getGuidingTeacher() == null || entity.getGuidingTeacher().equals("") )
            errors += "Invalid guiding teacher's name!\n";

        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}