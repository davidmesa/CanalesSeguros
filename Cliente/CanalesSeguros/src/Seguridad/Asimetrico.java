package Seguridad;

import java.security.*;
import javax.crypto.*;

public class Asimetrico {

	private final static String ALGORITMO="RSA"; 
	
	public static byte[] cifrarLlaveSecreta(PublicKey llavePublica , byte[] llaveSecreta)
	{
		try {
			Cipher cipher = Cipher.getInstance(ALGORITMO);
			cipher.init(Cipher.ENCRYPT_MODE, llavePublica);
			byte [] cipheredSK = cipher.doFinal(llaveSecreta);
			return cipheredSK;
		} catch (Exception e) {
			System.err.println("CifrarSK Exception: " + e.getMessage());
		}
		return null;
	}

}
