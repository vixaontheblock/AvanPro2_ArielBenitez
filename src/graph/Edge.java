package graph;
/**
 *  Representa una conexión entre nodos (arista)
 */
public class Edge {

    private Node destination; // Nodo al que apunta la conexión
    private int weight;      // Peso de la conexión (distancia o tiempo)

    // Constructor
    public Edge(Node destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    // Devuelve el nodo destino
    public Node getDestination() {
        return destination;
    }

    // Devuelve el peso de la conexión
    public int getWeight() {
        return weight;
    }
}
