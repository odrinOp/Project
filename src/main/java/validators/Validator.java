package validators;

import exceptions.ValidationException;

public interface Validator<E> {
    void validate(E entity) throws ValidationException;
}
