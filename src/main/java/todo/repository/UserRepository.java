package todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todo.entity.UserEntity;
import todo.models.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
