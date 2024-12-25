package manager.exception;

public class IdNotPresentException extends RuntimeException {

    private final Integer id;

    public IdNotPresentException(Integer id) {
        super("Id is not present in manager.");
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
