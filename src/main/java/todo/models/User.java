package todo.models;

import todo.entity.TodoEntity;
import todo.enums.Role;
import todo.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private List<Todo> todos;
    private LocalDateTime registered;
    private Role role;

    public static User toModel(UserEntity entity) {
        User model = new User();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setPassword(entity.getPassword()); // мне так не нравится...
        model.setName(entity.getName());
        model.setTodos(entity.getTodos().stream().map(Todo::toModel).collect(Collectors.toList()));
        model.setRegistered(entity.getRegistered());
        model.setRole(entity.getRole());

        return model;
    }

    public static UserEntity toEntity(User model) {
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setUsername(model.getUsername());
        entity.setPassword(model.getPassword()); // мне так не нравится...
        entity.setName(model.getName());
        entity.setRole(model.getRole());

        // Todo в TodoEntity
        if (model.getTodos() != null) {
            List<TodoEntity> todoEntities = model.getTodos().stream()
                    .map(Todo::toEntity)
                    .collect(Collectors.toList());
            entity.setTodos(todoEntities);
        }

        return entity;
    }


    public String getUsername() {
        return username;
    }
    // Нужен, тк это модель!
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Todo> getTodos() {
        return todos;
    }
    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public LocalDateTime getRegistered() {
        return registered;
    }
    // А тут нужен! Это модель!
    public void setRegistered(LocalDateTime registered) {
        this.registered = registered;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


}
