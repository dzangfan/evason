package cn.dzangfan.code.eson.exn;

import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.GetType;

@SuppressWarnings("serial")
public class EsonUnapplicableOperatorException extends EsonException {
    private EsonValue operator;

    public EsonUnapplicableOperatorException(EsonValue operator) {
        super();
        this.operator = operator;
    }

    @Override
    public String getMessage() {
        return String.format("%s cannot be used as a operator",
                             operator.on(GetType.getInstance()).toString());
    }

}
