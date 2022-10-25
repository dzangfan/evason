package cn.dzangfan.code.data;

public class EsonApplication extends EsonValue {

    private EsonValue operator;
    private EsonValue operand;

    public EsonValue getOperator() {
        return operator;
    }

    public void setOperator(EsonValue operator) {
        this.operator = operator;
    }

    public EsonValue getOperand() {
        return operand;
    }

    public void setOperand(EsonValue operand) {
        this.operand = operand;
    }

    private EsonApplication(EsonValue operator, EsonValue operand) {
        super();
        this.operator = operator;
        this.operand = operand;
    }

    public static EsonApplication from(EsonValue operator, EsonValue operand) {
        return new EsonApplication(operator, operand);
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenApplication(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonApplication application && obj != null) {
            return operator.equals(application.operator)
                    && operand.equals(application.operand);
        }
        return false;
    }
}
