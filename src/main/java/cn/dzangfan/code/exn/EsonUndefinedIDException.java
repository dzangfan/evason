package cn.dzangfan.code.exn;

import cn.dzangfan.code.data.EsonID;

@SuppressWarnings("serial")
public class EsonUndefinedIDException extends EsonException {
    private EsonID id;

    public EsonUndefinedIDException(EsonID id) {
        super();
        this.id = id;
    }

    @Override
    public String getMessage() {
        return String.format("Undefined variable %s", id.getName());
    }

}
