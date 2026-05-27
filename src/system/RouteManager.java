// declarar el paquete donde vive esta clase
package system;

// importar la clase graph que representa la estructura del grafo
import model.Graph;
// importar la clase node que representa un nodo del grafo
import model.Node;

// importar todas las clases utilitarias de java.util (list, map, queue, etc.)
import java.util.*;

/**
 * controla la ejecución de los tres algoritmos de búsqueda de rutas:
 * bfs, dfs y dijkstra. recibe el grafo y devuelve resultados al usuario.
 */
public class RouteManager {

    // atributo privado que guarda la referencia al grafo sobre el que se ejecutan los algoritmos
    private Graph graph;

    /**
     * constructor: recibe el grafo ya cargado desde deliverysystem.
     *
     * @param graph grafo con nodos y aristas en memoria
     */
    public RouteManager(Graph graph) {
        // asignar el parámetro graph recibido al atributo de instancia para guardarlo
        this.graph = graph;
    }

    // ---------------------------------------------------------------
    //  bfs - búsqueda en anchura
    // ---------------------------------------------------------------

    /**
     * ejecuta bfs (breadth-first search) desde un nodo origen.
     * explora nivel por nivel: primero los vecinos directos,
     * luego los vecinos de esos vecinos, etc.
     * útil para encontrar el camino con menos saltos (aristas).
     *
     * @param origenid id del nodo desde donde comienza la búsqueda
     * @return lista de ids en el orden en que fueron visitados
     */
    public List<String> ejecutarBFS(String origenId) {
        // crear nueva lista para almacenar los nodos en el orden exacto de visita
        List<String> visitados = new ArrayList<>();
        
        // crear nuevo set para controlar qué nodos ya fueron vistos y evitar duplicados
        Set<String> vistos = new HashSet<>();
        
        // crear nueva cola fifo (first in first out) para gestionar el orden de exploración del bfs
        Queue<String> cola = new LinkedList<>();

        // agregar el nodo de origen al inicio de la cola para comenzar la búsqueda
        cola.add(origenId);
        // marcar el origen como visto inmediatamente para no volver a procesarlo
        vistos.add(origenId);

        // iniciar bucle while que continúa mientras haya nodos pendientes en la cola
        while (!cola.isEmpty()) {
            // extraer y remover el primer elemento de la cola (el más antiguo)
            String actual = cola.poll();
            // agregar el nodo extraído a la lista de visitados para registrar el orden
            visitados.add(actual);

            // obtener la lista de vecinos del nodo actual desde el grafo
            // iniciar bucle for-each para recorrer cada vecino conectado
            for (String vecino : graph.getVecinos(actual)) {
                // verificar si este vecino aún no ha sido marcado como visto
                if (!vistos.contains(vecino)) {
                    // marcar el vecino como visto para evitar procesarlo nuevamente
                    vistos.add(vecino);
                    // agregar el vecino al final de la cola para explorarlo en el siguiente nivel
                    cola.add(vecino);
                }
                // si el vecino ya fue visto, se ignora y se continúa con el siguiente
            }
        }
        // al terminar el bucle, retornar la lista completa con el orden de visita bfs
        return visitados;
    }

    // ---------------------------------------------------------------
    //  dfs - búsqueda en profundidad
    // ---------------------------------------------------------------

    /**
     * ejecuta dfs (depth-first search) desde un nodo origen.
     * va tan profundo como puede por cada rama antes de retroceder.
     * útil para explorar todos los caminos posibles.
     *
     * @param origenid id del nodo desde donde comienza la búsqueda
     * @return lista de ids en el orden en que fueron visitados
     */
    public List<String> ejecutarDFS(String origenId) {
        // crear nueva lista para almacenar el resultado final del recorrido dfs
        List<String> visitados = new ArrayList<>();
        
        // crear nuevo set para controlar nodos ya procesados y evitar ciclos infinitos
        Set<String> vistos = new HashSet<>();

        // llamar al método helper recursivo pasando el origen y las estructuras de control
        dfsRecursivo(origenId, vistos, visitados);
        
        // retornar la lista completa con el orden de visita dfs
        return visitados;
    }

