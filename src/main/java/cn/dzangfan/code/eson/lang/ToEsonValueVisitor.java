package cn.dzangfan.code.eson.lang;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonLambda.Branch;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.IsCondition;
import cn.dzangfan.code.eson.exn.EsonNotConditionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.lang.EsonBaseVisitor;
import cn.dzangfan.code.lang.EsonParser.ApplicationContext;
import cn.dzangfan.code.lang.EsonParser.DoubleNumberContext;
import cn.dzangfan.code.lang.EsonParser.EmptyArrayContext;
import cn.dzangfan.code.lang.EsonParser.EmptyObjectContext;
import cn.dzangfan.code.lang.EsonParser.IntegerNumberContext;
import cn.dzangfan.code.lang.EsonParser.LambdaContext;
import cn.dzangfan.code.lang.EsonParser.LoadedArrayContext;
import cn.dzangfan.code.lang.EsonParser.LoadedObjectContext;
import cn.dzangfan.code.lang.EsonParser.ValueIdContext;
import cn.dzangfan.code.lang.EsonParser.ValueMapNormalContext;
import cn.dzangfan.code.lang.EsonParser.ValueMapShortcutContext;
import cn.dzangfan.code.lang.EsonParser.ValueSpecialContext;
import cn.dzangfan.code.lang.EsonParser.ValueStringContext;
import cn.dzangfan.code.lang.EsonParser.ValueSymbolContext;

public class ToEsonValueVisitor extends EsonBaseVisitor<EsonValue> {

    @Override
    public EsonValue visitValueId(ValueIdContext ctx) {
        return EsonID.from(ctx.ID().getText());
    }

    @Override
    public EsonValue visitValueSpecial(ValueSpecialContext ctx) {
        switch (ctx.SPECIALVALUE().getText()) {
        case "true":
            return EsonSpecialValue.TRUE;
        case "false":
            return EsonSpecialValue.FALSE;
        case "null":
            return EsonSpecialValue.NULL;
        default:
            throw new EsonRuntimeException();
        }
    }

    @Override
    public EsonValue visitValueMapNormal(ValueMapNormalContext ctx) {
        EsonValue param = visit(ctx.condValue());
        EsonValue result = visit(ctx.value());
        if (!param.on(IsCondition.getInstance())) {
            throw EsonRuntimeException
                    .causedBy(new EsonNotConditionException(param));
        }
        Branch branch = Branch.from(param, result);
        return EsonLambda.from(branch);
    }

    @Override
    public EsonValue visitLoadedArray(LoadedArrayContext ctx) {
        List<EsonValue> content
                = ctx.value().stream().map(this::visit).toList();
        EsonArray array = EsonArray.from(content);
        return ctx.ID() == null ? array
                : array.withRest(EsonID.from(ctx.ID().getText()));
    }

    @Override
    public EsonValue visitLoadedObject(LoadedObjectContext ctx) {
        List<Entry> content = ctx.objectPair().stream().map(pair -> {
            EsonID key = EsonID.from(pair.ID().getText());
            EsonValue value = pair.value() == null ? key : visit(pair.value());
            return Entry.from(key, value);
        }).toList();
        EsonObject object = EsonObject.from(content);
        return ctx.ID() == null ? object
                : object.withRest(EsonID.from(ctx.ID().getText()));
    }

    @Override
    public EsonValue visitLambda(LambdaContext ctx) {
        List<Branch> branchList = ctx.valueMap().stream().map(this::visit)
                .map(v -> (EsonLambda) v).map(v -> v.getContent().get(0))
                .toList();
        return EsonLambda.from(branchList);
    }

    @Override
    public EsonValue visitValueSymbol(ValueSymbolContext ctx) {
        return EsonSymbol.from(ctx.SYMBOL().getText().substring(1));
    }

    private static final Map<Character, String> escapeTable
            = Map.of('/', "/", 'b', "\b", 'f', "\f", 'n', "\n", 'r', "\r", 't',
                     "\t", '"', "\"", '\\', "\\");

    @Override
    public EsonValue visitValueString(ValueStringContext ctx) {
        StringBuilder sb = new StringBuilder();
        String source = ctx.STRING().getText();
        int pointer = 1;
        while (pointer < source.length() - 1) {
            char nextChar = source.charAt(pointer);
            if (nextChar == '\\') {
                char escaped = source.charAt(++pointer);
                sb.append(Objects.requireNonNull(escapeTable.get(escaped)));
            } else {
                sb.append(nextChar);
            }
            ++pointer;
        }
        return EsonString.from(sb.toString());
    }

    @Override
    public EsonValue visitApplication(ApplicationContext ctx) {
        List<EsonValue> values = ctx.value().stream().map(this::visit).toList();
        EsonValue currentOperator = values.get(0);
        for (int i = 1; i < values.size(); ++i) {
            EsonValue operand = values.get(i);
            currentOperator = EsonApplication.from(currentOperator, operand);
        }
        return currentOperator;
    }

    @Override
    public EsonValue visitValueMapShortcut(ValueMapShortcutContext ctx) {
        List<EsonValue> params = ctx.params.stream().map(this::visit).toList();
        EsonValue result = visit(ctx.result);
        EsonLambda currentEsonLambda = null;
        for (int i = params.size() - 1; i >= 0; --i) {
            EsonValue param = params.get(i);
            EsonValue value
                    = currentEsonLambda == null ? result : currentEsonLambda;
            Branch branch = Branch.from(param, value);
            currentEsonLambda = EsonLambda.from(branch);
        }
        return Objects.requireNonNull(currentEsonLambda);
    }

    @Override
    public EsonValue visitIntegerNumber(IntegerNumberContext ctx) {
        Integer integer = Integer.parseInt(ctx.INTEGER().getText());
        return EsonNumber.fromInteger(integer);
    }

    @Override
    public EsonValue visitDoubleNumber(DoubleNumberContext ctx) {
        Double double_ = Double.parseDouble(ctx.DOUBLE().getText());
        return EsonNumber.fromDouble(double_);
    }

    @Override
    public EsonValue visitEmptyArray(EmptyArrayContext ctx) {
        return EsonArray.from();
    }

    @Override
    public EsonValue visitEmptyObject(EmptyObjectContext ctx) {
        return EsonObject.from();
    }

}
