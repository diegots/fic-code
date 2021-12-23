package simpleknn.exceptions;

public class ArgumentsException extends Throwable {

    private final String reason;

    public ArgumentsException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
