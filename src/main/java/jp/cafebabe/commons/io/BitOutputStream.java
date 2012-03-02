package jp.cafebabe.commons.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Enabled bit outputs.
 *
 * @author Haruaki TAMADA
 */
public class BitOutputStream extends FilterOutputStream{
    /**
     * <p>When flush method is called, odd bits are ignored.</p>
     * <p>Examples:</p>
     * <pre>
     *   out.write(new boolean[] {
     *     true,  true,  true, true,  false, false, false, false,
     *     false, false, true, false, 
     *   });
     *   out.flush();
     * </pre> 
     * <p>Then, actually written data is only 0xf0.</p>
     */
    public static final int ABROGATION_MODE = 1;
    /**
     * <p>When flush method is called, also writes odd bits. Default mode.</p>
     * <p>Examples:</p>
     * <pre>
     *   out.write(new boolean[] {
     *     true,  true,  true, true,  false, false, false, false,
     *     false, false, true, false, 
     *   });
     *   out.flush();
     * </pre> 
     * <p>Then, actually written data is 0xf020.</p>
     */
    public static final int AGGRESSIVE_MODE = 0;
    
    private int bitBuf = 0;
    private int putCount = 8;
    private int mode = AGGRESSIVE_MODE;

    public BitOutputStream(OutputStream out){
        super(out);
    }

    public BitOutputStream(OutputStream out, int mode){
        super(out);
        setMode(mode);
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    public int getMode(){
        return mode;
    }

    public void write(boolean[] data, int off, int len) throws IOException{
        for(int i = 0; i < len; i++){
            write(data[i + off]);
        }
    }

    public void write(boolean[] data) throws IOException{
        write(data, 0, data.length);
    }

    /**
     * write 1 bit. If value is true, write bit 1, value is false, write 0 bit.
     */
    public void write(boolean value) throws IOException{
        putCount--;
        if(value){
            bitBuf = bitBuf | (1 << putCount);
        }
        if(putCount == 0){
            putCount = 8;
            super.write(bitBuf);
            bitBuf = 0;
        }
    }

    /**
     * @deprecated instead of {@link wirte(boolean) <code>write(boolean)</code>}
     */
    public void writeBit(int bits) throws IOException{
        write(bits == 1);
    }

    @Override
    public void write(int data) throws IOException{
        if(putCount != 8){
            out.write(bitBuf);
            bitBuf = 0;
            putCount = 8;
        }
        super.write(data);
    }

    @Override
    public void write(byte[] data) throws IOException{
        write(data, 0, data.length);
    }

    @Override
    public void write(byte[] data,int off,int len) throws IOException{
        if(putCount != 8){
            flush();
        }
        super.write(data, off, len);
    }

    @Override
    public void flush() throws IOException{
        if(getMode() == AGGRESSIVE_MODE){
            if(putCount != 8){
                out.write(bitBuf);
            }
        }
        bitBuf=0;
        putCount = 8;
        super.flush();
    }
}
