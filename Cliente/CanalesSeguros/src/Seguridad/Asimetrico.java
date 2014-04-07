/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Seguridad;

import java.security.*;
import javax.crypto.*;

public class Asimetrico {

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	private final static String ALGORITMO="RSA"; 

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * 
	 * @param llavePublica
	 * @param llaveSecreta
	 * @return
	 */
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
