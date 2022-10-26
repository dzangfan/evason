package cn.dzangfan.code.eson.lang;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

import cn.dzangfan.code.eson.exn.EsonRuntimeException;

public class EsonBailErrorStrategy extends DefaultErrorStrategy {

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        throw EsonRuntimeException.causedBy(e);
    }

    @Override
    public void sync(Parser recognizer) throws RecognitionException {

    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        throw EsonRuntimeException
                .causedBy(new InputMismatchException(recognizer));
    }

}
