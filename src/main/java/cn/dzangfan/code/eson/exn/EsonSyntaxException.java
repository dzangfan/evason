package cn.dzangfan.code.eson.exn;

@SuppressWarnings("serial")
public class EsonSyntaxException extends EsonException {

    @Override
    public String getMessage() {
        return "A syntactic error has been found";
    }

}
