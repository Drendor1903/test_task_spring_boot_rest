package ru.frolov.TestTaskSpringBoot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.service.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if(userService.findUserByPhoneNumber(user.getPhoneNumber()).isPresent())
            errors.rejectValue("phoneNumber", "", "Пользователем с таким логином уже существует.");
    }
}
