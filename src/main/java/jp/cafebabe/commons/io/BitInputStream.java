package jp.cafebabe.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Haruaki TAMADA
 */
public class BitInputStream extends FilterInputStream{
    private int bitBuf = -1;
    private int current = 7;

    public BitInputStream(InputStream in){
        super(in);
    }

    public int readBit() throws IOException{
        if(bitBuf == -1){
            bitBuf = read();
            if(bitBuf == -1){
                return -1;
            }
        }
        int v = (bitBuf >>> current) & 1;
        current--;
        if(current == -1){
            current = 7;
            bitBuf = in.read();
        }
        return v;
    }

    public int read(boolean[] data) throws IOException{
        return read(data, 0, data.length);
    }

    public int read(boolean[] data, int off, int len) throws IOException{
        int read = 0;
        for(int i = 0; i < len; i++){
            int v = readBit();
            if(v == -1){
                return read;
            }
            data[off + i] = (v == 1);
            read++;
        }
        return read;
    }

    public int convert(boolean[] v){
        return convert(v, 0, v.length);
    }

    public int convert(boolean[] data, int off, int len){
        if(data.length > 32){
            throw new IllegalArgumentException("too many data: over the range of int: " + data.length);
        }
        int v = 0;
        for(int i = 0; i < len; i++){
            v = (v << 1);
            if(data[i + off]){
                v = v | 1;
            }
        }
        return v;
    }

    public static void main(String[] args) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(out);
        for(int i = 0; i < args.length; i++){
            dout.writeInt(Integer.parseInt(args[i]));
        }
        dout.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        BitInputStream bin = new BitInputStream(in);

        int d;
        int count = 0;
        while((d = bin.readBit()) != -1){
            System.out.print(d);
            count++;
            if((count % 4) == 0) System.out.print(" ");
        }
        System.out.println();
    }
}
