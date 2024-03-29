package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, Status status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, Status status, String description, long duration, LocalDateTime startTime, int epicId) {
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, Status status, String description, long duration, LocalDateTime startTime, int epicId) {
        super(name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                ", status = " + status;
    }
}
