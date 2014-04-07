package Seguridad;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Certificado {
	
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
