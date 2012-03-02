package jp.cafebabe.commons.lang;

import java.net.URL;

/**
 * @author Haruaki TAMADA
 */
public interface DataHandler<T>{
    public T get(String name);

    public boolean isContain(String name);

    public String getName(URL url);
}
