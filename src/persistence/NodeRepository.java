// declarar el paquete donde vive esta clase
package persistence;

// importar la clase Node que representa los puntos del grafo (clientes/restaurantes)
import model.Node;
// importar ArrayList para crear listas dinámicas de objetos Node
import java.util.ArrayList;
// importar la interfaz List para definir el tipo de retorno de los métodos
import java.util.List;

/**
 * repositorio de nodos: guarda y lee nodos en nodes.txt.
 * cada línea del archivo representa un nodo con formato:
 *   id,tipo
 * ejemplo:
 *   a,restaurante
 *   b,cliente
 */
public class NodeRepository {

    // constante que guarda la ruta del archivo donde se persisten los nodos
    private static final String RUTA_ARCHIVO = "data/nodes.txt";

    // atributo que guarda la instancia del manejador de archivos para operaciones de i/o
    private FileManager fileManager;

    /**
     * constructor: crea el filemanager apuntando a nodes.txt.
     * el archivo se crea automáticamente si no existe.
     */
    public NodeRepository() {
        // crear nueva instancia de FileManager pasando la ruta definida para nodes.txt
        this.fileManager = new FileManager(RUTA_ARCHIVO);
    }

    /**
     * serializa un nodo a formato csv y lo agrega como línea nueva en nodes.txt.
     * no verifica duplicados; eso es responsabilidad del llamador (DeliverySystem).
     *
     * @param nodo objeto nodo con id y tipo a persistir
     */
    public void guardar(Node nodo) {
        // obtener el id del nodo y empezar a construir el string csv
        // agregar coma como separador entre campos
        // obtener el tipo del nodo y completarlo al string
        String linea = nodo.getId() + "," + nodo.getTipo();
        
        // llamar al filemanager para escribir la línea al final del archivo (modo append)
        fileManager.escribirLinea(linea);
    }

    /**
     * lee todas las líneas de nodes.txt y las convierte en objetos Node.
     * las líneas mal formadas (sin coma, campos vacíos) se ignoran con aviso.
     *
     * @return lista con todos los nodos encontrados en el archivo
     */
    public List<Node> leerTodos() {
        // crear nueva lista vacía para almacenar los objetos Node válidos
        List<Node> nodos = new ArrayList<>();
        
        // llamar al filemanager para obtener todas las líneas válidas del archivo
        List<String> lineas = fileManager.leerLineas();

        // iniciar bucle for-each para recorrer cada línea leída del archivo
        for (String linea : lineas) {
            // llamar al método privado para intentar convertir la línea en un objeto Node
            Node nodo = parsearLinea(linea);

            // verificar si el parseo fue exitoso (nodo no es null)
            if (nodo != null) {
                // agregar el nodo válido a la lista de resultados
                nodos.add(nodo);
            }
            // si nodo es null, se ignora silenciosamente y se continúa con la siguiente línea
        }

        // retornar la lista completa con todos los nodos válidos encontrados
        return nodos;
    }

    /**
     * convierte una línea csv en un objeto Node.
     * formato esperado: "id,tipo" → ej. "a,restaurante"
     * devuelve null si la línea no cumple el formato esperado.
     *
     * @param linea línea de texto leída del archivo
     * @return objeto Node si el parseo fue correcto, null si hubo error
     */
    private Node parsearLinea(String linea) {
        // dividir el string de la línea usando la coma como delimitador
        String[] partes = linea.split(",");

        // validar que el array tenga exactamente 2 elementos (id y tipo)
        if (partes.length != 2) {
            // imprimir mensaje de error en la consola de errores estándar
            System.err.println("línea inválida en nodes.txt (se ignora): " + linea);
            // retornar null para indicar que esta línea no se pudo procesar
            return null;
        }

        // extraer el primer campo (índice 0) y quitar espacios en blanco al inicio y final
        String id = partes[0].trim();
        // extraer el segundo campo (índice 1) y quitar espacios en blanco al inicio y final
        String tipo = partes[1].trim();

        // validar que el campo de id no esté vacío después de trim
        // validar que el campo de tipo no esté vacío después de trim
        if (id.isEmpty() || tipo.isEmpty()) {
            // imprimir mensaje de error indicando campos vacíos
            System.err.println("campos vacíos en nodes.txt (se ignora): " + linea);
            // retornar null porque faltan datos esenciales
            return null;
        }

        // todas las validaciones pasaron: crear nuevo objeto Node con los datos validados
        return new Node(id, tipo);
    }

    /**
     * verifica si un nodo con el id dado ya existe en nodes.txt.
     * recorre el archivo completo y compara ids (insensible a mayúsculas).
     *
     * @param id identificador del nodo a buscar
     * @return true si el nodo ya está guardado, false en caso contrario
     */
    public boolean existe(String id) {
        // obtener todos los nodos guardados llamando al método leerTodos()
        // iniciar bucle for-each para recorrer cada nodo de la lista
        for (Node nodo : leerTodos()) {
            // comparar el id del nodo actual con el parámetro, ignorando mayúsculas/minúsculas
            if (nodo.getId().equalsIgnoreCase(id)) {
                // si coinciden, retornar true inmediatamente porque el nodo ya existe
                return true;
            }
            // si no coinciden, continuar con el siguiente nodo del bucle
        }
        // si el bucle terminó sin encontrar coincidencias, retornar false
        return false;
    }
}
