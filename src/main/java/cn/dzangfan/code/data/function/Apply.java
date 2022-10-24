package cn.dzangfan.code.data.function;

import cn.dzangfan.code.data.CaseFunction;
import cn.dzangfan.code.data.EsonArray;
import cn.dzangfan.code.data.EsonLambda;
import cn.dzangfan.code.data.EsonObject;
import cn.dzangfan.code.data.EsonString;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.exn.EsonRuntimeException;
import cn.dzangfan.code.exn.EsonUnapplicableOperatorException;

public class Apply extends CaseFunction<EsonValue> {
    private EsonValue operand;

    private Apply(EsonValue operand) {
        super(operator -> {
            EsonUnapplicableOperatorException e
                    = new EsonUnapplicableOperatorException(operator);
            EsonRuntimeException re = new EsonRuntimeException();
            re.initCause(e);
            throw re;
        });
        this.operand = operand;
    }

    @Override
    public EsonValue whenString(EsonString string) {
        // TODO Auto-generated method stub
        return super.whenString(string);
    }

    @Override
    public EsonValue whenBoolean(boolean value) {
        // TODO Auto-generated method stub
        return super.whenBoolean(value);
    }

    @Override
    public EsonValue whenObject(EsonObject object) {
        // TODO Auto-generated method stub
        return super.whenObject(object);
    }

    @Override
    public EsonValue whenArray(EsonArray array) {
        // TODO Auto-generated method stub
        return super.whenArray(array);
    }

    @Override
    public EsonValue whenLambda(EsonLambda lambda) {
        // TODO Auto-generated method stub
        return super.whenLambda(lambda);
    }

}
