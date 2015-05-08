package ca.vorona.bertapull;

public class LogicException extends Exception {

    private static final long serialVersionUID = 1L;

    public LogicException() {
        super();
    }

    public LogicException(String arg0, Throwable arg1, boolean arg2,
            boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public LogicException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public LogicException(String arg0) {
        super(arg0);
    }

    public LogicException(Throwable arg0) {
        super(arg0);
    }

}
