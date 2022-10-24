package cn.dzangfan.code.data;

public class EsonID extends EsonValue {

    private String name;

    private EsonID(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EsonID from(String name) {
        return new EsonID(name);
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenID(this);
    }

}
