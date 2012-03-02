package jp.cafebabe.commons.io;

import java.io.OutputStream;

/**
 * no output stream.
 *
 * @author Haruaki TAMADA
 */
public class NullOutputStream extends OutputStream{
    public NullOutputStream(){
    }

    @Override
    public void write(int b){
    }

    @Override
    public void write(byte[] d, int off, int len){
    }

    @Override
    public void write(byte[] d){
    }

    @Override
    public void flush(){
    }
}
