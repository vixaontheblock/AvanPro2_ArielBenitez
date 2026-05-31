// paquete donde vive esta clase
package persistence;

// importar la clase Edge que representa una arista del grafo
import model.Edge;
// importar ArrayList para crear listas dinámicas de aristas
import java.util.ArrayList;
// importar List para usar la interfaz de lista genérica
import java.util.List;

/**
 * repositorio de aristas: guarda y lee conexiones en edges.txt.
 * cada línea del archivo representa una arista con formato:
 *   origen,destino,peso
 * ejemplo:
 *   a,b,5
 *   b,c,10
 */
public class EdgeRepository {

    // constante que guarda la ruta del archivo donde se persisten las aristas
    private static final String RUTA_ARCHIVO = "data/edges.txt";

    // atributo que guarda la instancia del manejador de archivos
    private FileManager fileManager;

    /**
     * constructor: crea el filemanager apuntando a edges.txt.
     * el archivo se crea automáticamente si no existe.
     */
    public EdgeRepository() {
        // crear nueva instancia de filemanager pasando la ruta del archivo
        this.fileManager = new FileManager(RUTA_ARCHIVO);
    }

    /**
     * serializa una arista a formato csv y la agrega como línea nueva en edges.txt.
     * no verifica duplicados; eso es responsabilidad del llamador (DeliverySystem).
     *
     * @param arista objeto edge con origen, destino y peso a persistir
     */
    public void guardar(Edge arista) {
        // obtener el nodo de origen de la arista y empezar a construir el string
        String linea = arista.getOrigen()
                // agregar coma como separador de campos
                + "," 
                // obtener el nodo de destino y agregarlo al string
                + arista.getDestino()
                // agregar otra coma como separador
                + "," 
                // obtener el peso numérico y convertirlo a string para completar la línea
                + arista.getPeso();

        // llamar al filemanager para escribir la línea al final del archivo (modo append)
        fileManager.escribirLinea(linea);
    }

    /**
     * lee todas las líneas de edges.txt y las convierte en objetos Edge.
     * las líneas mal formadas (campos faltantes, peso no numérico) se ignoran.
     *
     * @return lista con todas las aristas encontradas en el archivo
     */
    public List<Edge> leerTodas() {
        // crear nueva lista vacía para almacenar los objetos edge válidos
        List<Edge> aristas = new ArrayList<>();
        // llamar al filemanager para obtener todas las líneas del archivo como lista de strings
        List<String> lineas = fileManager.leerLineas();

        // iniciar bucle para recorrer cada línea leída del archivo
        for (String linea : lineas) {
            // llamar al método privado para intentar convertir la línea en un objeto edge
            Edge arista = parsearLinea(linea);

            // verificar si el parseo fue exitoso (arista no es null)
            if (arista != null) {
                // agregar la arista válida a la lista de resultados
                aristas.add(arista);
            }
            // si arista es null, se ignora silenciosamente y se continúa con la siguiente línea
        }

        // retornar la lista completa con todas las aristas válidas encontradas
        return aristas;
    }

    /**
     * convierte una línea csv en un objeto Edge.
     * formato esperado: "origen,destino,peso" → ej. "a,b,5"
     * devuelve null si la línea no cumple el formato o el peso no es entero.
     *
     * @param linea línea de texto leída del archivo
     * @return objeto Edge si el parseo fue correcto, null si hubo error
     */
    private Edge parsearLinea(String linea) {
        // dividir el string de la línea usando la coma como delimitador, guardando las partes en un array
        String[] partes = linea.split(",");

        // validar que el array tenga exactamente 3 elementos (origen, destino, peso)
        if (partes.length != 3) {
            // imprimir mensaje de error en la consola de errores estándar
            System.err.println("línea inválida en edges.txt (se ignora): " + linea);
            // retornar null para indicar que esta línea no se pudo procesar
            return null;
        }

        // extraer el primer campo (índice 0) y quitar espacios en blanco al inicio y final
        String origen = partes[0].trim();
        // extraer el segundo campo (índice 1) y quitar espacios en blanco al inicio y final
        String destino = partes[1].trim();
        // extraer el tercer campo (índice 2) y quitar espacios en blanco al inicio y final
        String pesoStr = partes[2].trim();

        // validar que el campo de origen no esté vacío después de trim
        // validar que el campo de destino no esté vacío después de trim
        // validar que el campo de peso no esté vacío después de trim
        if (origen.isEmpty() || destino.isEmpty() || pesoStr.isEmpty()) {
            // imprimir mensaje de error indicando campos vacíos
            System.err.println("campos vacíos en edges.txt (se ignora): " + linea);
            // retornar null porque faltan datos esenciales
            return null;
        }

        // declarar variable entera para almacenar el peso convertido
        int peso;
        // intentar convertir el string de peso a número entero
        try {
            peso = Integer.parseInt(pesoStr);
        } 
        // capturar excepción si el string no representa un número entero válido
        catch (NumberFormatException e) {
            // imprimir mensaje de error indicando que el peso no es numérico
            System.err.println("peso no numérico en edges.txt (se ignora): " + linea);
            // retornar null porque no se puede crear una arista con peso inválido
            return null;
        }

        // validar que el peso convertido no sea negativo (las distancias no pueden ser negativas)
        if (peso < 0) {
            // imprimir mensaje de error indicando peso negativo
            System.err.println("peso negativo en edges.txt (se ignora): " + linea);
            // retornar null porque una arista con peso negativo no tiene sentido en este contexto
            return null;
        }

        // todas las validaciones pasaron: crear nuevo objeto Edge con los datos validados
        return new Edge(origen, destino, peso);
    }

    /**
     * verifica si ya existe una arista entre dos nodos específicos.
     * útil para evitar duplicados antes de llamar a guardar().
     * la comparación es insensible a mayúsculas.
     *
     * @param origen  id del nodo de partida
     * @param destino id del nodo de llegada
     * @return true si la arista ya está guardada, false en caso contrario
     */
    public boolean existe(String origen, String destino) {
        // obtener todas las aristas guardadas llamando al método leerTodas()
        // iniciar bucle for-each para recorrer cada arista de la lista
        for (Edge arista : leerTodas()) {
            // comparar el origen de la arista con el parámetro, ignorando mayúsculas/minúsculas
            // guardar resultado en variable booleana
            boolean mismoOrigen = arista.getOrigen().equalsIgnoreCase(origen);
            // comparar el destino de la arista con el parámetro, ignorando mayúsculas/minúsculas
            // guardar resultado en variable booleana
            boolean mismoDestino = arista.getDestino().equalsIgnoreCase(destino);

            // verificar si ambas comparaciones son verdaderas (mismo origen y mismo destino)
            if (mismoOrigen && mismoDestino) {
                // si coinciden, retornar true inmediatamente porque la arista ya existe
                return true;
            }
            // si no coinciden, continuar con la siguiente arista del bucle
        }
        // si el bucle terminó sin encontrar coincidencias, retornar false
        return false;
    }
}
