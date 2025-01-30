package todo.service;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import todo.Application;
import todo.Progress;
import todo.Role;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;
import todo.exception.UserNotFoundException;
import todo.models.User;
import todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private TodoService todoService;

    private PasswordEncoder passwordEncoder;

    private UserEntity user;
    private Model model;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        todoService = Mockito.mock(TodoService.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, todoService, passwordEncoder);
        model = Mockito.mock(Model.class);
        user = new UserEntity();
        user.setUsername("test");
        user.setName("Test User");
        user.setPassword("123"); // Либо значение, которое вы хотите
        user.setRole(Role.GUEST);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_correct() {
        userService.save(user);
        verify(userRepository).save(user);
    }

    @Test
    void delete_correct() {
        userService.delete(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void addUser_correct() {
        userService.addUser(user);
        verify(userRepository).save(user);
    }

    @Test
    void findAll_correct() {
        userService.findAll();
        verify(userRepository).findAll();
    }

    @Test
    void findByUsername_correct() {
        userService.findByUsername(user.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void findById_correct() throws UserNotFoundException {
        userService.findById(user.getId());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void findById_userNotFoundThrowsException() {
        Long userId = 4L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void show_correct() {
        String result = userService.show(user.getId(), null, null, model);
        assertEquals("user/show", result);
    }

    @Test
    void show_error() {
        String result = userService.show(4L, null, null, model);
        assertEquals("redirect:/error", result);
    }

    @Test
    void update_correct() {
        UserEntity updatedUser = new UserEntity();
        updatedUser.setName("Updated User");
        updatedUser.setPassword("1234");
        updatedUser.setRole(Role.USER);
        String result = userService.update(updatedUser, user.getId());
        verify(userRepository).save(any(UserEntity.class));
        assertEquals("redirect:/user/{id}", result);
    }

    @Test
    void update_error() {
        String result = userService.update(user, 4L);
        assertEquals("redirect:/error", result);
    }



}
