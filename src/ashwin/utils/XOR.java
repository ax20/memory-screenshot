package ashwin.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.io.IOUtils;

public class XOR {

	static File testFile = new File("encrypted.xor");
	static String algorithm = "PBEWithMD5AndDES";
	static String decryptedFileData;
	static String decryptedAppendData;

	public static Cipher resolve(final int method, String key) {

		Random random = new Random(17876699L); // seed for encryption
		byte[] salt = new byte[8]; // salt means 8 bytes
		random.nextBytes(salt); // randomizes with seed
		PBEParameterSpec PBEPSpecs = new PBEParameterSpec(salt, 5); // loops 5

		try {
			SecretKey PBEKey = SecretKeyFactory.getInstance(algorithm).generateSecret(new PBEKeySpec(key.toCharArray()));
			Cipher xor = Cipher.getInstance(algorithm);

			xor.init(method, PBEKey, PBEPSpecs);
			return xor;

		} catch (InvalidKeySpecException e) {
			XOR.err("invalid key spec");

		} catch (NoSuchAlgorithmException e) {
			XOR.err("invalid algorithm provided");

		} catch (NoSuchPaddingException e) {
			XOR.err("invalid padding, file has been modified");

		} catch (InvalidKeyException e) {
			XOR.err("invalid key");

		} catch (InvalidAlgorithmParameterException e) {
			XOR.err("invalid algorithm parameter provided");

		}
		return null;
	}

	public static void readFile(final String key, File readFrom) {
		Cipher xor = XOR.resolve(Cipher.DECRYPT_MODE, key);

		try {
			DataInputStream DIS = new DataInputStream(new CipherInputStream(new FileInputStream(readFrom), xor));
			decryptedFileData = DIS.readUTF();
			DIS.close();

			XOR.info("decrypting file data");

		} catch (FileNotFoundException e) {
			XOR.err("invalid file");

		} catch (IOException e) {
			XOR.err("ioexception.");
			e.printStackTrace();

		}
	}

	public static void writeFile(final String key, String data, File saveTo) {
		Cipher xor = XOR.resolve(Cipher.ENCRYPT_MODE, key);
		String encryptData = data;

		try {
			DataOutputStream DOS = new DataOutputStream(new CipherOutputStream(new FileOutputStream(saveTo), xor));
			DOS.writeUTF(encryptData);
			DOS.flush();
			DOS.close();
			
			XOR.info("written local data to '" + saveTo.toString() + "'");
		} catch (IOException e) {
			XOR.err(e.getStackTrace().toString());

		}
	}
	
	public static void appendXORData(final String key, String data) {
		Cipher xor = XOR.resolve(Cipher.DECRYPT_MODE, key);
		InputStream dataStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
		try {
			CipherInputStream CIS = new CipherInputStream(dataStream, xor);
			decryptedAppendData = XOR.convertStreamApache(dataStream);
			try {
				byte[] t = xor.doFinal(decryptedAppendData.getBytes());
				decryptedAppendData = new String(t);
			} catch (IllegalBlockSizeException e) {
				XOR.err("Illegal Block");
			} catch (BadPaddingException e) {
				XOR.err("incorrect Password"); // means failed password
			}
			CIS.close();
			XOR.info("decrypting append data");
			
		} catch (UnsupportedEncodingException e) {
			XOR.err("Invalid Encoding");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void info(final String info) {
		System.out.println("[XOR] I: " + info);
	}

	public static void err(final String info) {
		System.out.println("[XOR] [!]: " + info);
	}

	public static void debug(final String debug) {
		System.out.println("[XOR] D: " + debug);
	}

	public static String getDecryptedFileData() {
		return decryptedFileData.toString();
	}
	
	public static String getDecryptedAppendData() {
		return decryptedAppendData.toString();
	}
	
	public static String convertStreamApache(InputStream is) {
		String converted = "";
		try {
			converted = IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (IOException e) {
			XOR.err("Error with IOUtils");
			e.printStackTrace();
		}
		return converted;
	}

}
