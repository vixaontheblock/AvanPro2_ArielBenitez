import java.util.ArrayList;
import java.util.List;

/**
 * Representa el grafo completo del sistema
 * Aquí se almacenan todos los nodos y conexiones
 */
public class Graph {

    private List<Node> nodes; // Lista de todos los nodos del sistema

    //  Constructor
    public Graph() {
        nodes = new ArrayList<>();
    }

    //  Agrega un nodo al grafo
    public void addNode(Node node) {
        // Evita agregar nodos duplicados
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    //  Busca un nodo por su ID
    public Node getNodeById(String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node; // devuelve el nodo si lo encuentra
            }
        }
        return null; // si no existe
    }

    // Conecta dos nodos creando una arista
    public void addEdge(String fromId, String toId, int weight) {
        Node from = getNodeById(fromId); // nodo origen
        Node to = getNodeById(toId);     // nodo destino

        // Verifica que ambos nodos existan
        if (from != null && to != null) {

            // Agrega conexión de origen a destino
            from.addEdge(new Edge(to, weight));

            //  Agrega conexión inversa (grafo no dirigido)
            // Esto significa que se puede ir en ambos sentidos
            to.addEdge(new Edge(from, weight));
        }
    }

    //  Devuelve todos los nodos
    public List<Node> getNodes() {
        return nodes;
    }

    //  Imprime el grafo en consola (útil para pruebas)
    public void printGraph() {
        for (Node node : nodes) {

            System.out.print("Nodo " + node.getId() + " -> ");

            // Recorre todas sus conexiones
            for (Edge edge : node.getEdges()) {

                // Muestra destino y peso
                System.out.print(
                        edge.getDestination().getId() +
                        "(" + edge.getWeight() + ") "
                );
            }

            System.out.println(); // salto de línea
        }
    }
}
