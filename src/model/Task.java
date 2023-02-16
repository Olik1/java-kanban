package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected static int counter = 1;// переменная для создания корректного айди
    private final TaskType taskType = TaskType.TASK;
    protected int id = counter++;
    protected String name;
    protected String description;
    protected Status status;
    protected long duration; //продолжительность задачи в минутах
    protected LocalDateTime startTime; //когда приступить к задаче - время

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
        this(name, description);
        this.status = status;
    }

    public Task(int id, String name, Status status, String description) {
        this.id = id;
        if (counter <= id) counter = id++;
        this.name = name;
        this.status = status;
        this.description = description;

    }

    public Task(int id, String name, Status status, String description, long duration, LocalDateTime startTime) {
        this.id = id;
        if (counter <= id) counter = id++;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() { //время завершения задачи
        return startTime.plusMinutes(duration);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && taskType == task.taskType
                && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskType, id, name, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task " +
                "id = '" + id + '\'' +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", status = '" + status + '\'' +
                ", duration = '" + duration + '\'' +
                ", startTime = '" + startTime + '\'' +
                ", endTime = '" + getEndTime() + '\'';
    }
}