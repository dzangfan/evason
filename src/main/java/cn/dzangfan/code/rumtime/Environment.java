package cn.dzangfan.code.rumtime;

import java.util.Objects;
import java.util.Optional;

import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonValue;
import cn.dzangfan.code.exn.EsonRedefinitionException;
import cn.dzangfan.code.exn.EsonUndefinedIDException;
import cn.dzangfan.code.rumtime.SymbolTable.Entry;

public class Environment {
    private SymbolTable symbolTable;
    private Environment parent;

    private Environment(SymbolTable symbolTable, Environment parent) {
        super();
        this.symbolTable = symbolTable;
        this.parent = parent;
    }

    public static final Environment ROOT
            = new Environment(SymbolTable.newEmpty(), null);

    public Environment getParent() {
        if (isRoot())
            throw new UnsupportedOperationException(
                    "ROOT environment doesn't have parent");
        return parent;
    }

    public void define(EsonID id, EsonValue value)
            throws EsonRedefinitionException {
        Optional<Entry> maybeEntry = symbolTable.get(id.getName());
        if (maybeEntry.isPresent()) {
            throw new EsonRedefinitionException(maybeEntry.get().getId(), id);
        }
        symbolTable.put(id, value);
    }

    public Entry find(EsonID id) throws EsonUndefinedIDException {
        Optional<Entry> maybeEntry = symbolTable.get(id.getName());
        if (maybeEntry.isPresent()) {
            return maybeEntry.get();
        } else if (isRoot()) {
            throw new EsonUndefinedIDException(id);
        } else {
            return parent.find(id);
        }
    }

    public Environment extend(SymbolTable table) {
        return new Environment(table, this);
    }

    public Environment extend() {
        return extend(SymbolTable.newEmpty());
    }

    public boolean isRoot() {
        return ROOT == this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Environment env && obj != null) {
            return symbolTable.equals(env.symbolTable)
                    && Objects.equals(parent, env.parent);
        } else
            return false;
    }

}
