package cn.dzangfan.code.exn;

@SuppressWarnings("serial")
public class EsonRuntimeException extends RuntimeException {
    public static EsonRuntimeException causedBy(Throwable cause) {
        EsonRuntimeException re = new EsonRuntimeException();
        re.initCause(cause);
        return re;
    }
}
