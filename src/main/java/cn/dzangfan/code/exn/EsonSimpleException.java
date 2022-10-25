package cn.dzangfan.code.exn;

@SuppressWarnings("serial")
public class EsonSimpleException extends EsonException {
    private String message = "Unknown error has occurred";

    public EsonSimpleException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
