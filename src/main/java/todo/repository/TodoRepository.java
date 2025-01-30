package todo.repository;

import org.springframework.data.repository.CrudRepository;
import todo.entity.TodoEntity;

import java.util.List;

public interface TodoRepository extends CrudRepository<TodoEntity, Long> {

    List<TodoEntity> findByUserIdOrderByStartAsc(Long userId); // Для сортировки по дате создания
    List<TodoEntity> findByUserIdOrderByDeadlineAsc(Long userId); // Для сортировки по дедлайну
    List<TodoEntity> findByUserIdOrderByProgressAsc(Long userId); // Для сортировки по статусу

}
