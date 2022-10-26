package cn.dzangfan.code.eson.exn;

import cn.dzangfan.code.eson.data.EsonID;

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
