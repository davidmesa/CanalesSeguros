package Cliente;

/**
 * Clase que contiene metodos static que proveen de metodos de transformacion al protocolo.
 * Infraestructura Computacional 201320
 * Universidad de los Andes.
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * @author Michael Andres Carrillo Pinzon
 */
public class Transformacion {

	/**
	 * Separador de los bytes.
	 */
	public static final String SEPARADOR2 = ";";
	
	/**
	 * Algoritmo de encapsulamiento a enteros. Convierte los bytes de un String a su representacion como enteros.
	 * @param b Los bytes a representar como enteros.
	 * @return EL string construido con la representacion de bytes como enteros.
	 */
	public static String transformar( byte[] b )
	{	
		// Encapsulamiento con hexadecimales
		String ret = "";
		for (int i = 0 ; i < b.length ; i++) {
			String g = Integer.toHexString(((char)b[i])&0x00ff);
			ret += (g.length()==1?"0":"") + g;
		}
		return ret;
	}

	/**
	 * Algoritmo que transforma los enteros en los bytes correspondientes.
	 * @param ss El string con los enteros a transformar.
	 * @return Los bytes en su representacion real.
	 */
	public static byte[] destransformar( String ss)
	{
		// Encapsulamiento con hexadecimales
		byte[] ret = new byte[ss.length()/2];
		for (int i = 0 ; i < ret.length ; i++) {
			ret[i] = (byte) Integer.parseInt(ss.substring(i*2,(i+1)*2), 16);
		}
		return ret;
	}
}