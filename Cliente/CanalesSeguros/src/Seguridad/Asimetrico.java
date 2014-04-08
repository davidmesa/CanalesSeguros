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
	 * Método que cifra con la llave secreta para que se pueda decifrar unicamente con la llave publica
	 * @param llavePublica con la cual se va a realizar la operacion de decifrado
	 * @param llaveSecreta arreglo de bytes con el que se realiza el cifrado de la informacion
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
