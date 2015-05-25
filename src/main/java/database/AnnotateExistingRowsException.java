package database;

public class AnnotateExistingRowsException extends RuntimeException {
    //TODO: Should really extend Exception, not RuntimeException, but I don't want to have to wrap all 207 instances of new Processor() (eg. in tests) with try-catches.

    public AnnotateExistingRowsException(String message) {
        super(message);
    }
}
