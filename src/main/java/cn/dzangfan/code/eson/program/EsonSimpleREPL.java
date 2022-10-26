package cn.dzangfan.code.eson.program;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.Evaluate;
import cn.dzangfan.code.eson.data.function.PrettyPrint;
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonUndefinedIDException;
import cn.dzangfan.code.eson.lang.EsonValueReader;
import cn.dzangfan.code.eson.rumtime.Environment;

public class EsonSimpleREPL {

    public static void main(String[] args) {
        Environment environment = Environment.ROOT.extend();
        EsonSimpleREPL repl = new EsonSimpleREPL(System.in, environment);
        RunMetaCommand runMetaCommand
                = new RunMetaCommand(repl::storeValue, () -> {
                    repl.historyNameList.stream().forEach(name -> {
                        try {
                            String prefix = String.format("%s = ", name);
                            EsonValue value = environment
                                    .find(EsonID.from(name)).getValue();
                            List<String> lines = value
                                    .on(PrettyPrint.from(prefix.length(), 0));
                            System.out.print(prefix);
                            lines.stream().forEach(System.out::println);
                        } catch (EsonUndefinedIDException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
                    System.out.println();
                    return 0;
                });
        String message = "Evason Simple REPL\n"
                + "Several special  results are used as meta commands\n"
                + "  1. 'exit: Exit from REPL\n"
                + "  2. 'history: Show evaluated results history\n\n";
        System.out.println(message);
        while (true) {
            System.out.print(">>> ");
            repl.readAndEvaluate().ifPresent(value -> {
                if (value.on(runMetaCommand)) {
                    System.out.printf(PrettyPrint.the(value));
                }
            });
            System.out.println();
        }
    }

    private BufferedReader reader;

    private Evaluate evaluate;

    private Environment environment;

    private List<String> historyNameList = new ArrayList<String>();

    private int resultCounter = 0;

    private EsonSimpleREPL(InputStream inputStream, Environment environment) {
        super();
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.evaluate = Evaluate.in(environment);
        this.environment = environment;
    }

    private Optional<EsonValue> readAndEvaluate() {
        try {
            String line = reader.readLine();
            EsonValue value = EsonValueReader.from(line).toEsonValue();
            return Optional.of(value.on(evaluate));
        } catch (EsonRuntimeException e) {
            String message = e.getCause().getMessage();
            System.out.printf("RUNTIME ERROR: %s\n", message);
        } catch (Exception e) {
            String message = e.getMessage();
            System.out.printf("UNKNOWN ERROR: %s\n", message);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void storeValue(EsonValue value) {
        for (int limit = 10; limit > 0; --limit) {
            String name = String.format("_result%d", resultCounter++);
            try {
                environment.define(EsonID.from(name), value);
            } catch (EsonRedefinitionException e) {
                continue;
            }
            historyNameList.add(name);
            return;
        }
        System.out.println("WARNING: Previous history has been ignored "
                + "because all possible names has been used");
    }

    private static class RunMetaCommand extends CaseFunction<Boolean> {

        Consumer<EsonValue> storeValue;

        Supplier<Integer> printHistory;

        private RunMetaCommand(Consumer<EsonValue> storeValue,
                Supplier<Integer> printHistory) {
            super((value) -> {
                storeValue.accept(value);
                return true;
            });
            this.storeValue = storeValue;
            this.printHistory = printHistory;
        }

        @Override
        public Boolean whenSymbol(EsonSymbol symbol) {
            switch (symbol.getContent()) {
            case "exit":
                System.out.println("Bye.");
                System.exit(0);
                break;
            case "history":
                printHistory.get();
                break;
            default:
                storeValue.accept(symbol);
                return true;
            }
            return false;
        }

    }
}
