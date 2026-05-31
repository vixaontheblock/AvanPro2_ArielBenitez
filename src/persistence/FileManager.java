// declarar el paquete donde vive esta clase
package persistence;

// importar las clases necesarias para manejar excepciones de entrada/salida
import java.io.*;
// importar las clases modernas para manejo de rutas y archivos (nio)
import java.nio.file.*;
// importar ArrayList para crear listas dinámicas de strings
import java.util.ArrayList;
// importar la interfaz List para definir el tipo de retorno
import java.util.List;

/**
 * capa base de acceso a archivos txt.
 * sabe leer líneas, escribir líneas nuevas y garantizar
 * que el archivo exista antes de operar.
 * los repositorios (NodeRepository, EdgeRepository) lo usan
 * como su herramienta de i/o.
 */
public class FileManager {

    // atributo privado que guarda la ruta del archivo que este manager va a administrar
    private String rutaArchivo;

    /**
     * constructor: recibe la ruta del archivo con el que trabajará.
     * si el archivo no existe, lo crea vacío en ese momento.
     *
     * @param rutaArchivo ruta relativa o absoluta al .txt, ej. "data/nodes.txt"
     */
    public FileManager(String rutaArchivo) {
        // asignar el parámetro recibido al atributo de instancia para guardarlo
        this.rutaArchivo = rutaArchivo;
        // llamar al método privado para asegurar que el archivo exista en disco
        asegurarArchivo();
    }

    /**
     * verifica que el archivo exista en disco.
     * si no existe, lo crea junto con todos los directorios necesarios.
     * se llama automáticamente en el constructor.
     */
    private void asegurarArchivo() {
        // iniciar bloque try para capturar posibles errores de io
        try {
            // convertir el string de la ruta a un objeto Path para usar la API moderna de NIO
            Path ruta = Paths.get(rutaArchivo);

            // verificar si la ruta tiene un directorio padre (ej. "data/" en "data/archivo.txt")
            if (ruta.getParent() != null) {
                // crear todos los directorios en la ruta padre si no existen ya
                // si ya existen, no hace nada y no lanza error
                Files.createDirectories(ruta.getParent());
            }

            // verificar si el archivo completo aún no existe en el sistema de archivos
            if (!Files.exists(ruta)) {
                // crear el archivo vacío en la ruta especificada
                Files.createFile(ruta);
            }
        } 
        // capturar cualquier excepción de tipo IO que ocurra durante la creación
        catch (IOException e) {
            // imprimir mensaje de error en la consola de errores estándar
            // incluir la ruta del archivo y el mensaje específico de la excepción
            System.err.println("error al crear el archivo: " + rutaArchivo + " → " + e.getMessage());
        }
        // si no hay error, el método termina silenciosamente (archivo listo para usar)
    }

    /**
     * lee todas las líneas del archivo y las devuelve como lista.
     * las líneas vacías y las que empiezan con '#' (comentarios) se ignoran.
     *
     * @return lista con las líneas con contenido del archivo
     */
    public List<String> leerLineas() {
        // crear nueva lista vacía para almacenar las líneas válidas que se lean
        List<String> lineas = new ArrayList<>();

        // iniciar bloque try-with-resources para asegurar que el buffer se cierre automáticamente
        // crear un BufferedReader que envuelve un FileReader apuntando a la ruta del archivo
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            // declarar variable para guardar temporalmente cada línea leída
            String linea;

            // iniciar bucle while que lee línea por línea hasta llegar al final del archivo
            // readLine() devuelve null cuando no hay más líneas
            while ((linea = br.readLine()) != null) {
                // quitar espacios en blanco al inicio y al final de la línea leída
                linea = linea.trim();

                // validar que la línea no esté vacía después del trim
                // validar que la línea no empiece con '#' (que sería un comentario)
                // solo procesar la línea si cumple ambas condiciones
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    // agregar la línea válida a la lista de resultados
                    lineas.add(linea);
                }
                // si la línea es vacía o es comentario, se ignora y se continúa con la siguiente
            }
        } 
        // capturar cualquier excepción de IO que ocurra durante la lectura
        catch (IOException e) {
            // imprimir mensaje de error en consola con la ruta y el detalle del error
            System.err.println("error al leer: " + rutaArchivo + " → " + e.getMessage());
        }

        // retornar la lista con todas las líneas válidas encontradas (puede estar vacía)
        return lineas;
    }

    /**
     * agrega una línea nueva al final del archivo sin borrar nada.
     * usa modo append (true) para no sobreescribir el contenido existente.
     *
     * @param linea texto de la línea a agregar (sin salto de línea al final)
     */
    public void escribirLinea(String linea) {
        // iniciar bloque try-with-resources para gestionar automáticamente el cierre del writer
        // crear un BufferedWriter que envuelve un FileWriter en modo append (true = agregar al final)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            // escribir el contenido de la variable linea en el buffer
            bw.write(linea);
            // agregar un salto de línea después del contenido para que la próxima escritura empiece en nueva línea
            bw.newLine();
        } 
        // capturar cualquier excepción de IO que ocurra durante la escritura
        catch (IOException e) {
            // imprimir mensaje de error en consola con la ruta y el detalle del error
            System.err.println("error al escribir en: " + rutaArchivo + " → " + e.getMessage());
        }
        // al salir del try-with-resources, el buffer se cierra y los datos se flushan al disco automáticamente
    }

    /**
     * sobreescribe todo el archivo con la lista de líneas recibida.
     * útil cuando se necesita actualizar o eliminar registros existentes.
     * cada elemento de la lista se escribe como una línea separada.
     *
     * @param lineas lista de strings; cada uno se convierte en una línea del archivo
     */
    public void sobreescribir(List<String> lineas) {
        // iniciar bloque try-with-resources para gestionar el cierre automático del writer
        // crear un BufferedWriter con FileWriter en modo false = sobreescritura (borra contenido previo)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            // iniciar bucle for-each para recorrer cada string en la lista recibida
            for (String linea : lineas) {
                // escribir el contenido de la línea actual en el buffer
                bw.write(linea);
                // agregar salto de línea para separar esta línea de la siguiente
                bw.newLine();
            }
            // al terminar el bucle, todas las líneas de la lista están escritas en el buffer
        } 
        // capturar cualquier excepción de IO que ocurra durante la sobreescritura
        catch (IOException e) {
            // imprimir mensaje de error en consola con la ruta y el detalle del error
            System.err.println("error al sobreescribir: " + rutaArchivo + " → " + e.getMessage());
        }
        // al salir del try-with-resources, el buffer se cierra y los datos se guardan en disco
    }

    /**
     * devuelve la ruta del archivo que este manager administra.
     * útil para logs y mensajes de error externos.
     *
     * @return ruta en formato string
     */
    public String getRutaArchivo() {
        // retornar el valor del atributo rutaArchivo para que otras clases puedan consultarlo
        return rutaArchivo;
    }
}
