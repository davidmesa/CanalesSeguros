/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Seguridad;

import javax.crypto.*;
import javax.crypto.spec.*;;

public class Digest {

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	private final static String ALGORITMO="HmacMD5"; 

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------
	
	/**
	 * 
	 * @param buffer por donde se realizara la operacion del digest
	 * @param llaveSecreta con la que se realizara la operacion del cifrado
	 * @return el mensaje final si logra realizarse, nulo de lo contrario
	 */
	private static byte[] getKeyedDigest(byte[] buffer, byte[] llaveSecreta) { 
		try { 
			Mac mac = Mac.getInstance(ALGORITMO);
			SecretKey secret = new SecretKeySpec(llaveSecreta, ALGORITMO);
			mac.init(secret);
			mac.update(buffer);
			return mac.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			return null; 
		} 
	}

	/**
	 * Metodo que obtiene un mensaje de decifrado 
	 * @param mensaje que se desea calcular por medio del decifrado
	 * @param llaveSecreta con la que se desea decifrar
	 * @return
	 */
	public static byte[] calcular(String mensaje, byte[] llaveSecreta) { 
		try { 
			String dato = mensaje; 
			byte[] text = dato.getBytes(); 
			byte [] digest = getKeyedDigest(text, llaveSecreta); 
			return digest; 
		} 
		catch (Exception e) { 
			System.out.println("Excepcion: " + e.getMessage()); 
			return null; 
		} 
	} 


}
