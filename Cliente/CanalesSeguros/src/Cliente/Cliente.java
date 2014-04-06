/**
 * Universidad de los Andes
 * Infraestructura Computacional
 * David Mesa y Miguel Caldas
 * 2014 - 10
 */
package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

	public final static String HMAC = "HMACSHA1";
	
	public final static String CERTSRV = "CERTSRV";
	
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
	private BufferedReader in;

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
		puerto = 443;
		//Puerto con seguridad
		//puerto = 80;
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
		if(!certificado())
		{
			System.out.println("Termina en Certificado");
			System.exit(1);
		}
		
		
		//Etapa4
		String respuesta = tutela(numeroTutela);
		if(respuesta==null)
		{
			System.out.println("Termina en tutela");
			System.exit(1);
		}
		else
		{
			System.out.println(respuesta);
		}
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean conectar()
	{
		try {
			canal = new Socket(servidor, puerto);
			out = new PrintWriter(canal.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(canal.getInputStream()));
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
	public boolean certificado()
	{
		try {
			out.println(CERTSRV);
			String respuesta = in.readLine();
			System.out.println(respuesta);
		} catch (Exception e) {
			System.err.println("Algoritmos Exception: " + e.getMessage());
		}
		return true;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String tutela(String id){
		try {
			out.println(TUTELA+SEPARADOR+id);
			String respuesta = in.readLine();
			String [] res = respuesta.split(SEPARADOR);
			if(res[0].equals(INFO))
			{
				return res[1];
			}
		}
		catch (Exception e)
		{
			System.err.println("Tutela Exception +"+e.getMessage());
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
