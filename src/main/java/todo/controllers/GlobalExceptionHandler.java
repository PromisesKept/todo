package todo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import todo.exception.TodoNotFoundException;
import todo.exception.UserAlreadyExistException;
import todo.exception.UserMustBeAuthenticated;
import todo.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TodoNotFoundException.class)
    public String handleTodoNotFoundException(TodoNotFoundException ex, Model model) {
        logger.info("Выбросилась TodoNotFoundException");
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException ex, Model model) {
        logger.info("Выбросилась UserAlreadyExistException");
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        logger.info("Выбросилась UserNotFoundException");
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

    @ExceptionHandler(UserMustBeAuthenticated.class)
    public String handleUserMustBeAuthenticated(UserMustBeAuthenticated ex, Model model) {
        logger.info("Выбросилась UserMustBeAuthenticated");
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }


    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        logger.info("Выбросилась IllegalStateException");
        model.addAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

}

