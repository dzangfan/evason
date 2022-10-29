package cn.dzangfan.code.eson.data.function;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonTypeException;

public class CheckType<T extends EsonValue> extends CaseFunction<T> {
    @SuppressWarnings("unchecked")
    private CheckType(GetType.Type expectedType) {
        super(value -> {
            if (value.on(GetType.getInstance()) != expectedType) {
                throw EsonRuntimeException
                        .causedBy(new EsonTypeException(value, expectedType));
            }
            return (T) value;
        });
    }

    public static final CheckType<EsonNumber> NUMBER
            = new CheckType<EsonNumber>(GetType.Type.NUMBER);
    public static final CheckType<EsonSymbol> SYMBOL
            = new CheckType<EsonSymbol>(GetType.Type.SYMBOL);
    public static final CheckType<EsonString> STRING
            = new CheckType<EsonString>(GetType.Type.STRING);
    public static final CheckType<EsonSpecialValue> BOOLEAN
            = new CheckType<EsonSpecialValue>(GetType.Type.BOOLEAN);
    public static final CheckType<EsonSpecialValue> NULL
            = new CheckType<EsonSpecialValue>(GetType.Type.NULL);
    public static final CheckType<EsonID> ID
            = new CheckType<EsonID>(GetType.Type.ID);
    public static final CheckType<EsonObject> OBJECT
            = new CheckType<EsonObject>(GetType.Type.OBJECT);
    public static final CheckType<EsonArray> ARRAY
            = new CheckType<EsonArray>(GetType.Type.ARRAY);
    public static final CheckType<EsonLambda> LAMBDA
            = new CheckType<EsonLambda>(GetType.Type.LAMBDA);
    public static final CheckType<EsonApplication> APPLICATION
            = new CheckType<EsonApplication>(GetType.Type.APPLICATION);

}
