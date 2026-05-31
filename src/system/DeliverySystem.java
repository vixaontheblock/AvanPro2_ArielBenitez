// declarar el paquete donde vive esta clase principal del sistema
package system;

// importar la clase node que representa los puntos (clientes/restaurantes)
import model.Node;
// importar la clase edge que representa las conexiones entre nodos
import model.Edge;
// importar la clase graph que contiene la estructura completa de nodos y aristas
import model.Graph;
// importar el repositorio de nodos para manejar la persistencia en nodes.txt
import persistence.NodeRepository;
// importar el repositorio de aristas para manejar la persistencia en edges.txt
import persistence.EdgeRepository;

/**
 * controlador principal del sistema de delivery.
 * punto de entrada para todas las operaciones del negocio:
 * agregar nodos, crear conexiones y sincronizar con archivos txt.
 */
public class DeliverySystem {

    // atributo privado que guarda la instancia del grafo en memoria RAM
    private Graph graph;

    // atributo privado que guarda la instancia del repositorio de nodos (para leer/escribir nodes.txt)
    private NodeRepository nodeRepo;

    // atributo privado que guarda la instancia del repositorio de aristas (para leer/escribir edges.txt)
    private EdgeRepository edgeRepo;

    /**
     * constructor: inicializa el grafo y los repositorios,
     * luego carga en memoria todo lo guardado en los txt.
     */
    public DeliverySystem() {
        // crear nueva instancia vacía del grafo para empezar a trabajar
        this.graph = new Graph();
        
        // crear nueva instancia del repositorio de nodos (esto crea/verifica nodes.txt)
        this.nodeRepo = new NodeRepository();
        
        // crear nueva instancia del repositorio de aristas (esto crea/verifica edges.txt)
        this.edgeRepo = new EdgeRepository();
        
        // llamar al método privado para cargar los datos existentes desde los archivos al grafo
        cargarDesdeTxt();
    }

    /**
     * lee nodes.txt y edges.txt y reconstruye el grafo en memoria.
     * se llama una sola vez al arrancar el sistema.
     */
    private void cargarDesdeTxt() {
        // llamar al repositorio de nodos para obtener la lista de todos los nodos guardados
        // iniciar bucle for-each para recorrer cada nodo leído
        for (Node nodo : nodeRepo.leerTodos()) {
            // agregar el nodo leído al grafo en memoria para tenerlo disponible
            graph.agregarNodo(nodo);
        }
        
        // llamar al repositorio de aristas para obtener la lista de todas las aristas guardadas
        // iniciar bucle for-each para recorrer cada arista leída
        for (Edge arista : edgeRepo.leerTodas()) {
            // agregar la arista leída al grafo en memoria para conectar los nodos
            graph.agregarArista(arista);
        }
        // al terminar ambos bucles, el grafo en memoria está sincronizado con los archivos
    }

    /**
     * agrega un nuevo nodo (cliente o restaurante) al grafo
     * y lo persiste en nodes.txt.
     *
     * @param id   identificador único del nodo, ej. "A"
     * @param tipo "Restaurante" o "Cliente"
     */
    public void agregarNodo(String id, String tipo) {
        // crear nuevo objeto node usando el id y el tipo recibidos como parámetros
        Node nodo = new Node(id, tipo);
        
        // agregar el nuevo objeto nodo al grafo en memoria para usarlo inmediatamente
        graph.agregarNodo(nodo);
        
        // llamar al repositorio para guardar el nodo en el archivo nodes.txt (persistencia)
        nodeRepo.guardar(nodo);
    }

    /**
     * crea una conexión (arista con peso) entre dos nodos existentes,
     * la agrega al grafo y la persiste en edges.txt.
     *
     * @param origen  id del nodo de partida
     * @param destino id del nodo de llegada
     * @param peso    distancia o costo de la conexión
     */
    public void crearConexion(String origen, String destino, int peso) {
        // crear nuevo objeto edge usando origen, destino y peso recibidos como parámetros
        Edge arista = new Edge(origen, destino, peso);
        
        // agregar la nueva arista al grafo en memoria para actualizar la conectividad
        graph.agregarArista(arista);
        
        // llamar al repositorio para guardar la arista en el archivo edges.txt (persistencia)
        edgeRepo.guardar(arista);
    }

    /**
     * devuelve el grafo completo para que otros módulos
     * (como routemanager) puedan operar sobre él.
     *
     * @return instancia del grafo con todos los nodos y aristas
     */
    public Graph getGraph() {
        // retornar la referencia al atributo graph para que otras clases lo usen
        return graph;
    }

    /**
     * imprime en consola todos los nodos y conexiones actuales.
     * útil para depuración y verificación rápida del estado.
     */
    public void mostrarEstado() {
        // imprimir línea separadora visual en la consola
        System.out.println("=== estado del sistema ===");
        
        // imprimir la lista de nodos actuales obtenida directamente del grafo
        System.out.println("nodos: " + graph.getNodos());
        
        // imprimir la lista de aristas actuales obtenida directamente del grafo
        System.out.println("aristas: " + graph.getAristas());
    }
}
