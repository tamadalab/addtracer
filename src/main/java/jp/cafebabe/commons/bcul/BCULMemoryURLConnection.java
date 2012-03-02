package jp.cafebabe.commons.bcul;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.bcel.classfile.JavaClass;

import jp.cafebabe.commons.lang.DataHandler;

/**
 * This class implements URL Connection class.
 *
 * @author Haruaki TAMADA
 */
public class BCULMemoryURLConnection extends URLConnection{
    private DataHandler<JavaClass> handler;

    public BCULMemoryURLConnection(URL url, DataHandler<JavaClass> handler){
        super(url);
        this.handler = handler;
    }

    @Override
    public int getContentLength(){
        String name = handler.getName(getURL());
        if(handler.isContain(name)){
            return handler.get(name).getBytes().length;
        }

        return -1;
    }

    @Override
    public String getContentType(){
        return "application/x-octedstream";
    }

    @Override
    public Object getContent(){
        String name = handler.getName(getURL());
        if(handler.isContain(name)){
            return handler.get(name).getBytes();
        }

        return null;
    }

    @Override
    public InputStream getInputStream(){
        String name = handler.getName(getURL());
        if(handler.isContain(name)){
            return new ByteArrayInputStream(handler.get(name).getBytes());
        }
        return null;
    }

    @Override
    public void connect(){
    }
}
