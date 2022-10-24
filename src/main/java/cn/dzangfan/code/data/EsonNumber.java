package cn.dzangfan.code.data;

public class EsonNumber extends EsonValue {

    private double value;
    private boolean isInteger;

    private EsonNumber(double value, boolean isInteger) {
        super();
        this.value = value;
        this.isInteger = isInteger;
    }

    public static EsonNumber fromInteger(Integer integer) {
        return new EsonNumber(integer, true);
    }

    public static EsonNumber fromDouble(Double value) {
        return new EsonNumber(value, false);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isInteger() {
        return isInteger;
    }

    public void setInteger(boolean isInteger) {
        this.isInteger = isInteger;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenNumber(this);
    }

}
