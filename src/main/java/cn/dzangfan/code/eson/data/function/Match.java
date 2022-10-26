package cn.dzangfan.code.eson.data.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonApplication;
import cn.dzangfan.code.eson.data.EsonArray;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonLambda;
import cn.dzangfan.code.eson.data.EsonNumber;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonSpecialValue;
import cn.dzangfan.code.eson.data.EsonString;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.exn.EsonNotConditionException;
import cn.dzangfan.code.eson.exn.EsonNotFinalValueException;
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.rumtime.SymbolTable;

public class Match extends CaseFunction<Match.Result> {

    public static class Result {
        private boolean matched;
        private SymbolTable symbolTable;

        private Result(boolean matched, SymbolTable symbolTable) {
            super();
            this.matched = matched;
            this.symbolTable = symbolTable;
        }

        private static final Result UNMATCHED = new Result(false, null);

        public static Result matched(SymbolTable symbolTable) {
            return new Result(true, symbolTable);
        }

        public static Result matched() {
            return Result.matched(SymbolTable.newEmpty());
        }

        public static Result unmatched() {
            return UNMATCHED;
        }

        public boolean isMatched() {
            return matched;
        }

        public SymbolTable getSymbolTable() {
            return symbolTable;
        }

    }

    private static class ConditionMatcher extends CaseFunction<Match.Result> {

        private EsonValue value;

        public ConditionMatcher(EsonValue value) {
            this();
            this.value = value;
        }

        private ConditionMatcher() {
            super((__) -> Result.unmatched());
        }

        @Override
        public Result whenID(EsonID id) {
            SymbolTable symbolTable = SymbolTable.newEmpty();
            symbolTable.put(id, value);
            return Result.matched(symbolTable);
        }

        private Result reportConditionError(EsonValue condition) {
            EsonNotConditionException e
                    = new EsonNotConditionException(condition);
            EsonRuntimeException re = new EsonRuntimeException();
            re.initCause(e);
            throw re;
        }

        @Override
        public Result whenLambda(EsonLambda lambda) {
            return reportConditionError(lambda);
        }

        @Override
        public Result whenApplication(EsonApplication application) {
            return reportConditionError(application);
        }

    }

    private EsonValue condition;

    private Match(EsonValue condition) {
        super();
        this.condition = condition;
    }

    private static Match withEnsured(EsonValue condition) {
        return new Match(condition);
    }

    public static Match with(EsonValue condition)
            throws EsonNotConditionException {
        if (!condition.on(IsCondition.getInstance())) {
            throw new EsonNotConditionException(condition);
        }
        return withEnsured(condition);
    }

    @Override
    public Result whenSymbol(EsonSymbol valSymbol) {
        return condition.on(new ConditionMatcher(valSymbol) {

            @Override
            public Result whenSymbol(EsonSymbol condSymbol) {
                return condSymbol.getContent().equals(valSymbol.getContent())
                        ? Result.matched()
                        : Result.unmatched();
            }

        });
    }

    @Override
    public Result whenString(EsonString valString) {
        return condition.on(new ConditionMatcher(valString) {

            @Override
            public Result whenString(EsonString condString) {
                return condString.getContent().equals(valString.getContent())
                        ? Result.matched()
                        : Result.unmatched();
            }

        });
    }

    @Override
    public Result whenNumber(EsonNumber valNumber) {
        return condition.on(new ConditionMatcher(valNumber) {

            @Override
            public Result whenNumber(EsonNumber condNumber) {
                return condNumber.getValue().equals(valNumber.getValue())
                        ? Result.matched()
                        : Result.unmatched();
            }

        });
    }

    @Override
    public Result whenBoolean(boolean valValue) {
        return condition
                .on(new ConditionMatcher(EsonSpecialValue.from(valValue)) {

                    @Override
                    public Result whenBoolean(boolean condValue) {
                        return valValue == condValue ? Result.matched()
                                : Result.unmatched();
                    }

                });
    }

    @Override
    public Result whenNull() {
        return condition.on(new ConditionMatcher(EsonSpecialValue.NULL) {

            @Override
            public Result whenNull() {
                return Result.matched();
            }

        });
    }

    @Override
    public Result whenID(EsonID id) {
        return reportValueError();
    }

