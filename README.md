CanalesSeguros
==============

> Universidad de los Andes 201410

## Implementación del Prototipo

En esta parte del proyecto nos concentraremos en el sistema que recibe y responde las consultas de los usuarios sobre las 
tutelas. Usted debe construir la aplicación cliente que los usuarios correrán para enviar consultas al servidor web que maneja 
la información de las tutelas. Como queremos concentraremos en el protocolo de comunicaciones y sus requerimientos de 
seguridad, construiremos una aplicación cliente/servidor simplificada en Java, ignorando el protocolo https, que es el más común para correr una aplicación web. 

El cliente y el servidor seguirán el siguiente procedimiento para comunicarse: 

1. El cliente inicia la comunicación enviando una solicitud de inicio de sesión, a continuación espera un mensaje de  confirmación de inicio del servidor.
2. El cliente envía la lista de algoritmos de cifrado que usará durante la sesión y espera un segundo mensaje del servidor confirmando si soporta los algoritmos seleccionados (si no, el servidor corta la comunicación). 
3. El servidor envía su certificado digital (CD) para autenticarse con el cliente. El CD debe seguir el estándar X509. 
4. El cliente extrae la llave pública del servidor, del certificado digital, y la usa para enviar una llave simétrica (LS) cifrada. 
5. El servidor usa su llave privada para descifrar la llave simétrica que el cliente envía. 
6. Para autenticarse el cliente envía su usuario y clave, cifrados con la llave simétrica que estableció en el paso anterior. 
7. El servidor usa la llave simétrica para descifrar el usuario y la clave y para verificar la integridad del mensaje, a continuación responde, OK o ERROR, de acuerdo con el resultado de la verificación. Para este prototipo use usuarioic/ic201320. 
8. El cliente usa la llave simétrica para cifrar el número de tutela que se quiere consultar. 
9. El servidor envía la información correspondiente cifrada y anexa el código HMAC correspondiente. 
10. El cliente recibe la respuesta y verifica la integridad de la información. 
11. El cliente responde, OK o ERROR, anunciando el resultado de la transacción y la terminación de la comunicación. 

![](https://github.com/davidmesa/CanalesSeguros/blob/master/Cliente/Protocolo.png?raw=true)

## Para Tener en Cuenta

<ul>
  <li> El protocolo de comunicación maneja la siguiente convención:
    <ul>
      <li> Cadenas de Control: “INIT”, “STATUS”, “OK”, “ERROR”, “ALGORITMOS”, “CERTSRV”, “AUT”, “STATTUTELA”, “INFO”, “RESULTADO”</li>
      <li> Separador Principal: “ : ”</li>
    </ul>
  </li>
  <li> A continuación se presentan los algoritmos disponibles en el servidor para manejo de las tareas de cifrado. Es decir, los algoritmos que deben reemplazar las cadenas ALGS, ALGA y ALGH en el protocolo. Para implementar el cliente usted debe seleccionar un algoritmo en cada caso. 
    <ul>
      <li> Simétricos (ALGS):
        <ul>
          <li> DES. Modo ECB, esquema de relleno PKCS5, llave de 64 bits.</li>
          <li> AES. Modo ECB, esquema de relleno PKCS5, llave de 128 bits.</li>
          <li> Blowfish. Cifrado por bloques, llave de 128 bits.</li>
          <li> RC4. Cifrado por flujo, llave de 128 bits.</li>
        </ul>
      </li>
      <li> Asimétricos (ALGA): 
        <ul>
          <li> RSA. Cifrado por bloques, llave de 1024 bits.</li>
        </ul>
      </li>
      <li> HMAC (ALGH): 
        <ul>
          <li> HmacMD5 </li>
          <li> HmacSHA1 </li>
          <li> HmacSHA256 </li>
        </ul>
      </li>
    </ul>
    Las cadenas que identifican cada uno de los algoritmos son: “DES”, “AES”, “Blowfish”, “RC4”, “RSA”, “HMACMD5”, “HMACSHA1”, “HMACSHA256”. 
  </li>
  <li> Utilizaremos la versión 3 del estándar X509 para el CD. La idea es que el cliente puede comprobar la identidad del servidor a partir de un CD (en un caso real este debería ser expedido por una entidad certificadora pero aquí se va a generar localmente). El CD debe seguir el estándar X509, en particular, debe contener la llave pública para usarla en el proceso de comunicación (se recomienda revisar la librería Bouncycastle para la generación del certificado). El cliente se autentica con el servidor por medio de un usuario y una clave </li>
  <li> La comunicación se realiza a través de sockets de acuerdo con el protocolo de comunicación definido. </li>
  <li> Dado que existen problemas en la transmisión de los bytes cifrados, se manejará encapsulamiento con cadenas 
hexadecimales para transmisión de enteros. </li>
</ul> 
