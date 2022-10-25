package cn.dzangfan.code.data.function;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.exn.EsonTypeException;

public abstract class ExceptionCaseFunction<T> extends CaseFunction<T> {
    public ExceptionCaseFunction(GetType.Type expectedType) {
        super(value -> {
            throw EsonRuntimeException
                    .causedBy(new EsonTypeException(value, expectedType));
        });
    }
}
