public class C2decryptEBC0 {

	/* This source assumes a Big Endian machine (most significant byte first), where the "long" is 32 bits: */
	
	/* Logical left rotate macros: */
	public static byte lrot8(int x, int n) {
		return (byte)( (x << n) | ( x >>> (8-n) ) );
	}

	public static int lrot32(int x, int n) {
		return ( (x << n) | ( x >>> (32-n) ) );
	}

	/* The secret constant is available under license from the 4C Entity, LLC. */
	// const BYTE SecretConstant[256] = "ThisIsSecretConstant";
	static byte SecretConstant[] = {
		(byte)0xB6, (byte)0xAA, (byte)0xEB, (byte)0xB3, (byte)0x35, (byte)0x5D, (byte)0xEE, (byte)0xB1,
		(byte)0x72, (byte)0x33, (byte)0x05, (byte)0x13, (byte)0x6D, (byte)0xC7, (byte)0x6C, (byte)0x27, 
		(byte)0x25, (byte)0x54, (byte)0xE9, (byte)0x4C, (byte)0xDE, (byte)0xC3, (byte)0x21, (byte)0x39,
		(byte)0xA9, (byte)0xAB, (byte)0xD6, (byte)0xDF, (byte)0xE8, (byte)0x71, (byte)0x94, (byte)0xAE, 
		(byte)0x16, (byte)0x44, (byte)0x76, (byte)0xCD, (byte)0xB7, (byte)0x78, (byte)0x20, (byte)0xF0,
		(byte)0xC1, (byte)0x9F, (byte)0xCF, (byte)0xAF, (byte)0x0F, (byte)0xCB, (byte)0x59, (byte)0x83, 
		(byte)0x3A, (byte)0x5E, (byte)0xB8, (byte)0xB5, (byte)0xF3, (byte)0x47, (byte)0x80, (byte)0xC2,
		(byte)0xF6, (byte)0x14, (byte)0xE6, (byte)0x69, (byte)0xFC, (byte)0x17, (byte)0xE0, (byte)0xE5, 
		(byte)0x79, (byte)0xF9, (byte)0x12, (byte)0xBF, (byte)0x3C, (byte)0xB4, (byte)0x66, (byte)0xAD,
		(byte)0xF7, (byte)0x65, (byte)0x95, (byte)0xF4, (byte)0x4E, (byte)0x02, (byte)0xA0, (byte)0x07, 
		(byte)0x4D, (byte)0x2F, (byte)0x0D, (byte)0x7E, (byte)0xE4, (byte)0xEF, (byte)0xA1, (byte)0x8C,
		(byte)0x6E, (byte)0xD2, (byte)0xFD, (byte)0x19, (byte)0x1C, (byte)0x82, (byte)0x42, (byte)0xBB, 
		(byte)0x9A, (byte)0x43, (byte)0xC6, (byte)0xE2, (byte)0x1F, (byte)0xF2, (byte)0x75, (byte)0x1A,
		(byte)0x63, (byte)0x45, (byte)0xD1, (byte)0x30, (byte)0x81, (byte)0x7F, (byte)0x8E, (byte)0x62, 
		(byte)0x3B, (byte)0xA4, (byte)0xFB, (byte)0x1E, (byte)0x5F, (byte)0xBC, (byte)0xB0, (byte)0x40,
		(byte)0x8B, (byte)0x74, (byte)0x38, (byte)0x8A, (byte)0xC4, (byte)0x73, (byte)0x9C, (byte)0x09, 
		(byte)0xD4, (byte)0xED, (byte)0xD3, (byte)0x5A, (byte)0x60, (byte)0x48, (byte)0xC5, (byte)0x9E,
		(byte)0x01, (byte)0xCC, (byte)0x34, (byte)0x1B, (byte)0x58, (byte)0x36, (byte)0x23, (byte)0x88, 
		(byte)0x7A, (byte)0x90, (byte)0x9B, (byte)0x8F, (byte)0xBD, (byte)0x3F, (byte)0xB9, (byte)0x57,
		(byte)0xA2, (byte)0x3E, (byte)0x04, (byte)0xB2, (byte)0x49, (byte)0x37, (byte)0x5C, (byte)0x7D, 
		(byte)0x61, (byte)0x4A, (byte)0xA6, (byte)0x67, (byte)0xEC, (byte)0x7C, (byte)0x0E, (byte)0x96,
		(byte)0xDD, (byte)0xE3, (byte)0x2C, (byte)0x56, (byte)0x08, (byte)0x0C, (byte)0x8D, (byte)0x2B, 
		(byte)0x6A, (byte)0xFE, (byte)0xEA, (byte)0xA3, (byte)0xCA, (byte)0x3D, (byte)0x91, (byte)0xE7,
		(byte)0xC9, (byte)0xAC, (byte)0x03, (byte)0xD5, (byte)0x89, (byte)0x86, (byte)0xDC, (byte)0x10,
		(byte)0x55, (byte)0x77, (byte)0xC8, (byte)0xD7, (byte)0x97, (byte)0x24, (byte)0x46, (byte)0x9D,
		(byte)0x0A, (byte)0x1D, (byte)0x22, (byte)0xD9, (byte)0xFF, (byte)0x5B, (byte)0x52, (byte)0xD8, 
		(byte)0x00, (byte)0xFA, (byte)0x53, (byte)0x26, (byte)0x29, (byte)0x2E, (byte)0x2A, (byte)0x11,
		(byte)0xC0, (byte)0x6F, (byte)0x4F, (byte)0x7B, (byte)0x28, (byte)0x99, (byte)0x41, (byte)0x92, 
		(byte)0xDB, (byte)0xF8, (byte)0x50, (byte)0xA8, (byte)0x51, (byte)0xA5, (byte)0x4B, (byte)0x93,
		(byte)0x87, (byte)0xDA, (byte)0x06, (byte)0x85, (byte)0x2D, (byte)0xBA, (byte)0x0B, (byte)0x98, 
		(byte)0x70, (byte)0x6B, (byte)0xBE, (byte)0xF1, (byte)0x18, (byte)0xD0, (byte)0x31, (byte)0x68,
		(byte)0x15, (byte)0x84, (byte)0x64, (byte)0xE1, (byte)0xCE, (byte)0xA7, (byte)0xF5, (byte)0x32, 
	};

