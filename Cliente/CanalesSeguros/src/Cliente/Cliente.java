/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Cliente;

import java.io.*;
import java.net.*;
import java.security.*;
import Seguridad.*;


/**
 * Clase Cliente
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * Clase encargada de controlar los aspectos generales del cliente.
 * @author davidmesa
 */
public class Cliente {

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	public final static String SEPARADOR = ":";

	public final static String INIT = "INIT";

	public final static String STATUS = "STATUS";

	public final static String OK = "OK";

	public final static String ALGORITMOS = "ALGORITMOS";

	public final static String SIMETRICO = "AES";

	public final static String ASIMETRICO = "RSA";

	public final static String HMAC = "HMACMD5";
	
	public final static String CERTSRV = "CERTSRV";
	
	public final static String AUTHENTICATION = "AUT";
	
	public final static String SEPARADOR_LOGIN = ",";
	
	public final static String TUTELA = "STATTUTELA";
	
	public final static String INFO = "INFO";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * 
	 */
	private String servidor;

	/**
	 * 
	 */
	private int puerto;

	/**
	 * 
	 */
	private Socket canal;


	/**
	 * 
	 */
	private PrintWriter out;
	
	/**
	 * 
	 */
	private OutputStream outStream;

	/**
	 * 
	 */
	private BufferedReader in;
	
	/**
	 * 
	 */
	private InputStream inStream;
	
	/**
	 * 
	 */
	private PublicKey llavePublicaServidor;
	
	/**
	 * 
	 */
	private Simetrico simetrico;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * 
	 */
	public Cliente ()
	{
		servidor = "infracomp.virtual.uniandes.edu.co";
		//Puerto sin seguridad
		//puerto = 443;
		//Puerto con seguridad
		puerto = 80;
		simetrico = new Simetrico();
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * 
	 */
	public void correr(String logIn, String clave, String numeroTutela)
	{
		if( !conectar() ) System.exit(1);

		//Etapa 1
		if( !inicio() )
		{
			System.out.println("Termina en Inicio");
			System.exit(1);
		}

		if( !algoritmos() )
		{
			System.out.println("Termina en Algoritmos");
			System.exit(1);
		}
		
		//Etapa 2
		byte[] certEntryBytes = certificado();
		if(certEntryBytes == null)
		{
			System.out.println("Termina en Certificado");
			System.exit(1);
		}
		else
		{
			llavePublicaServidor = Certificado.obtenerLlavePublica(certEntryBytes);
			if(llavePublicaServidor==null)
			{
				System.out.println("Termina en Llave Publica");
				System.exit(1);
			}
		}
		
		//Etapa 3
		
		byte[] llaveSecreta = simetrico.darLlaveSecreta();
		if(llaveSecreta == null)
		{
			System.out.println("Termina en encode Llave Secreta");
			System.exit(1);
		}
		
		if(!enviarLlaveSimetrica(llaveSecreta))
		{
			System.out.println("Termina en enviar Llave Secreta");
			System.exit(1);
		}
		
		autenticacion(logIn, clave, llaveSecreta);
//		
//		
//		//Etapa4
//		String respuesta = tutela(numeroTutela);
//		if(respuesta==null)
//		{
//			System.out.println("Termina en tutela");
//			System.exit(1);
//		}
//		else
//		{
//			System.out.println(respuesta);
//		}
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean conectar()
	{
		try {
			canal = new Socket(servidor, puerto);
			outStream = canal.getOutputStream();
			out = new PrintWriter(outStream, true);
			inStream = canal.getInputStream();
			in = new BufferedReader(new InputStreamReader(inStream));
			return true;
		} catch (Exception e) {
			System.err.println("Conectar Exception: " + e.getMessage()); 
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean inicio()
	{
		try {
			out.println(INIT);
			String respuesta = in.readLine();
			String[] partesRespuesta = respuesta.split(SEPARADOR);
			if(partesRespuesta[0].equals(STATUS) && partesRespuesta[1].equals(OK)) return true;
		} catch (Exception e) {
			System.err.println("Inicio Exception: " + e.getMessage()); 
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean algoritmos()
	{
		try{
			out.println(ALGORITMOS+SEPARADOR+SIMETRICO+SEPARADOR+ASIMETRICO+SEPARADOR+HMAC);
			String respuesta = in.readLine();
			String[]partesRespuesta = respuesta.split(SEPARADOR);
			if(partesRespuesta[0].equals(STATUS) && partesRespuesta[1].equals(OK)) return true;
		}catch (Exception e)
		{
			System.err.println("Algoritmos Exception: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public byte[] certificado()
	{
		try {
			out.println(CERTSRV);
			byte[] certEntryBytes = new byte[1024];
			inStream.read(certEntryBytes); 
            return certEntryBytes;
		} catch (Exception e) {
			System.err.println("Certificado Exception: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 
	 * @param llaveSecreta
	 * @return
	 */
	public boolean enviarLlaveSimetrica(byte[] llaveSecreta)
	{
		try{
			byte[] llaveEncriptada = Asimetrico.cifrarLlaveSecreta(llavePublicaServidor, llaveSecreta);
			out.println(AUTHENTICATION+SEPARADOR+Transformacion.transformar(llaveEncriptada));
			String respuesta = in.readLine();
			String[] partesRespuesta = respuesta.split(SEPARADOR);
			if(partesRespuesta[0].equals(STATUS) && partesRespuesta[1].equals(OK)) return true;
		}catch(Exception e)
		{
			System.err.println("Enviar Llave Exception: " + e.getMessage());	
		}
		return false;
		
	}
	
	/**
	 * 
	 * @param login
	 * @param clave
	 * @param llaveSecreta
	 * @return
	 */
	public boolean autenticacion(String login, String clave, byte[] llaveSecreta)
	{
		try{
			String mensaje = login+SEPARADOR_LOGIN+clave;
			byte[] loginCifrado = simetrico.cifrar(mensaje);
			byte[] loginDigest = Digest.calcular(mensaje, llaveSecreta);
			out.println(Transformacion.transformar(loginCifrado)+SEPARADOR+Transformacion.transformar(loginDigest));
			String respuesta = in.readLine();
			String[] partesRespuesta = respuesta.split(SEPARADOR);
			if(partesRespuesta[0].equals(STATUS) && partesRespuesta[1].equals(OK)) return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Autenticacion Exception: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public String tutela(String id){
		try {
			out.println(TUTELA+SEPARADOR+id);
			String respuesta = in.readLine();
			System.out.println(respuesta);
			String [] res = respuesta.split(SEPARADOR);
			if(res[0].equals(INFO))
			{
				return res[1];
			}
		}
		catch (Exception e)
		{
			System.err.println("Tutela Exception " + e.getMessage());
		}
		return null;
	}


	// -----------------------------------------------------------------
	// Main
	// -----------------------------------------------------------------

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		String logIn = null;
		String clave = null;
		String numeroTutela = null;
		
		//Informacion necesaria por pedido
		BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in)); 
		try {
			System.out.println("Login: ");
			logIn = stdIn.readLine();
			System.out.println("Clave: ");
			clave = stdIn.readLine();
			System.out.println("Numero de tutela: ");
			numeroTutela = stdIn.readLine(); 
		} catch (IOException e) {
			System.err.println("Datos Exception: " + e.getMessage());
			System.exit(1);
		}

		//Correr Cliente
		Cliente cliente = new Cliente();
		cliente.correr(logIn, clave, numeroTutela);
	}



}
