package exeptions;

public class ManagerCrossingException extends RuntimeException  {
    public ManagerCrossingException() {
        super("Задачи пересекаются!");
    }
}