    /**
     * helper recursivo del dfs: visita el nodo actual y
     * llama recursivamente sobre cada vecino no visitado.
     *
     * @param nodoid    id del nodo que se está visitando ahora
     * @param vistos    conjunto de nodos ya procesados
     * @param resultado lista acumuladora del recorrido
     */
    private void dfsRecursivo(String nodoId, Set<String> vistos, List<String> resultado) {
        // marcar el nodo actual como visto inmediatamente para evitar reentradas
        vistos.add(nodoId);
        // agregar el nodo actual a la lista de resultado para registrar el orden de visita
        resultado.add(nodoId);

        // obtener la lista de vecinos del nodo actual desde el grafo
        // iniciar bucle for-each para recorrer cada vecino conectado
        for (String vecino : graph.getVecinos(nodoId)) {
            // verificar si este vecino aún no ha sido marcado como visto
            if (!vistos.contains(vecino)) {
                // llamar recursivamente al mismo método con el vecino como nuevo nodo actual
                // esto profundiza en la rama antes de explorar otras alternativas
                dfsRecursivo(vecino, vistos, resultado);
            }
            // si el vecino ya fue visto, se ignora y se continúa con el siguiente
        }
        // al terminar el bucle, el método retorna implícitamente (fin de esta rama recursiva)
    }

    // ---------------------------------------------------------------
    //  dijkstra - ruta de menor costo
    // ---------------------------------------------------------------

    /**
     * ejecuta dijkstra para encontrar el camino de menor peso
     * desde un origen hasta un destino específico.
     * funciona con grafos ponderados (aristas con peso/distancia).
     *
     * @param origenid  id del nodo de partida
     * @param destinoid id del nodo al que se quiere llegar
     * @return lista de ids que forman el camino más corto,
     *         lista vacía si no existe ruta
     */
    public List<String> ejecutarDijkstra(String origenId, String destinoId) {
        // crear nuevo mapa para almacenar la distancia mínima conocida desde el origen a cada nodo
        // clave: id del nodo, valor: distancia acumulada mínima (entero)
        Map<String, Integer> distancias = new HashMap<>();

        // crear nuevo mapa para guardar el predecesor de cada nodo en el camino óptimo
        // esto permite reconstruir la ruta completa al final
        // clave: id del nodo, valor: id del nodo desde el que llegamos a él
        Map<String, String> anterior = new HashMap<>();

        // crear nueva cola de prioridad que ordena elementos por su primer valor (distancia)
        // cada elemento es un array de dos enteros: [distancia, hashcode del nodo]
        // la cola siempre entrega primero el elemento con menor distancia
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // obtener la lista de todos los ids de nodos del grafo
        // iniciar bucle for-each para recorrer cada id
        for (String id : graph.getIdsNodos()) {
            // inicializar la distancia de cada nodo a infinito (valor máximo de entero)
            // esto representa que aún no hemos encontrado un camino hacia ese nodo
            distancias.put(id, Integer.MAX_VALUE);
        }

        // establecer la distancia del nodo origen a cero (punto de partida)
        distancias.put(origenId, 0);
        
        // encolar el nodo de origen en la priority queue con distancia 0
        // usamos hashcode como identificador numérico para el array de enteros
        pq.offer(new int[]{0, origenId.hashCode()});
        
        // crear mapa auxiliar para relacionar hashcode de string con el id original
        // esto es necesario porque la priority queue trabaja con enteros, no con strings
        Map<Integer, String> hashAId = new HashMap<>();
        
        // iniciar bucle for-each para recorrer todos los ids de nodos nuevamente
        for (String id : graph.getIdsNodos()) {
            // guardar en el mapa auxiliar: clave=hashcode del id, valor=id original
            hashAId.put(id.hashCode(), id);
        }

        // iniciar bucle while que continúa mientras haya nodos pendientes en la cola de prioridad
        while (!pq.isEmpty()) {
            // extraer y remover el elemento con menor distancia de la cola
            int[] par = pq.poll();
            
            // extraer la distancia acumulada almacenada en la primera posición del array
            int distAct = par[0];
            
            // recuperar el id original del nodo usando su hashcode desde el mapa auxiliar
            String actual = hashAId.get(par[1]);

            // verificar si el nodo actual no es null y si es el nodo destino que buscamos
            if (actual != null && actual.equals(destinoId)) {
                // si ya llegamos al destino, salir del bucle inmediatamente (optimización)
                break;
            }

            // verificar si el nodo es null o si la distancia extraída es mayor a la conocida
            // esto indica que es un registro obsoleto en la cola (ya encontramos un camino mejor)
            if (actual == null || distAct > distancias.get(actual)) {
                // saltar esta iteración y continuar con el siguiente elemento de la cola
                continue;
            }

            // obtener el mapa de vecinos con peso del nodo actual desde el grafo
            // iniciar bucle for-each para recorrer cada entrada (vecino, peso)
            for (Map.Entry<String, Integer> entrada : graph.getVecinosConPeso(actual).entrySet()) {
                // extraer el id del nodo vecino desde la clave de la entrada
                String vecino = entrada.getKey();
                
                // extraer el peso de la arista desde el valor de la entrada
                int peso = entrada.getValue();
                
                // calcular la nueva distancia tentativa: distancia actual + peso de la arista
                int nuevaDist = distAct + peso;

                // verificar si esta nueva distancia es menor a la mejor conocida hasta ahora para este vecino
                if (nuevaDist < distancias.get(vecino)) {
                    // actualizar el mapa de distancias con el nuevo valor mínimo encontrado
                    distancias.put(vecino, nuevaDist);
                    
                    // guardar en el mapa de predecesores que llegamos a este vecino desde el nodo actual
                    anterior.put(vecino, actual);
                    
                    // encolar el vecino en la priority queue con su nueva distancia mínima
                    // esto permite que sea procesado en el orden correcto según su costo acumulado
                    pq.offer(new int[]{nuevaDist, vecino.hashCode()});
                }
                // si la nueva distancia no es mejor, se ignora esta arista y se continúa
            }
        }

        // llamar al método helper para reconstruir el camino desde destino hasta origen
        // usando el mapa de predecesores que fuimos llenando durante el algoritmo
        // retornar el resultado de esa reconstrucción (lista ordenada o vacía)
        return reconstruirCamino(anterior, origenId, destinoId);
    }

