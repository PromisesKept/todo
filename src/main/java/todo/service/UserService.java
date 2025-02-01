package todo.service;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import todo.enums.Progress;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;
import todo.enums.Sort;
import todo.exception.UserNotFoundException;
import todo.models.User;
import todo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService implements UserDetailsService {

    public static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TodoService todoService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, TodoService todoService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.todoService = todoService;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void addUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Начинает работу метод loadUserByUsername");
        Optional<UserEntity> user = userRepository.findByUsername(username);
        logger.info("В Optional<UserEntity> был добавлен: " + user);
        if (user.isEmpty()) throw new UsernameNotFoundException("Пользователь не найден");
        logger.info("в return это выглядит так: " + user.get());
        return user.get();
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity findById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public String show(Long id, Sort sort, String filter, Model model) {
        Optional<UserEntity> userOptional = userRepository.findById(id);

        // установка сортировки по-умолчанию
        if (sort == null) {
            sort = Sort.START;
        }

        if (userOptional.isPresent()) {
            User user = User.toModel(userOptional.get());
            // это нужно для получения списка задач до закрытия сессии Hibernate
            Hibernate.initialize(user.getTodos());
            model.addAttribute("user", user);

            // сортировка
            List<TodoEntity> todos = null;
            switch (sort) {
                case Sort.START -> todos = todoService.findAllByStart(id);
                case Sort.PROGRESS -> todos = todoService.findAllByProgress(id);
                case Sort.DEADLINE -> todos = todoService.findAllByDeadline(id);
                default -> logger.warn("switch (sort) обратился в default");
            }


            // фильтрация
            if (filter != null && !filter.isEmpty()) {
                todos = todos.stream()
                        .filter(todo -> todo.getProgress().name().equals(filter))
                        .collect(Collectors.toList());
            }

            model.addAttribute("todos", todos);
            model.addAttribute("sort", sort);
            model.addAttribute("userId", id);
            model.addAttribute("filter", filter);
            model.addAttribute("statuses", Progress.values());

            return "user/show";
        } else {
            return "redirect:/error";
        }
    }

    public String update(UserEntity user, Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            UserEntity existingUser = userOptional.get();
            logger.info("Имя в БД: " + existingUser.getName());
            if (user.getName() != null || !user.getName().trim().isEmpty()) existingUser.setName(user.getName().trim());
            if (user.getPassword() != null || !user.getPassword().trim().isEmpty()) existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            else user.setPassword(existingUser.getPassword());

            logger.info("Роль до установки:" + existingUser.getRole());
            existingUser.setRole(user.getRole());
            logger.info("Роль после установки:" + existingUser.getRole());

            logger.info("Теперь имя = " + existingUser.getName());
            userRepository.save(existingUser);
            logger.info("Отправлено в БД");
            return "redirect:/user/{id}";
        } else return "redirect:/error";
    }

}
