package cn.dzangfan.code.exn;

import cn.dzangfan.code.data.EsonValue;

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
