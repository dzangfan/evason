package cn.dzangfan.code.eson.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dzangfan.code.eson.data.function.Evaluate;
import cn.dzangfan.code.eson.data.function.IsCondition;
import cn.dzangfan.code.eson.data.function.Match;
import cn.dzangfan.code.eson.exn.EsonException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonUnmatchedCaseException;
import cn.dzangfan.code.eson.rumtime.Environment;

public class EsonLambda extends EsonValue {

    private List<Branch> content;

    private Environment environment;

    public static class Branch {
        private EsonValue condition;
        private EsonValue value;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Branch branch && obj != null) {
                return condition.equals(branch.condition)
                        && value.equals(branch.value);
            } else
                return false;
        }

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
        this(content, Environment.ROOT);
    }

    private EsonLambda(List<Branch> content, Environment environment) {
        super();
        this.content = content;
        this.environment = environment;
    }

    public static EsonLambda from(Collection<Branch> content) {
        return new EsonLambda(content.stream().toList());
    }

    public static EsonLambda from(Branch... content) {
        return new EsonLambda(Arrays.asList(content));
    }

    public EsonLambda in(Environment environment) {
        return new EsonLambda(content, environment);
    }

    public List<Branch> getContent() {
        return content;
    }

    public void setContent(List<Branch> content) {
        this.content = content;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenLambda(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonLambda lambda && obj != null) {
            return environment.equals(lambda.environment)
                    && content.equals(lambda.content);
        } else
            return false;
    }

    public EsonValue apply(EsonValue operand) {
        try {
            for (Branch branch : content) {
                Match.Result result
                        = operand.on(Match.with(branch.getCondition()));
                if (result.isMatched()) {
                    Environment localEnvironment
                            = environment.extend(result.getSymbolTable());
                    return branch.value.on(Evaluate.in(localEnvironment));
                }
            }
            throw new EsonUnmatchedCaseException(this, operand);
        } catch (EsonException e) {
            throw EsonRuntimeException.causedBy(e);
        }
    }

}
