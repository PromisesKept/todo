package todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;
import todo.repository.TodoRepository;

import java.util.List;
import java.util.Optional;

public class TodoServiceAiTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoEntity todo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todo = new TodoEntity();
        todo.setId(1L);
        todo.setUser(new UserEntity()); // заполнение пользователя, если необходимо
    }

    @Test
    void testDeleteTodoExists() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(todo));

        todoService.delete(1L);

        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void testDeleteTodoNotFound() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        todoService.delete(1L);

        verify(todoRepository, times(0)).delete(any());
    }

    @Test
    void testGetAllTodos() {
        when(todoRepository.findAll()).thenReturn(List.of(todo));

        List<TodoEntity> result = todoService.getAllTodos();

        assertEquals(1, result.size());
        assertEquals(todo, result.get(0));
    }

    @Test
    void testFindAllByStart() {
        Long userId = 1L;
        when(todoRepository.findByUserIdOrderByStartAsc(userId)).thenReturn(List.of(todo));

        List<TodoEntity> result = todoService.findAllByStart(userId);

        assertEquals(1, result.size());
        assertEquals(todo, result.get(0));
        verify(todoRepository, times(1)).findByUserIdOrderByStartAsc(userId);
    }

    @Test
    void testFindAllByDeadline() {
        Long userId = 1L;
        when(todoRepository.findByUserIdOrderByDeadlineAsc(userId)).thenReturn(List.of(todo));

        List<TodoEntity> result = todoService.findAllByDeadline(userId);

        assertEquals(1, result.size());
        assertEquals(todo, result.get(0));
        verify(todoRepository, times(1)).findByUserIdOrderByDeadlineAsc(userId);
    }

    @Test
    void testFindAllByProgress() {
        Long userId = 1L;
        when(todoRepository.findByUserIdOrderByProgressAsc(userId)).thenReturn(List.of(todo));

        List<TodoEntity> result = todoService.findAllByProgress(userId);

        assertEquals(1, result.size());
        assertEquals(todo, result.get(0));
        verify(todoRepository, times(1)).findByUserIdOrderByProgressAsc(userId);
    }
}
