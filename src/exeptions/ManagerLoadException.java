package exeptions;

public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
