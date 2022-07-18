package app;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class servidor {

    public static String user = System.getProperty("user.name");

    public static void main(String[] args) {
        System.out.println("Iniciando servidor...");
        servidor s = new servidor();
        s.escuchar(5008);
    }

    private void escuchar(int puerto) {
        try {

            while (true) {
                // Se abre el socket servidor
                ServerSocket socketServidor = new ServerSocket(puerto);

                // Se espera un cliente
                Socket cliente = socketServidor.accept();

                // Llega un cliente
                System.out.print("atendiendo / ");

                // Cuando se cierre el socket, esta opción hara que el cierre se
                // retarde automáticamente hasta 10 segundos dando tiempo al cliente
                // a leer los datos.
                cliente.setSoLinger(true, 5);

                // Se lee el mensaje de petición de fichero del cliente
                ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
                Object mensaje = ois.readObject();

                // Si el mensaje es de petición de fichero
                if (mensaje instanceof SolicitaFichero) {
                    // Se muestra en pantalla el fichero pedido y se envia
                    System.out.print("solicita " + ((SolicitaFichero) mensaje).nombreFichero);

                    enviaFichero("C:\\Users\\" + user + "\\Desktop\\Ficheros\\" + ((SolicitaFichero) mensaje).nombreFichero, new ObjectOutputStream(cliente.getOutputStream()));
                    System.out.println(" / terminando...");
                } else {
                    // Si no es el mensaje esperado, se avisa y se sale todo
                    System.err.println("Error en el mensaje: " + mensaje.getClass().getName());
                }

                // Cierre de sockets 
                cliente.close();
                socketServidor.close();
            }

        } catch (Exception e) {
            System.out.println("Servidor: ha ocurrido un error, " + e.getMessage());
        }
    }

    private void enviaFichero(String fichero, ObjectOutputStream oos) {
        try {
            boolean enviadoUltimo = false;

            //Se abre el fichero.
            FileInputStream fis = new FileInputStream(fichero);

            // Se instancia y rellena un mensaje de envio de fichero
            EnviaFichero mensaje = new EnviaFichero();
            mensaje.nombreFichero = fichero;

            // Se leen los primeros bytes del fichero en un campo del mensaje
            int leidos = fis.read(mensaje.contenidoFichero);

            // Bucle mientras se vayan leyendo datos del fichero
            while (leidos > -1) {

                // Se rellena el número de bytes leidos
                mensaje.bytesValidos = leidos;
                
                // Si no se han leido el máximo de bytes, es porque el fichero
                // se ha acabado y este es el último mensaje
                if (leidos < EnviaFichero.LONGITUD_MAXIMA) {
                    mensaje.ultimoMensaje = true;
                    enviadoUltimo = true;
                } else {
                    mensaje.ultimoMensaje = false;
                }

                // Se envía por el socket
                oos.writeObject(mensaje);

                // Si es el último mensaje, salimos del bucle.
                if (mensaje.ultimoMensaje) {
                    break;
                }

                // Se crea un nuevo mensaje
                mensaje = new EnviaFichero();
                mensaje.nombreFichero = fichero;

                // y se leen sus bytes.
                leidos = fis.read(mensaje.contenidoFichero);
            }

            if (enviadoUltimo == false) {
                mensaje.ultimoMensaje = true;
                mensaje.bytesValidos = 0;
                oos.writeObject(mensaje);
            }

            // Se cierra el ObjectOutputStream
            oos.close();
        } catch (Exception e) {
            System.out.print(" " + e.getMessage());
        }
    }
}