    /**
     * recorre el mapa de predecesores desde el destino hasta el origen
     * para armar la lista del camino en orden correcto.
     *
     * @param anterior  mapa nodo → nodo que lo precedió en el camino óptimo
     * @param origen    id del nodo de partida
     * @param destino   id del nodo final
     * @return lista ordenada de ids desde origen hasta destino,
     *         lista vacía si no hay camino
     */
    private List<String> reconstruirCamino(Map<String, String> anterior,
                                           String origen, String destino) {
        // crear nueva linkedlist para poder insertar elementos al inicio eficientemente
        // esto nos permite construir el camino en orden inverso y luego tenerlo correcto
        LinkedList<String> camino = new LinkedList<>();
        
        // iniciar la variable de control con el nodo destino para comenzar el recorrido hacia atrás
        String paso = destino;

        // iniciar bucle while que continúa mientras paso no sea null y no sea el origen
        while (paso != null && !paso.equals(origen)) {
            // insertar el nodo actual al inicio de la lista para invertir el orden de reconstrucción
            camino.addFirst(paso);
            
            // avanzar un paso hacia atrás: obtener el predecesor del nodo actual desde el mapa
            paso = anterior.get(paso);
        }

        // verificar si la variable paso es null (significa que no encontramos el origen)
        if (paso == null) {
            // retornar lista vacía indicando que no existe camino desde origen hasta destino
            return Collections.emptyList();
        }

        // agregar el nodo de origen al inicio de la lista para completar el camino
        camino.addFirst(origen);
        
        // retornar la lista completa con el camino ordenado desde origen hasta destino
        return camino;
    }
}
