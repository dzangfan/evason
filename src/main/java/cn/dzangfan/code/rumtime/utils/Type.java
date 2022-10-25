package cn.dzangfan.code.rumtime.utils;

import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.data.function.GetType;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.exn.EsonTypeException;

public class Type {
    public static void ensure(GetType.Type expectedType, EsonValue value)
            throws EsonTypeException {
        if (!value.on(GetType.getInstance()).equals(expectedType)) {
            throw new EsonTypeException(value, expectedType);
        }
    }

    public static void ensureRuntime(GetType.Type expectedType,
                                     EsonValue value) {
        try {
            ensure(expectedType, value);
        } catch (EsonTypeException e) {
            throw EsonRuntimeException.causedBy(e);
        }
    }
}
