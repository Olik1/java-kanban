package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected List<Integer> subTasks;
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public Epic(int id, String name, Status status, String description, List<Integer> subTasks) {
        super(id, name, status, description);
        this.subTasks = subTasks;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubtaskId(SubTask subTask) {
        subTasks.add(subTask.getEpicId());
    }

    public void removeSubTaskId(int id) {
        subTasks.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks, endTime);
    }

    @Override
    public String toString() {
        return "Epic " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", status = " + status +
                ", subTasks = " + subTasks +  '\'' +
                ", duration = " + getDuration() + '\'' +
                ", startTime = " + getStartTime() + '\'' +
                ", endTime = " + endTime;
    }
}
