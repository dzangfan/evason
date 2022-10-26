package cn.dzangfan.code.eson.exn;

import cn.dzangfan.code.eson.data.EsonValue;

@SuppressWarnings("serial")
public class EsonNotConditionException extends EsonException {
    @SuppressWarnings("unused")
    private EsonValue condition;

    public EsonNotConditionException(EsonValue condition) {
        super();
        this.condition = condition;
    }

    @Override
    public String getMessage() {
        return "Condition should not is or contain lambda and application";
    }

}
