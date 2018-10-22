import com.cnk.travelogix.util.CryptoUtil;

public class TestEncryptDecrypt {
	public static void runEncryptDecrypt(String encryptStr) {
		long startTime = System.currentTimeMillis();
		String encryptedString = CryptoUtil.encrypt(encryptStr);
		long midTime = System.currentTimeMillis();
		String decryptedString = CryptoUtil.decrypt(encryptedString);
		long endTime = System.currentTimeMillis();
		
		System.out.printf("Encrypted string for <%s> is <%s>. It took %d ms.\n", encryptStr, encryptedString, (midTime - startTime));
		System.out.printf("Decrypted string for <%s> is <%s>. It took %d ms.\n", encryptStr, decryptedString, (endTime - midTime));
	}
	
	public static void main(String[] args) {
		runEncryptDecrypt(args[0]);
		runEncryptDecrypt(args[0]);
		System.exit(0);
	}

}
