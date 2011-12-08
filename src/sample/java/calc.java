public class calc{
    public static void main(String[] args){
        int v1 = Integer.parseInt(args[0]);
        int v2 = Integer.parseInt(args[1]);

        calc c = new calc();

        System.out.println("addInt" + c.addInt(v1, v2));
        System.out.println("subInt" + c.subInt(v1, v2));
        System.out.println("mulInt" + c.mulInt(v1, v2));
        System.out.println("divInt" + c.divInt(v1, v2));
        System.out.println("remInt" + c.remInt(v1, v2));
        System.out.println("andInt" + c.andInt(v1, v2));
        System.out.println("orInt" + c.orInt(v1, v2));
        System.out.println("xorInt" + c.xorInt(v1, v2));
        System.out.println("ushrInt" + c.ushrInt(v1, v2));
        System.out.println("shrInt" + c.shrInt(v1, v2));
        System.out.println("shlInt" + c.shlInt(v1, v2));
        System.out.println("negInt" + c.negInt(v1));

        System.out.println("addLong" + c.addLong((long)v1, (long)v2));
        System.out.println("subLong" + c.subLong((long)v1, (long)v2));
        System.out.println("mulLong" + c.mulLong((long)v1, (long)v2));
        System.out.println("divLong" + c.divLong((long)v1, (long)v2));
        System.out.println("remLong" + c.remLong((long)v1, (long)v2));
        System.out.println("andLong" + c.andLong((long)v1, (long)v2));
        System.out.println("orLong" + c.orLong((long)v1, (long)v2));
        System.out.println("xorLong" + c.xorLong((long)v1, (long)v2));
        System.out.println("ushrLong" + c.ushrLong((long)v1, (long)v2));
        System.out.println("shrLong" + c.shrLong((long)v1, (long)v2));
        System.out.println("shlLong" + c.shlLong((long)v1, (long)v2));
        System.out.println("negLong" + c.negLong((long)v1));
    }

    public int addInt(int v1, int v2){
        int r = v1 + v2;
        return r;
    }

    public int subInt(int v1, int v2){
        int r = v1 - v2;
        return r;
    }

    public int mulInt(int v1, int v2){
        int r = v1 * v2;
        return r;
    }

    public int divInt(int v1, int v2){
        int r = v1 / v2;
        return r;
    }

    public int remInt(int v1, int v2){
        int r = v1 % v2;
        return r;
    }

    public int andInt(int v1, int v2){
        int r = v1 & v2;
        return r;
    }

    public int orInt(int v1, int v2){
        int r = v1 | v2;
        return r;
    }

    public int xorInt(int v1, int v2){
        int r = v1 ^ v2;
        return r;
    }

    public int ushrInt(int v1, int v2){
        int r = v1 >>> v2;
        return r;
    }

    public int shrInt(int v1, int v2){
        int r = v1 >> v2;
        return r;
    }

    public int shlInt(int v1, int v2){
        int r = v1 << v2;
        return r;
    }

    public int negInt(int v){
        int r = -v;
        return r;
    }

    public long addLong(long v1, long v2){
        long r = v1 + v2;
        return r;
    }

    public long subLong(long v1, long v2){
        long r = v1 - v2;
        return r;
    }

    public long mulLong(long v1, long v2){
        long r = v1 * v2;
        return r;
    }

    public long divLong(long v1, long v2){
        long r = v1 / v2;
        return r;
    }

    public long remLong(long v1, long v2){
        long r = v1 % v2;
        return r;
    }

    public long andLong(long v1, long v2){
        long r = v1 & v2;
        return r;
    }

    public long orLong(long v1, long v2){
        long r = v1 | v2;
        return r;
    }

    public long xorLong(long v1, long v2){
        long r = v1 ^ v2;
        return r;
    }

    public long ushrLong(long v1, long v2){
        long r = v1 >>> v2;
        return r;
    }

    public long shrLong(long v1, long v2){
        long r = v1 >> v2;
        return r;
    }

    public long shlLong(long v1, long v2){
        long r = v1 << v2;
        return r;
    }

    public long negLong(long v){
        long r = -v;
        return r;
    }
}
