package cn.dzangfan.code.data;

public class EsonString extends EsonValue {

    private String content;

    private EsonString(String content) {
        super();
        this.content = content;
    }

    public static EsonString from(String content) {
        return new EsonString(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public <T> T on(CaseFunction<T> function) {
        return function.whenString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EsonString string && string != null) {
            return content.equals(string.content);
        }
        return false;
    }

}
