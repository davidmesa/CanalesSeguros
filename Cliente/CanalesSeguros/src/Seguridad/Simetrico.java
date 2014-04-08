/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Seguridad;

import javax.crypto.*;

public class Simetrico {

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	private final static String ALGORITMO="AES"; 

	private final static String PADDING="AES/ECB/PKCS5Padding";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Almacena la llave secreta para encriptar y desencriptar
	 */
	private SecretKey desKey;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Constructor de la clase Simetrico.
	 * Inicializa la llave secreta.
	 */
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

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Devuelve la llave secreta decodificada.
	 * @return Llave secreta.
	 */
	public byte[] darLlaveSecreta()
	{
		return desKey.getEncoded();
	}

	/**
	 * Cifra el mensaje de forma simetrica con la llave secreta
	 * @param mensaje El mensaje a cifrar
	 * @return Arreglo de bytes con el mensaje cifrado
	 */
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

	/**
	 * Descifra una cadena de bytes a un mensaje con la llave secreta.
	 * @param cipheredText Cadena de bytes a descifrar.
	 * @return El mensaje descifrado.
	 */
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
