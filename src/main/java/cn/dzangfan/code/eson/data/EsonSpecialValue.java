package cn.dzangfan.code.eson.data;

import java.util.Objects;

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

    public static EsonSpecialValue from(Boolean nullableBoolean) {
        if (Objects.isNull(nullableBoolean))
            return NULL;
        if (nullableBoolean)
            return TRUE;
        return FALSE;
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
