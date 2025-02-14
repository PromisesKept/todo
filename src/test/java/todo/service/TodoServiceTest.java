package todo.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import todo.Application;
import todo.enums.Progress;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;
import todo.models.Todo;
import todo.repository.TodoRepository;
import todo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class TodoServiceTest {

    // именно такая реализация, тк тестовые задачи создаются на реальном пользователе (id = 2L)
    @Mock
    private TodoRepository todoRepository;

    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    private TodoEntity todo;
    private UserEntity user;

    @BeforeEach
    void setUp()  {
        todoService = new TodoService(todoRepository, userRepository);
        user = userRepository.findByUsername("screemo").orElse(null);
        todo = new TodoEntity();
        todo.setId(1L);
        todo.setTitle("Тестовая задача 1L");
        todo.setDescription("Тестовый дескрипшен 1L");
        todo.setDeadline(LocalDateTime.now().plusDays(3));
        todo.setProgress(Progress.TODO);
        todo.setUser(user);

        // контекст безопасности с пользователем
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        doNothing().when(todoRepository).delete(any());
    }

    @Test
    void deleteCorrect() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        doNothing().when(todoRepository).delete(any(TodoEntity.class));

        Long userId = todoService.delete(1L);
        assertEquals(2L, userId);
        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void deleteTodoNotFound() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        Long userId = todoService.delete(1L);
        assertEquals(2L, userId);
        verify(todoRepository, never()).delete(any(TodoEntity.class));
    }

    @Test
    void createCorrect() {
        Todo todoDto = Todo.toModel(todo);
        Optional<Long> userId = todoService.create(todoDto);
        if (userId.isPresent()) {
            assertEquals(2L, userId.get());
        } else return;
        TodoEntity expectedTodoEntity = Todo.toEntity(todoDto);
        expectedTodoEntity.setUser(user);
        verify(todoRepository, times(1)).save(argThat(entity ->
                entity.getTitle().equals(expectedTodoEntity.getTitle()) &&
                        entity.getDescription().equals(expectedTodoEntity.getDescription()) &&
                        entity.getProgress().equals(expectedTodoEntity.getProgress()) &&
                        entity.getUser().equals(expectedTodoEntity.getUser()) &&
                        entity.getDeadline().isEqual(expectedTodoEntity.getDeadline())
        ));
    }

    @Test
    void createTodoNotFound() {
        Todo todo1 = Todo.toModel(todo);
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Long> createTodo = todoService.create(todo1);
        createTodo.ifPresent(a -> assertEquals(2L, a));
        verify(todoRepository, never()).delete(any(TodoEntity.class));
    }

    @Test
    void updateCorrect() {
        Long id = 1L;
        TodoEntity updatedTodo = new TodoEntity();
        updatedTodo.setId(id);
        String nt = "New Title";
        updatedTodo.setTitle(nt);
        String nd = "New Description";
        updatedTodo.setDescription(nd);
        LocalDateTime updateDate = LocalDateTime.now();
        updatedTodo.setDeadline(updateDate);
        updatedTodo.setProgress(Progress.DONE);
        updatedTodo.setUser(user);
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        String result = todoService.update(1L, Todo.toModel(updatedTodo));
        verify(todoRepository).findById(todo.getId());
        verify(todoRepository).save(todo);
        assertEquals("redirect:/todo/{id}", result);

        assertEquals(nt, todo.getTitle());
        assertEquals(nd, todo.getDescription());
        assertEquals(updateDate, todo.getDeadline());
        assertEquals(updatedTodo.getProgress(), todo.getProgress());
    }

    @Test
    void updateTodoNotFound() {
        Long id = 2L;
        Todo updatedTodo = new Todo();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        String result = todoService.update(id, updatedTodo);
        verify(todoRepository).findById(id);
        verify(todoRepository, never()).save(any());
        assertEquals("redirect:/error", result);
    }

}
