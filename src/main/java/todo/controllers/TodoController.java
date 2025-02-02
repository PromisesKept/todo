package todo.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.enums.Progress;
import todo.entity.TodoEntity;
import todo.exception.TodoNotFoundException;
import todo.models.Todo;
import todo.service.TodoService;

import java.util.Optional;

@Controller
@RequestMapping("/todo")
public class TodoController {

    public static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
        logger.info("TodoController instantiated");
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.info("GET /todos invoked by index(Model model)");
        model.addAttribute("todos", todoService.getAllTodos());
        return "todo/index";
    }

    @PostMapping("/")
    public String create(@ModelAttribute("todo") @Valid Todo todo,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            logger.info("Ошибка валидации на форме создания задачи: {}", bindingResult.getAllErrors());
            return "todo/new";
        }

        Optional<Long> createTodoId = todoService.create(todo);
        if (createTodoId.isPresent()) {
            return "redirect:/user/" + createTodoId;
        } else {
            logger.warn("Не удалось создать задачу. Пользователь не найден или не аутентифицирован. КАК ТЫ ВООБЩЕ СЮДА ПОПАЛ, ЭУ?! Напиши мне, дам премию (пизды)");
        } return "redirect:/error";

    }

    @GetMapping("/new")
    public String newTodo(@ModelAttribute("todo") Todo todo) {
        TodoEntity todoEntity = Todo.toEntity(todo);
        todoService.setProgressTODO(todoEntity);
        return "todo/new";
    }

    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable("id") Long id) {
        logger.info("Запрос в TodoController на удаление задачи с id: {}", id);
        return "redirect:/user" + todoService.delete(id);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        todoService.show(id, model);
        return "todo/show";
    }

    @PostMapping("/{id}")
    public String update(@ModelAttribute("todo") @Valid Todo todo,
                         BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "todo/edit";
        }
        return todoService.update(id, todo);
    }

    @GetMapping("/{id}/edit")
    public String editTodo(@PathVariable("id") Long id, Model model) throws TodoNotFoundException {
        logger.info("Запускаю @GetMapping в TodoController с /{id}/edit");
        TodoEntity todo = todoService.findById(id);
        logger.info("Ошибки не обнаружено");
        logger.info(todo.getDeadline().toString());
        model.addAttribute("todo", todo);
        model.addAttribute("progress", Progress.values());
        model.addAttribute("deadline", todo.getDeadline());
        return "todo/edit";
    }


}
