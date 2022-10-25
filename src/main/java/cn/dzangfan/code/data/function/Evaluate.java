package cn.dzangfan.code.data.function;

import java.util.List;
import java.util.function.Supplier;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonApplication;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonNumber;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonObject.Entry;
import cn.dzangfan.code.data.EsonSpecialValue;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonSymbol;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.exn.EsonRedefinitionException;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.exn.EsonUndefinedIDException;
import cn.dzangfan.code.rumtime.Environment;
import cn.dzangfan.code.rumtime.SymbolTable;

public class Evaluate extends CaseFunction<EsonValue> {

    private Environment environment;

    private Evaluate(Environment environment) {
        super();
        this.environment = environment;
    }

    public static Evaluate in(Environment environment) {
        return new Evaluate(environment);
    }

    @Override
    public EsonValue whenSymbol(EsonSymbol symbol) {
        return symbol;
    }

    @Override
    public EsonValue whenString(EsonString string) {
        return string;
    }

    @Override
    public EsonValue whenNumber(EsonNumber number) {
        return number;
    }

    @Override
    public EsonValue whenBoolean(boolean value) {
        return EsonSpecialValue.from(value);
    }

    @Override
    public EsonValue whenNull() {
        return EsonSpecialValue.NULL;
    }

    @Override
    public EsonValue whenID(EsonID id) {
        try {
            SymbolTable.Entry entry = environment.find(id);
            return entry.getValue();
        } catch (EsonUndefinedIDException e) {
            EsonRuntimeException re = new EsonRuntimeException();
            re.initCause(e);
            throw re;
        }
    }

    @Override
    public EsonValue whenObject(EsonObject object) {
        Environment localEnvironment = environment.extend();
        EsonObject newObject = EsonObject.from();
        for (Entry entry : object.getContent()) {
            EsonValue value
                    = entry.getValue().on(Evaluate.in(localEnvironment));
            try {
                localEnvironment.define(entry.getKey(), value);
            } catch (EsonRedefinitionException e) {
                EsonRuntimeException re = new EsonRuntimeException();
                re.initCause(e);
                throw re;
            }
            newObject.add(entry.getKey(), value);
        }
        return newObject;
    }

    @Override
    public EsonValue whenArray(EsonArray array) {
        List<EsonValue> evaluatedContent = array.getContent().stream()
                .map(value -> value.on(this)).toList();
        return EsonArray.from(evaluatedContent);
    }

    @Override
    public EsonValue whenLambda(EsonLambda lambda) {
        return lambda.in(environment);
    }

    @Override
    public EsonValue whenApplication(EsonApplication application) {
        EsonValue operator = application.getOperator().on(this);
        Supplier<EsonValue> operandSupplier
                = () -> application.getOperand().on(this);
        return operator.on(Apply.to(operandSupplier));
    }

}
