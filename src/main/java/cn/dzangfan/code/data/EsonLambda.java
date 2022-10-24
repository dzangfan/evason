package cn.dzangfan.code.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dzangfan.code.data.function.IsCondition;

public class EsonLambda extends EsonValue {

    private List<Branch> content;

    public static class Branch {
        private EsonValue condition;
        private EsonValue value;

        private Branch(EsonValue condition, EsonValue value) {
            super();
            this.condition = condition;
            this.value = value;
        }

        public static Branch from(EsonValue condition, EsonValue value) {
            if (!condition.on(IsCondition.getInstance())) {
                throw new IllegalArgumentException(
                        "condition must satify Eson::isCondition");
            }
            return new Branch(condition, value);
        }

        public EsonValue getCondition() {
            return condition;
        }

        public void setCondition(EsonValue condition) {
            this.condition = condition;
        }

        public EsonValue getValue() {
            return value;
        }

        public void setValue(EsonValue value) {
            this.value = value;
        }

    }

    private EsonLambda(List<Branch> content) {
        super();
        this.content = content;
    }

    public static EsonLambda from(Collection<Branch> content) {
        return new EsonLambda(content.stream().toList());
    }

    public static EsonLambda from(Branch... content) {
        return new EsonLambda(Arrays.asList(content));
    }

    public List<Branch> getContent() {
        return content;
    }

    public void setContent(List<Branch> content) {
        this.content = content;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenLambda(this);
    }

}
