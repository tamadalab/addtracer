package jp.cafebabe.commons.io;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * PrintStream class for PrintWriter for System.setOut.
 *
 * @author Haruaki TAMADA
 */
public class PrintWriterStream extends PrintStream{
    private PrintWriter out;

    public PrintWriterStream(PrintWriter out){
        super(new NullOutputStream());
        this.out = out;
    }

    @Override
    public void flush(){
        out.flush();
    }

    @Override
    public void close(){
        out.close();
    }

    @Override
    public boolean checkError(){
        return out.checkError();
    }

    @Override
    public void write(int data){
        out.write(data);
    }

    @Override
    public void write(byte[] data, int offset, int len){
        out.write(new String(data, offset, len));
    }

    @Override
    public void print(boolean b){
        out.print(b);
    }

    @Override
    public void print(char c){
        out.print(c);
    }

    @Override
    public void print(int i){
        out.print(i);
    }

    @Override
    public void print(long l){
        out.print(l);
    }

    @Override
    public void print(float f){
        out.print(f);
    }

    @Override
    public void print(double d){
        out.print(d);
    }

    @Override
    public void print(char[] c){
        out.print(c);
    }

    @Override
    public void print(String s){
        out.print(s);
    }

    @Override
    public void print(Object o){
        out.print(o);
    }

    @Override
    public void println(boolean b){
        out.println(b);
    }

    @Override
    public void println(char c){
        out.println(c);
    }

    @Override
    public void println(int i){
        out.println(i);
    }

    @Override
    public void println(long l){
        out.println(l);
    }

    @Override
    public void println(float f){
        out.println(f);
    }

    @Override
    public void println(double d){
        out.println(d);
    }

    @Override
    public void println(char[] c){
        out.println(c);
    }

    @Override
    public void println(String s){
        out.println(s);
    }

    @Override
    public void println(Object o){
        out.println(o);
    }

    @Override
    public void println(){
        out.println();
    }

    @Override
    public PrintStream printf(String format, Object... args){
        out.printf(format, args);

        return this;
    }

    @Override
    public PrintStream printf(Locale locale, String format, Object... args){
        out.printf(locale, format, args);
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args){
        out.format(format, args);
        return this;
    }

    @Override
    public PrintStream format(Locale locale, String format, Object... args){
        out.format(locale, format, args);
        return this;
    }

    @Override
    public PrintStream append(CharSequence seq){
        out.append(seq);
        return this;
    }

    @Override
    public PrintStream append(CharSequence seq, int offset, int length){
        out.append(seq, offset, length);
        return this;
    }

    @Override
    public PrintStream append(char c){
        out.append(c);
        return this;
    }
}