	/* The cipher has 10 rounds: */
	static int MaxRound = 10;
	
	 /* F is the Feistel round function: */
	public static int F(int data, int key) {
	    int t;
	    byte v[] = new byte[4];
		byte u;
	    
		/* Key Insersion */
		t = data + key;
	    
		/* Secret Constant */
	    v[3] = (byte)((t>>24)&0xff);
	    v[2] = (byte)((t>>16)&0xff);
	    v[1] = (byte)((t>> 8)&0xff);
	    v[0] = SecretConstant[t&0xff];
	    u = (byte)((v[0]&0xff) ^ 0x65);
	    v[1] ^= lrot8((u&0xff),1);
	    u = (byte)((v[0]&0xff) ^ 0x2b);
	    v[2] ^= lrot8((u&0xff),5);
	    u = (byte)((v[0]&0xff) ^ 0xc9);
	    v[3] ^= lrot8((u&0xff),2);
	    
	    /* Rotate */
	    t = ((v[3]&0xff)<<24)|((v[2]&0xff)<<16)|((v[1]&0xff)<<8)|(v[0]&0xff);
	    t ^= lrot32(t,9) ^ lrot32(t,22);
	    return t;
	}

	/* C2 decryption in ECB (Electronic Code Book) mode: */
	public static void c2_d(int inout[]) {
		int L, R, t;
		int ktmpa, ktmpb, ktmpc, ktmpd;
		int sk[] = new int[MaxRound];
		int round;
    	int i;

		/* Input Conversion */
		L = inout[0];
		R = inout[1];

		/* Round Keys */
		sk[0] = 0x789ac6ee;
		sk[1] = 0x79bc3398;
		sk[2] = 0x48d15d62;
		sk[3] = 0xb3c4da86;
		sk[4] = 0xabcde483;
		sk[5] = 0xc246908f;
		sk[6] = 0x159e285f;
		sk[7] = 0x4d5e7599;
		sk[8] = 0xde1240f6;
		sk[9] = 0x68acfde5;

	    for(round=MaxRound-1;round>=0;round--) {
	    /* Feistel net */
	    	L -= F(R, sk[round]);
	    	t = L; L = R; R = t; // swap
	    }
	    t = L; L = R; R = t; // swap cancel
	    
	    /* Output */
	    inout[0] = L;
	    inout[1] = R;
	    return;
	}


	public static void main(String[] args) {

	    /* デバイス鍵 */
	    //byte devicekey[] = { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9a, (byte)0xbc, (byte)0xde }; // "TestKey"

	    /* ラウンド鍵 */
	    /* int roundkey[] = { 
		0x789ac6ee,
		0x79bc3398,
		0x48d15d62,
		0xb3c4da86,
		0xabcde483,
		0xc246908f,
		0x159e285f,
		0x4d5e7599,
		0xde1240f6,
		0x68acfde5 }; */

	    //int data[] = {0x192837, 0x182736}; // cleartext
	    int data[] = {0x51cf6911, 0xb878b1e5}; // ciphertext

	    System.out.println("Encrypted:" + Integer.toHexString(data[0]) + ", " + Integer.toHexString(data[1]));
	    c2_d(data);
	    System.out.println("Decrypted:" + Integer.toHexString(data[0]) + ", " + Integer.toHexString(data[1]));

	    return;
	}
}
