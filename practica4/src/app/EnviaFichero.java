package app;

import java.io.Serializable;

public class EnviaFichero implements Serializable {

    //Nombre del fichero que se transmite. Por defecto ""
    public String nombreFichero = "";

    //Si este es el ultimo mensaje del fichero en cuestion o hay mas despues
    public boolean ultimoMensaje = true;
 
    //Cuantos bytes son validos en el array de bytes
    public int bytesValidos = 0;

    //Array con bytes leidos del fichero
    public byte[] contenidoFichero = new byte[LONGITUD_MAXIMA];

    //Numero maximo de bytes que se envian en cada mensaje
    public final static int LONGITUD_MAXIMA = 4000;
}
