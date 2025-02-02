package todo.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import todo.Application;
import todo.enums.Role;
import todo.entity.UserEntity;
import todo.exception.UserNotFoundException;
import todo.models.User;
import todo.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        user.setPassword("123");
        user.setRole(Role.GUEST);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void saveCorrect() {
        User userDto = User.toModel(user);
        userService.save(userDto);
        verify(userRepository).save(user);
    }

    @Test
    void deleteCorrect() {
        userService.delete(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void addUserCorrect() {
        userService.addUser(User.toModel(user));
        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        // Проверяем, что свойства захваченной сущности совпадают с теми, которые мы ожидали
        UserEntity savedEntity = userEntityCaptor.getValue();

        assertEquals(user.getName(), savedEntity.getName());
        assertEquals(passwordEncoder.encode(user.getPassword()), savedEntity.getPassword());
        assertEquals(user.getRole(), savedEntity.getRole());
    }

    @Test
    void findAllCorrect() {
        userService.findAll();
        verify(userRepository).findAll();
    }

    @Test
    void findByUsernameCorrect() {
        userService.findByUsername(user.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void findByIdCorrect() throws UserNotFoundException {
        userService.findById(user.getId());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void findByIdUserNotFoundThrowsException() {
        Long userId = 4L; // этого точно нет (и не будет) в БД
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void showCorrect() {
        String result = userService.show(user.getId(), null, null, model);
        assertEquals("user/show", result);
    }

    @Test
    void showError() {
        String result = userService.show(4L, null, null, model);
        assertEquals("redirect:/error", result);
    }

    @Test
    void updateCorrect() {
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName("Updated User");
        updatedUser.setPassword("1234");
        updatedUser.setRole(Role.USER);
        String result = userService.update(updatedUser, user.getId());
        verify(userRepository).save(any(UserEntity.class));
        assertEquals("redirect:/user/{id}", result);
    }

    @Test
    void updateError() {
        String result = userService.update(User.toModel(user), 4L);
        assertEquals("redirect:/error", result);
    }



}
