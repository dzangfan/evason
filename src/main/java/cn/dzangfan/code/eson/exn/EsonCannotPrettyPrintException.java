package cn.dzangfan.code.eson.exn;

@SuppressWarnings("serial")
public class EsonCannotPrettyPrintException extends EsonException {

    @Override
    public String getMessage() {
        return "Cannnot pretty print application and identifier";
    }

}
