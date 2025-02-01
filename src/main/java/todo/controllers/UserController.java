package todo.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todo.Role;
import todo.entity.UserEntity;
import todo.exception.UserNotFoundException;
import todo.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        logger.info("UserController instantiated");
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.info("GET /users invoked by index(Model model)");
        // Получаем людей из БД и передаем их в представление через модель
        model.addAttribute("users", userService.findAll());
        return "user/index";
    }

    @PostMapping("/")
    public String create(@ModelAttribute("user") @Valid UserEntity user, BindingResult bindingResult) {
        logger.info("create start");
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "Такой логин уже существует.");
            return "user/new";
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "user/new";
        }
        logger.info(bindingResult.getAllErrors().toString());
        if (user.getRole() == null) {
            user.setRole(Role.USER);
            logger.info("Роль установлена внутри метода create");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("В итоге отправляется на сохранение в репозиторий: \n" + user);
        userService.save(user);
        logger.info(user + "был успешно отправлен на сохранение");
        return "redirect:/user/ok";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") UserEntity user) {
        logger.info("newUser start");
        user.setRole(Role.USER);
        logger.info("{} - установленная роль", user.getRole());
        return "user/new";
    }

    @GetMapping("/ok")
    public String okPage() {
        return "ok";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/user";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id,
                       @RequestParam(value = "sort", required = false, defaultValue = "deadline") String sort,
                       @RequestParam(value = "filter", required = false) String filter, Model model) {
        return userService.show(id, sort, filter, model);
    }

    @PostMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid UserEntity user, BindingResult bindingResult, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println("Ошибка в поле: " + error.getField() + ", Сообщение: " + error.getDefaultMessage());
            });
            return "user/edit";
        }

        logger.info("Имя из формы: " + user.getName());
        return userService.update(user, id);
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") Long id, Model model) throws UserNotFoundException {
        logger.info("Запускаю @GetMapping с /user/{id}/edit");
        UserEntity user = userService.findById(id);
        logger.info("Ошибки не обнаружено");
        logger.info("Текущая роль: {}", user.getRole());
        logger.info("Текущее имя пользователя: {}", user.getName());
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user/edit";
    }

    @PostMapping("/registered")
    public String addUser(@RequestBody UserEntity userEntity) {
        userService.addUser(userEntity);
        return userEntity.getUsername() + "был создан";
    }



}