    @Override
    public Result whenObject(EsonObject valObject) {
        return condition.on(new ConditionMatcher(valObject) {

            @Override
            public Result whenObject(EsonObject condObject) {

                SymbolTable symbolTable = SymbolTable.newEmpty();

                boolean needCollectRest = condObject.getMaybeRest().isPresent();

                Set<String> unusedFields
                        = needCollectRest
                                ? valObject.getContent().stream()
                                        .map(entry -> entry.getKey().getName())
                                        .collect(Collectors.toSet())
                                : null;

                for (Entry condEntry : condObject.getContent()) {
                    Optional<Entry> maybeValEntry = valObject.getContent()
                            .stream()
                            .filter(entry -> entry.getKey().getName()
                                    .equals(condEntry.getKey().getName()))
                            .findFirst();
                    if (maybeValEntry.isEmpty()) {
                        return Result.unmatched();
                    }
                    Result result = maybeValEntry.get().getValue()
                            .on(Match.withEnsured(condEntry.getValue()));
                    if (!result.isMatched()) {
                        return Result.unmatched();
                    }
                    result.getSymbolTable().forEach((name, entry) -> {
                        if (symbolTable.isDefined(name)) {
                            EsonRedefinitionException e
                                    = new EsonRedefinitionException(
                                            symbolTable.get(name).get().getId(),
                                            entry.getId());
                            EsonRuntimeException re
                                    = new EsonRuntimeException();
                            re.initCause(e);
                            throw re;
                        }
                        symbolTable.put(entry.getId(), entry.getValue());
                    });
                    if (needCollectRest) {
                        unusedFields.remove(condEntry.getKey().getName());
                    }
                }

                if (needCollectRest) {
                    String restName = condObject.getMaybeRest().get().getName();
                    if (symbolTable.isDefined(restName)) {
                        EsonRedefinitionException e
                                = new EsonRedefinitionException(
                                        symbolTable.get(restName).get().getId(),
                                        condObject.getMaybeRest().get());
                        throw EsonRuntimeException.causedBy(e);
                    }
                    List<Entry> restContent = valObject.getContent().stream()
                            .filter(entry -> unusedFields
                                    .contains(entry.getKey().getName()))
                            .toList();
                    symbolTable.put(condObject.getMaybeRest().get(),
                                    EsonObject.from(restContent));
                }

                return Result.matched(symbolTable);
            }

        });
    }

    @Override
    public Result whenArray(EsonArray valArray) {
        return condition.on(new ConditionMatcher(valArray) {

            @Override
            public Result whenArray(EsonArray condArray) {
                boolean needCollectRest = condArray.getMaybeRest().isPresent();
                if ((!needCollectRest && condArray.getContent()
                        .size() != valArray.getContent().size())
                        || (needCollectRest && condArray.getContent()
                                .size() > valArray.getContent().size())) {
                    return Result.unmatched();
                }

                List<EsonValue> restContent
                        = needCollectRest ? new ArrayList<EsonValue>() : null;

                SymbolTable symbolTable = SymbolTable.newEmpty();
                for (int i = 0; i < valArray.getContent().size(); ++i) {
                    if (i < condArray.getContent().size()) {
                        EsonValue condition = condArray.getContent().get(i);
                        EsonValue value = valArray.getContent().get(i);
                        Result result = value.on(Match.withEnsured(condition));
                        if (!result.isMatched()) {
                            return Result.unmatched();
                        }
                        result.getSymbolTable().forEach((name, entry) -> {
                            if (symbolTable.isDefined(name)) {
                                EsonRedefinitionException e
                                        = new EsonRedefinitionException(
                                                symbolTable.get(name).get()
                                                        .getId(),
                                                entry.getId());
                                EsonRuntimeException re
                                        = new EsonRuntimeException();
                                re.initCause(e);
                                throw re;
                            }
                            symbolTable.put(entry.getId(), entry.getValue());
                        });
                    } else {
                        restContent.add(valArray.getContent().get(i));
                    }
                }

                if (needCollectRest) {
                    String restName = condArray.getMaybeRest().get().getName();
                    if (symbolTable.isDefined(restName)) {
                        EsonRedefinitionException e
                                = new EsonRedefinitionException(
                                        symbolTable.get(restName).get().getId(),
                                        condArray.getMaybeRest().get());
                        throw EsonRuntimeException.causedBy(e);
                    }
                    symbolTable.put(condArray.getMaybeRest().get(),
                                    EsonArray.from(restContent));
                }

                return Result.matched(symbolTable);
            }

        });
    }

    @Override
    public Result whenLambda(EsonLambda lambda) {
        return condition.on(new ConditionMatcher(lambda));
    }

    @Override
    public Result whenApplication(EsonApplication application) {
        return reportValueError();
    }

    private Result reportValueError() {
        EsonNotFinalValueException e = new EsonNotFinalValueException();
        EsonRuntimeException re = new EsonRuntimeException();
        re.initCause(e);
        throw re;
    }

}
