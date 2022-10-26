package cn.dzangfan.code.eson.rumtime.utils;

import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.GetType;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonTypeException;

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
