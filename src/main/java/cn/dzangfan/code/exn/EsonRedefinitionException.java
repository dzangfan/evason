package cn.dzangfan.code.exn;

import cn.dzangfan.code.data.EsonID;

@SuppressWarnings("serial")
public class EsonRedefinitionException extends EsonException {

    private EsonID oldId;
    @SuppressWarnings("unused")
    private EsonID newId;

    public EsonRedefinitionException(EsonID oldId, EsonID newId) {
        super();
        this.oldId = oldId;
        this.newId = newId;
    }

    @Override
    public String getMessage() {
        return String.format("Redefining local variable %s is unsupported",
                             oldId.getName());
    }

}
