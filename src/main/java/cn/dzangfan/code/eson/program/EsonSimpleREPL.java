package cn.dzangfan.code.eson.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import cn.dzangfan.code.eson.data.CaseFunction;
import cn.dzangfan.code.eson.data.EsonID;
import cn.dzangfan.code.eson.data.EsonObject;
import cn.dzangfan.code.eson.data.EsonObject.Entry;
import cn.dzangfan.code.eson.data.EsonSymbol;
import cn.dzangfan.code.eson.data.EsonValue;
import cn.dzangfan.code.eson.data.function.CheckType;
import cn.dzangfan.code.eson.data.function.Evaluate;
import cn.dzangfan.code.eson.data.function.ExceptionCaseFunction;
import cn.dzangfan.code.eson.data.function.GetType;
import cn.dzangfan.code.eson.data.function.PrettyPrint;
import cn.dzangfan.code.eson.exn.EsonRedefinitionException;
import cn.dzangfan.code.eson.exn.EsonRuntimeException;
import cn.dzangfan.code.eson.exn.EsonSyntaxException;
import cn.dzangfan.code.eson.exn.EsonUndefinedIDException;
import cn.dzangfan.code.eson.lang.EsonScriptLoader;
import cn.dzangfan.code.eson.lang.EsonValueReader;
import cn.dzangfan.code.eson.lib.EsonBaseLib;
import cn.dzangfan.code.eson.lib.EsonLib;
import cn.dzangfan.code.eson.rumtime.Environment;

public class EsonSimpleREPL {

    private List<EsonLib> javaLibs = List.of(new EsonBaseLib());

