package todo.models;

import todo.Progress;
import todo.entity.TodoEntity;
import todo.entity.UserEntity;

import java.time.LocalDateTime;


public class Todo {

    private UserEntity user;
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime start;
    private Progress progress;

    public static Todo toModel(TodoEntity entity) {
            Todo model = new Todo();
            model.setUser(entity.getUser());
            model.setId(entity.getId());
            model.setProgress(entity.getProgress());
            model.setTitle(entity.getTitle());
            model.setDescription(entity.getDescription());
            model.setDeadline(entity.getDeadline());
            model.setStart(entity.getStart());
            return model;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

}
