package jp.cafebabe.commons.bcul.updater;

import org.apache.bcel.classfile.Method;

/**
 * @author Haruaki TAMADA
 */
public interface MethodCreatable{
    public boolean isContain();

    public Method[] getMethods();
}