    public static void main(String[] args) {
        Environment environment = Environment.ROOT.extend();
        EsonSimpleREPL repl = new EsonSimpleREPL(System.in, environment);
        repl.loadJavaLibs();
        repl.loadBaseLib();
        repl.loadScripts(args);
        RunMetaCommand runMetaCommand = repl.defaultRunMetaCommand();
        String message = "Evason Simple REPL\n"
                + "Press character ';' + <enter> to evaluate\n"
                + "Use command line arguments to load a .eson library\n"
                + "  e.g. java -jar evason.jar mylibrary.eson yourlibrary.eson\n"
                + "Several special results are used as meta commands\n"
                + "  1. 'exit: Exit from REPL\n"
                + "  2. 'history: Show evaluated results history\n"
                + "  3. 'variables: Show global variables\n\n";
        System.out.println(message);
        while (true) {
            System.out.print(">>> ");
            repl.readAndEvaluate().ifPresent(value -> {
                if (value.on(runMetaCommand)) {
                    System.out.println(PrettyPrint.the(value));
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

    private EsonScriptLoader scriptLoader = EsonScriptLoader.getInstance();

    private EsonSimpleREPL(InputStream inputStream, Environment environment) {
        super();
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.evaluate = Evaluate.in(environment);
        this.environment = environment;
    }

    private String read() throws IOException {
        StringBuilder sb = new StringBuilder();
        do {
            int nextCodepoint = reader.read();
            if (nextCodepoint == -1) {
                System.exit(1);
            }
            char nextChar = (char) nextCodepoint;
            if (nextChar == ';') {
                return sb.toString();
            } else {
                sb.append(nextChar);
            }
        } while (true);
    }

    private Optional<EsonValue> readAndEvaluate() {
        try {
            String line = read();
            EsonValue value = EsonValueReader.from(line).toEsonValue();
            if (value == null) {
                throw EsonRuntimeException.causedBy(new EsonSyntaxException());
            }
            return Optional.of(value.on(evaluate));
        } catch (EsonRuntimeException e) {
            String message = e.getCause().getMessage();
            message = message == null ? "Oops..." : message;
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

    private RunMetaCommand defaultRunMetaCommand() {
        return new RunMetaCommand(this::storeValue, () -> {
            historyNameList.stream().forEach(name -> {
                try {
                    String prefix = String.format("%s = ", name);
                    EsonValue value
                            = environment.find(EsonID.from(name)).getValue();
                    List<String> lines
                            = value.on(PrettyPrint.from(prefix.length(), 0));
                    System.out.print(prefix);
                    lines.stream().forEach(System.out::println);
                } catch (EsonUndefinedIDException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            System.out.println();
            return 0;
        }, () -> {
            environment.getVariables().forEach(System.out::println);
            return 0;
        });
    }

    private static class RunMetaCommand extends CaseFunction<Boolean> {

        Consumer<EsonValue> storeValue;

        Supplier<Integer> printHistory;

        Supplier<Integer> printVariables;

        private RunMetaCommand(Consumer<EsonValue> storeValue,
                Supplier<Integer> printHistory,
                Supplier<Integer> printVariables) {
            super((value) -> {
                storeValue.accept(value);
                return true;
            });
            this.storeValue = storeValue;
            this.printHistory = printHistory;
            this.printVariables = printVariables;
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
            case "variables":
                printVariables.get();
                break;
            default:
                storeValue.accept(symbol);
                return true;
            }
            return false;
        }

    }

    private void loadScripts(String[] args) {
        for (String path : args) {
            try {
                scriptLoader.load(path, environment)
                        .on(new ExceptionCaseFunction<EsonValue>(
                                GetType.Type.OBJECT) {

                            @Override
                            public EsonValue whenObject(EsonObject object) {
                                for (Entry entry : object.getContent()) {
                                    try {
                                        /**
                                         * [WARN] Have not considered
                                         * consistency. So if a redefinition
                                         * exception were raised in half of
                                         * evaluation, the defined symbols in
                                         * the script will not be erased.
                                         */
                                        environment.define(entry.getKey(),
                                                           entry.getValue());
                                    } catch (EsonRedefinitionException e) {
                                        throw EsonRuntimeException.causedBy(e);
                                    }
                                }
                                return object;
                            }

                        });
            } catch (EsonRuntimeException e) {
                System.out.printf("A error has been raised in script %s\n",
                                  path);
                String message = e.getCause().getMessage();
                message = message == null ? "Oops..." : message;
                System.out.printf("RUNTIME ERROR: %s\n", message);
            } catch (Exception e) {
                System.out.printf("UNKNOWN ERROR: %s\n", e.getMessage());
            }
        }
    }

    private void loadJavaLibs() {
        for (EsonLib lib : javaLibs) {
            try {
                lib.inject(environment);
            } catch (EsonRuntimeException e) {
                System.out
                        .printf("Unable to load library [%s] because of following error\n",
                                lib.name());
                String message = e.getCause().getMessage();
                message = message == null ? "Oops..." : message;
                System.out.printf("RUNTIME ERROR: %s\n", message);
            } catch (Exception e) {
                System.out.printf("UNKNOWN ERROR: %s\n", e.getMessage());
            }
        }
    }

    private void loadBaseLib() {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("base.eson");
            scriptLoader.load(Objects.requireNonNull(inputStream), environment)
                    .on(CheckType.OBJECT).getContent().forEach(entry -> {
                        EsonID id = entry.getKey();
                        EsonValue value = entry.getValue();
                        try {
                            environment.define(id, value);
                        } catch (EsonRedefinitionException e) {
                            throw EsonRuntimeException.causedBy(e);
                        }
                    });
            return;
        } catch (NullPointerException e) {
            System.out.println("UNKNOWN ERROR: Cannot find base.eson");
        } catch (EsonRuntimeException e) {
            String message = e.getCause().getMessage();
            message = message == null ? "Oops..." : message;
            System.out.printf("RUNTIME ERROR: %s\n", message);
        } catch (Exception e) {
            System.out.printf("UNKNOWN ERROR: %s\n", e.getMessage());
        }
        System.out.println("WARNING: failed to load base.eson");
    }
}
