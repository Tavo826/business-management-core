package co.com.manager.api.advice;

import co.com.manager.model.exceptions.BusinessNotFoundException;
import co.com.manager.model.exceptions.InvalidPasswordException;
import co.com.manager.model.exceptions.InvalidTokenException;
import co.com.manager.model.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("User Not Found");
        return problem;
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ProblemDetail handleBusinessNotFoundException(BusinessNotFoundException ex) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Business Not Found");
        return problem;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidTokenException(InvalidTokenException ex) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Invalid Token");
        return problem;
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ProblemDetail handleInvalidPasswordException(InvalidPasswordException ex) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Invalid Password");
        return problem;
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ProblemDetail handleServerWebInputException(ServerWebInputException ex) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getReason());
        problem.setTitle("Request Error");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception e) {

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problem.setTitle("Internal Server Error");
        return problem;
    }
}
