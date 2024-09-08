package ru.frolov.TestTaskSpringBoot.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.frolov.TestTaskSpringBoot.dto.PaymentDTO;
import ru.frolov.TestTaskSpringBoot.dto.PaymentsResponse;
import ru.frolov.TestTaskSpringBoot.dto.UserDTO;
import ru.frolov.TestTaskSpringBoot.model.Payment;
import ru.frolov.TestTaskSpringBoot.model.User;
import ru.frolov.TestTaskSpringBoot.security.UsersDetails;
import ru.frolov.TestTaskSpringBoot.service.UserService;
import ru.frolov.TestTaskSpringBoot.util.ErrorResponse;
import ru.frolov.TestTaskSpringBoot.util.NotRegisteredException;
import ru.frolov.TestTaskSpringBoot.util.UserValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, ModelMapper modelMapper) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody UserDTO registerUser,
                                         BindingResult bindingResult){
        User user = convertToUser(registerUser);
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error: errors ) {
                errorMessage.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }

            throw new NotRegisteredException(errorMessage.toString());
        }

        userService.register(user);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get_balance")
    public ResponseEntity<Map<String, Object>> getBalance(@AuthenticationPrincipal UsersDetails userDetails) {

        Optional<User> userOptional = userService.findUserByPhoneNumber(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = userOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("balance", user.getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@AuthenticationPrincipal UsersDetails userDetails,
                                          @RequestBody PaymentDTO paymentDTO){
        Optional<User> userOptional = userService.findUserByPhoneNumber(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = userOptional.get();

        return ResponseEntity.ok(userService.makePayment(user, paymentDTO));
    }

    @GetMapping("/edit")
    public ResponseEntity<Map<String, Object>> edit(@AuthenticationPrincipal UsersDetails userDetails){

        Optional<User> userOptional = userService.findUserByPhoneNumber(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = userOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("gender", user.getGender());
        response.put("birthDate", user.getBirthDate());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<HttpStatus> updateUser(@AuthenticationPrincipal UsersDetails usersDetails,
                                                 @RequestBody UserDTO userDTO){

        userService.updateUser(userDTO, usersDetails);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get_payments")
    public PaymentsResponse getPayments(@AuthenticationPrincipal UsersDetails usersDetails,
                                        @RequestParam(value = "page") Integer page,
                                        @RequestParam(value = "item_per_page") Integer itemPerPage){
        Optional<User> userOptional = userService.findUserByPhoneNumber(usersDetails.getUsername());

        if (userOptional.isEmpty()) {
            return new PaymentsResponse(null);
        }
        User user = userOptional.get();

        return new PaymentsResponse(userService.getPayments(user, page, itemPerPage).stream().map(this::convertToPaymentDTO).collect(Collectors.toList()));
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotRegisteredException e){
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new  ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
    private PaymentDTO convertToPaymentDTO(Payment payment){
        return modelMapper.map(payment, PaymentDTO.class);
    }
}
