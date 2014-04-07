package Seguridad;

import javax.crypto.*;

public class Simetrico {

	private SecretKey desKey; 
	private final static String ALGORITMO="AES"; 
	private final static String PADDING="AES/ECB/PKCS5Padding";

	public Simetrico ()
	{
		KeyGenerator keygen;
		try {
			keygen = KeyGenerator.getInstance(ALGORITMO);
			desKey = keygen.generateKey(); 
		} catch (Exception e) {
			System.err.println("Constructor Simetrico Exception: " + e.getMessage());
		} 
	}

	public byte[] darLlaveSecreta()
	{
		return desKey.getEncoded();
	}

	public byte[] cifrar(String mensaje) { 
		byte [] cipheredText; 
		try { 
			Cipher cipher = Cipher.getInstance(PADDING); 
			byte [] clearText = mensaje.getBytes(); 
			cipher.init(Cipher.ENCRYPT_MODE, desKey); 
			cipheredText = cipher.doFinal(clearText); 
			return cipheredText; 
		} 
		catch (Exception e) { 
			System.out.println("Cifrado simetrico Excepcion: " + e.getMessage()); 
			return null; 
		} 
	}

	public String descifrar(byte [] cipheredText) {

		try { 
			Cipher cipher = Cipher.getInstance(PADDING); 
			cipher.init(Cipher.DECRYPT_MODE, desKey); 
			byte [] clearText = cipher.doFinal(cipheredText); 
			String s3 = new String(clearText); 
			return s3; 
		} 
		catch (Exception e) { 
			System.out.println("Desifrado simetrico Excepcion: " + e.getMessage()); 
			return null;
		} 
	} 

}
