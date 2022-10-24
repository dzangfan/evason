package cn.dzangfan.code.exn;

import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.data.function.GetType;

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
