package todo.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import todo.Progress;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;
import todo.exception.TodoNotFoundException;
import todo.exception.UserMustBeAuthenticated;
import todo.exception.UserNotFoundException;
import todo.repository.TodoRepository;
import todo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class TodoService {

    public static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    private UserEntity getCurrentUser() throws UserMustBeAuthenticated, UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UserMustBeAuthenticated("User must be authenticated");
        }
        Optional<UserEntity> currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser.isEmpty()) {
            throw new UserNotFoundException("User NFE! Ты кто ваще?! Как ты сюда попал?!");
        } else return currentUser.get();
    }

    public Long delete(Long id) {
        try {
            UserEntity currentUser = getCurrentUser(); // проверка пользователя на аутентификацию
            logger.info("Запрос из TodoController в TodoService на удаление задачи с id: {}", id);
            TodoEntity todo = todoRepository.findById(id).orElse(null);
            if (todo != null) {
                todo.setUser(null);
                todoRepository.delete(todo);
                logger.info("Задача с id {} была успешно удалена.", id);
            } else {
                logger.info("Задача с id {} не найдена для удаления.", id);
            }
            return currentUser.getId();
        } catch (UserMustBeAuthenticated | UserNotFoundException e) {
            logger.error("Ошибка при удалении задачи: {}", e.getMessage());
            return null;
        }

    }

    public Optional<Long> create(TodoEntity todo) {
        try {
            UserEntity currentUser = getCurrentUser();
            todo.setUser(currentUser);
            logger.info("Попытка создания задачи в TodoService: {}", todo);
            todoRepository.save(todo);
            logger.info("Задача {} создана успешно!", todo);
            return Optional.of(currentUser.getId());
        } catch (UserMustBeAuthenticated | UserNotFoundException e) {
            logger.error("Ошибка создания задачи: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void show(Long id, Model model) {
        todoRepository.findById(id).ifPresentOrElse(
                todo -> model.addAttribute("todo", todo),
                () -> model.addAttribute("error", "Todo не найдена")
        );
    }



    public String update(Long id, TodoEntity todo) {
        Optional<TodoEntity> todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
            TodoEntity existingTodo = todoOptional.get();

            existingTodo.setTitle(todo.getTitle());
            existingTodo.setDescription(todo.getDescription());
            existingTodo.setDeadline(todo.getDeadline());
            existingTodo.setProgress(todo.getProgress());

            todoRepository.save(existingTodo);
            return "redirect:/todo/{id}";
        } else return "redirect:/error";
    }

    public void setProgressTODO(TodoEntity todo) {
        todo.setProgress(Progress.TODO);
    }

    public TodoEntity findById(Long id) throws TodoNotFoundException {
        return todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException("Todo not found"));
    }

    public List<TodoEntity> getAllTodos() {
        return (List<TodoEntity>) todoRepository.findAll();
    }

    public List<TodoEntity> findAllByStart(Long userId) {
        // Получение задач текущего пользователя, отсортированных по дате создания
        return todoRepository.findByUserIdOrderByStartAsc(userId);
    }

    public List<TodoEntity> findAllByDeadline(Long userId) {
        // Получение задач текущего пользователя, отсортированных по дедлайну
        return todoRepository.findByUserIdOrderByDeadlineAsc(userId);
    }

    public List<TodoEntity> findAllByProgress(Long userId) {
        // Получение задач текущего пользователя, отсортированных по статусу
        return todoRepository.findByUserIdOrderByProgressAsc(userId);
    }


}
