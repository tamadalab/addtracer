package jp.cafebabe.commons.io;

import java.io.ByteArrayInputStream;

/**
 * @author Haruaki TAMADA
 */
public class CirculateByteArrayInputStream extends ByteArrayInputStream{
    private int separator;
    private int byteArrayOffset;

    public CirculateByteArrayInputStream(byte[] buf){
        this(buf, 0, buf.length);
    }

    public CirculateByteArrayInputStream(byte[] buf, int separator){
        this(buf, 0, buf.length, separator);
    }

    public CirculateByteArrayInputStream(byte[] buf, int offset, int length){
        this(buf, offset, length, -1);
    }

    public CirculateByteArrayInputStream(byte[] buf, int offset, int length, int separator){
        super(buf, offset, length);
        this.byteArrayOffset = offset;

        this.separator = separator;
    }

    @Override
    public int read(){
        int v = super.read();
        if(v == -1){
            pos = byteArrayOffset;
            if(separator != -1){
                return separator;
            }
            else{
                return super.read();
            }
        }
        return v;
    }

    @Override
    public int read(byte[] data, int offset, int length){
        int r = 0;
        int currentOffset = offset;
        int currentRead = 0;
        int currentLength = length;

        do{
            currentRead = super.read(data, currentOffset, currentLength);
            if((r + currentRead) != length){
                if(separator != -1){
                    data[offset + r + currentRead] = (byte)(separator & 0xff);
                    currentRead++;
                }
                pos = byteArrayOffset;
                currentOffset += currentRead;
                currentLength -= currentRead;
            }
            r += currentRead;
        } while(r < length);

        return r;
    }
}
