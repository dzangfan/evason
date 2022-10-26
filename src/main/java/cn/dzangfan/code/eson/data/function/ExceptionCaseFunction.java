package cn.dzangfan.code.eson.data.function;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonTypeException;

public abstract class ExceptionCaseFunction<T> extends CaseFunction<T> {
    public ExceptionCaseFunction(GetType.Type expectedType) {
        super(value -> {
            throw EsonRuntimeException
                    .causedBy(new EsonTypeException(value, expectedType));
        });
    }
}
