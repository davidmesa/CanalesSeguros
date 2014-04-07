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
	
	public final static String RESULTADO = "RESULTADO";
	
	public final static String ERROR = "ERROR";
	
	public final static String FIN = "FIN";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Contiene la direccion del servidor.
	 */
	private String servidor;

	/**
	 * Contiene el puerto del servidor.
	 */
	private int puerto;

	/**
	 * Almacena el socket con el cual se comunica con el servidor.
	 */
	private Socket canal;

	/**
	 * Por este medio se escriben strings al servidor.
	 */
	private PrintWriter out;
	
	/**
	 * Por este medio se escriben bytes al servidor
	 */
	private OutputStream outStream;

	/**
	 * Por este medio recibe strings desde el servidor
	 */
	private BufferedReader in;
	
	/**
	 * Por este medio se reciben bytes desde el servidor.
	 */
	private InputStream inStream;
	
	/**
	 * Almacena la llave publica del servidor.
	 */
	private PublicKey llavePublicaServidor;
	
	/**
	 * Clase que encripta simetricamente
	 */
	private Simetrico simetrico;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Constructor de la clase cliente <br>
	 * Inicializa los atributos principales.
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
	 * Metodo encargado de realizar la secuencia necesaria para obtener una respuesta a al estado tutela
	 * @param logIn LogIn que va a realizar la consulta
	 * @param clave Clave del usuario que va a realizar la consulta
	 * @param numeroTutela Numero de tutela a consultar
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
		
		if(!autenticacion(logIn, clave, llaveSecreta))
		{
			System.out.println("Termina en autenticacion");
			System.exit(1);
		}
		
		
		//Etapa4
		
		String respuesta = tutela(numeroTutela, llaveSecreta);
		if(respuesta==null)
		{
			resultado(false);
			System.out.println("Termina en tutela");
			System.exit(1);
		}
		else
		{
			System.out.println(respuesta);
			resultado(true);
		}
		
		close();
	}

	/**
	 * Metodo encargado de conectarse con el servidor.
	 * <b>post: </b> OutStream, Out, InStream, In y canal quedan inicializados.
	 * @return True si es posible conectar con el servidor, false en el caso contrario.
	 */
	private boolean conectar()
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
	 * Envia el mensaje de inicio al servidor, este le contesta con un status.
	 * @return True si la respuesta fue exitosa, false en caso contrario.
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
	 * Metodo encargado de notificarle al servidor que algoritmos van a ser usados.
	 * @return True si los algoritmos son aceptados, false en caso contrario.
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
	 * Metodo encargado de pedir el certificado.
	 * @return Retorna el certificado o null si no fue posible extraerlo.
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
	 * Metodo encargado de enviar la llave secreta al servidor. 
	 * Esta es encriptada con el llave publica del servidor.
	 * @param llaveSecreta Llave secreta en bytes.
	 * @return True si el mensaje fue aceptado o null en caso de lo contrario.
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
	 * Metodo encargado de autenticar al usuario con el servidor.
	 * La clave y el usuario son encriptados usando la llave sercreta.
	 * Se crea un digest que acompaña al mensaje.
	 * @param login Login del usuario.
	 * @param clave Calve del usuario.
	 * @param llaveSecreta Llave secreta acordada con el servidor.
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
	 * Envia la peticion encriptada de la tutela con la llave secreta.
	 * Desencripta el mensaje con la llave secreta
	 * Comprueba que la la respuesta no este alterada por medio de un digest.
	 * @return La respuesta del mensaje, null en caso de no poder obtenerla.
	 */
	public String tutela(String id, byte[] llaveSecreta){
		try {
			
			out.println(TUTELA+SEPARADOR+Transformacion.transformar(simetrico.cifrar(id)));
			String respuesta = in.readLine();
			String [] res = respuesta.split(SEPARADOR);
			if(res[0].equals(INFO))
			{
				System.out.println(respuesta);
				String mensaje = res[1];
				mensaje = simetrico.descifrar(Transformacion.destransformar(mensaje));
				
				byte[] digestExterno = Digest.calcular(mensaje, llaveSecreta);
				if(res[2].equals(Transformacion.transformar(digestExterno))) return mensaje;
			}
		}
		catch (Exception e)
		{
			System.err.println("Tutela Exception " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Finaliza la comunicacion con el servidor, informandole del estado final de la comunicacion
	 * @param estado El estado de la comunicacion.
	 */
	public void resultado( boolean estado )
	{
		if(estado) out.println(RESULTADO+SEPARADOR+OK+SEPARADOR+FIN);
		else out.println(RESULTADO+SEPARADOR+ERROR+SEPARADOR+FIN);
	}
	
	/**
	 * Metodo encargado de cerrar toda la comunicacion con el servidor.
	 */
	private void close() {
		try {
			in.close();
			out.close();
			inStream.close();
			outStream.close();
			canal.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// -----------------------------------------------------------------
	// Main
	// -----------------------------------------------------------------

	/**
	 * Metodo Main encargado de pedir los datos basicos y ejecutar el programa
	 * @param args Argumentos del main, no son utilizados.
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
