import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa un nodo dentro del grafo
 * Puede ser un restaurante, cliente o repartidor
 */
public class Node {

    private String id;        // Identificador único del nodo (ej: A, B, C)
    private String type;      // Tipo de nodo (Restaurante, Cliente, etc.)
    private List<Edge> edges; // Lista de conexiones (aristas)

    //  Constructor
    public Node(String id, String type) {
        this.id = id;
        this.type = type;
        this.edges = new ArrayList<>(); // Inicializa la lista de conexiones
    }

    //  Agrega una conexión a otro nodo
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    // Devuelve el ID del nodo
    public String getId() {
        return id;
    }

    // Devuelve el tipo del nodo
    public String getType() {
        return type;
    }

    // Devuelve la lista de conexiones
    public List<Edge> getEdges() {
        return edges;
    }

    // Permite comparar nodos por ID (evita duplicados)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    // Genera código hash basado en ID (necesario con equals)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

