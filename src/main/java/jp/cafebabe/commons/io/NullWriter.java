package jp.cafebabe.commons.io;

import java.io.Writer;

/**
 * Writer for writing no data.
 *
 * @author Haruaki TAMADA
 */
public class NullWriter extends Writer{
    public NullWriter(){
    }

    @Override
    public void write(int b){
    }

    @Override
    public void write(char[] d, int off, int len){
    }

    @Override
    public void write(char[] d){
    }

    @Override
    public void flush(){
    }

    @Override
    public void close(){
    }
}
