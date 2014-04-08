/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Seguridad;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Certificado {

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------
	
	/**
	 * Método encargado de obtener la llave del arreglo de bytes. Esto es con el certificado del servidor que sigue el formato X509
	 * @param certEntryBytes El certificado enviado por el servidor para verificar la autenticidad del emisor
	 * @return la llave si la logra obtener o nulo en caso contrario
	 */
	public static PublicKey obtenerLlavePublica(byte[] certEntryBytes)
	{
		InputStream in = new ByteArrayInputStream(certEntryBytes); 
		CertificateFactory certFactory;
		X509Certificate cert;
		try {
			certFactory = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) certFactory.generateCertificate(in);
			in.close();
			return cert.getPublicKey();
		} catch (Exception e) {
			System.err.println("Llave Publica Exception: " + e.getMessage());
		}  
		return null;
	}

}
