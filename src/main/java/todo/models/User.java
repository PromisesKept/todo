package todo.models;

import todo.Role;
import todo.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class User {
    private Long id;
    private String username;
    private String name;
    private List<Todo> todos;
    private LocalDateTime registered;
    private Role role;

    public static User toModel(UserEntity entity) {
        User model = new User();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setTodos(entity.getTodos().stream().map(Todo::toModel).collect(Collectors.toList()));
        model.setRegistered(entity.getRegistered());
        model.setRole(entity.getRole());

        return model;
    }


    public String getUsername() {
        return username;
    }
    // Нужен, тк это модель!
    private void setId(Long id) {
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


}
