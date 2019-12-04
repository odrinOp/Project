package validators;

import domain.Grade;
import exceptions.ValidationException;

public class GradeValidation implements Validator<Grade> {
    @Override
    public void validate(Grade entity) throws ValidationException {
        String errors = "";

        if(entity.getId() == null)
            errors += "Invalid id!\n";
        else{
            if(entity.getId().getStudentID() == null || entity.getId().getStudentID().equals("") || entity.getId().getHomeworkID() == null || entity.getId().getHomeworkID().equals(""))
                errors += "Invalid id!\n";
        }

        if(entity.getDate() == null)
            errors += "Date is null!\n";

        if(entity.getTeacher() == null || entity.getTeacher().equals("") )
            errors += "Teacher's name invalid!\n";

        if(entity.getValue() < 1 || entity.getValue() > 10)
            errors += "The value of the grade is invalid!\n";

        if(!errors.equals(""))
            throw new ValidationException(errors);
    }


}
