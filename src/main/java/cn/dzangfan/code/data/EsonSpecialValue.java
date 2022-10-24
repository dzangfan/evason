package cn.dzangfan.code.data;

public class EsonSpecialValue extends EsonValue {

    private EsonSpecialValue() {

    }

    public static final EsonSpecialValue TRUE = new EsonSpecialValue();

    public static final EsonSpecialValue FALSE = new EsonSpecialValue();

    public static final EsonSpecialValue NULL = new EsonSpecialValue();

    @Override
    public <T> T on(CaseFunction<T> function) {
        if (isTrue()) {
            return function.whenBoolean(true);
        } else if (isFalse()) {
            return function.whenBoolean(false);
        } else {
            return function.whenNull();
        }
    }

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean isNull() {
        return this == NULL;
    }

}
