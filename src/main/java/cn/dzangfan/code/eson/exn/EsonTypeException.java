package cn.dzangfan.code.eson.exn;

import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.GetType;
import cn.dzangfan.code.eson.data.function.GetType.Type;

@SuppressWarnings("serial")
public class EsonTypeException extends EsonException {
    private EsonValue value;
    private GetType.Type expectedType;

    public EsonTypeException(EsonValue value, Type expectedType) {
        super();
        this.value = value;
        this.expectedType = expectedType;
    }

    @Override
    public String getMessage() {
        return String.format("Expected a %s, but found a %s", expectedType,
                             value.on(GetType.getInstance()));
    }

}
