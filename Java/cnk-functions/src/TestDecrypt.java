import com.cnk.travelogix.util.CryptoUtil;

public class TestDecrypt {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String decryptedString = CryptoUtil.decrypt(args[0]);
		long endTime = System.currentTimeMillis();
		
		System.out.printf("Decrypted string for <%s> is <%s>. It took %d ms.\n", args[0], decryptedString, (endTime - startTime));
		
		System.exit(0);
	}

}
