package cn.dzangfan.code.data;

public class EsonNumber extends EsonValue {

    private Double value;
    private boolean isInteger;

    private EsonNumber(Double value, boolean isInteger) {
        super();
        this.value = value;
        this.isInteger = isInteger;
    }

    public static EsonNumber fromInteger(Integer integer) {
        return new EsonNumber(integer.doubleValue(), true);
    }

    public static EsonNumber fromDouble(Double value) {
        return new EsonNumber(value, false);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonNumber number && number != null) {
            return value.equals(number.value);
        }
        return false;
    }

}
