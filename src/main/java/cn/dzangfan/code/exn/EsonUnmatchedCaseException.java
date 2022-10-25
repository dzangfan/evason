package cn.dzangfan.code.exn;

import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonValue;

@SuppressWarnings("serial")
public class EsonUnmatchedCaseException extends EsonException {
    @SuppressWarnings("unused")
    private EsonLambda lambda;
    @SuppressWarnings("unused")
    private EsonValue value;

    public EsonUnmatchedCaseException(EsonLambda lambda, EsonValue value) {
        super();
        this.lambda = lambda;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Unable to a find matchable branch";
    }

}
