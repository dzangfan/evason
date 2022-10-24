package cn.dzangfan.code.rumtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import cn.dzangfan.code.data.EsonID;
import cn.dzangfan.code.data.EsonValue;

public class SymbolTable {

    public static class Entry {
        private EsonID id;
        private EsonValue value;

        private Entry(EsonID id, EsonValue value) {
            super();
            this.id = id;
            this.value = value;
        }

        public static Entry from(EsonID id, EsonValue value) {
            return new Entry(id, value);
        }

        public EsonID getId() {
            return id;
        }

        public void setId(EsonID id) {
            this.id = id;
        }

        public EsonValue getValue() {
            return value;
        }

        public void setValue(EsonValue value) {
            this.value = value;
        }
    }

    private Map<String, Entry> table;

    private SymbolTable(Map<String, Entry> table) {
        super();
        this.table = table;
    }

    public static SymbolTable newEmpty() {
        return new SymbolTable(new HashMap<String, SymbolTable.Entry>());
    }

    public boolean isDefined(String content) {
        return table.containsKey(content);
    }

    public Optional<Entry> get(String content) {
        return Optional.ofNullable(table.get(content));
    }

    public Entry put(EsonID id, EsonValue value) {
        Entry entry = Entry.from(id, value);
        table.put(id.getName(), entry);
        return entry;
    }
}
