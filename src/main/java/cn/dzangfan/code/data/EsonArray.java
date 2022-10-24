package cn.dzangfan.code.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EsonArray extends EsonValue {

    private List<EsonValue> content;

    private EsonArray(List<EsonValue> content) {
        super();
        this.content = content;
    }

    public static EsonArray from(Collection<EsonValue> content) {
        return new EsonArray(content.stream().toList());
    }

    public static EsonArray from(EsonValue... content) {
        return new EsonArray(Arrays.asList(content));
    }

    public List<EsonValue> getContent() {
        return content;
    }

    public void setContent(List<EsonValue> content) {
        this.content = content;
    }

    @Override
    public boolean isCondition() {
        return content.stream().allMatch(EsonValue::isCondition);
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenArray(this);
    }

}
