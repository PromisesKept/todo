package todo.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.Progress;
import todo.entity.TodoEntity;
import todo.exception.TodoNotFoundException;
import todo.service.TodoService;

@Transactional
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

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping()
    public String index(Model model) {
        logger.info("GET /todos invoked by index(Model model)");
        model.addAttribute("todos", todoService.getAllTodos());
        return "todo/index";
    }

    @PostMapping()
    public String create(@ModelAttribute("todo") @Valid TodoEntity todo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("Validation errors: " + bindingResult.getAllErrors());
            return "todo/new";
        }
        return "redirect:/user/" + todoService.create(todo);
    }

    @GetMapping("/new")
    public String newTodo(@ModelAttribute("todo") TodoEntity todo) {
        todo.setProgress(Progress.TODO);
        return "todo/new";
    }

    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable("id") Long id) {
        logger.info("Запрос в TodoController на удаление задачи с id: " + id);
        return "redirect:/user" + todoService.delete(id);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        todoService.show(id, model);
        return "todo/show";
    }

    @PostMapping("/{id}")
    public String update(@ModelAttribute("todo") @Valid TodoEntity todo, BindingResult bindingResult, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) return "todo/edit";
        return todoService.update(id, todo);
    }

    @GetMapping("/{id}/edit")
    public String editTodo(@PathVariable("id") Long id, Model model) throws TodoNotFoundException {
        logger.info("Запускаю @GetMapping в TodoController с /{id}/edit");
        TodoEntity todo = todoService.findById(id);
        logger.info("Ошибки не обнаружено");
        System.out.println(todo.getDeadline());
        model.addAttribute("todo", todo);
        model.addAttribute("progress", Progress.values());
        model.addAttribute("deadline", todo.getDeadline());
        return "todo/edit";
    }


}
