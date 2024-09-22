// PROG VT2023, Inlämningsuppgift, del 1
// Grupp 380
// Louis Guerpillon logu5907

import java.util.*;
import java.io.Serializable;
import java.util.List;


public class ListGraph<T> implements Graph<T>, Serializable {

    private final Map<T, Set<Edge<T>>> nodes = new HashMap<>();


    public void add(T node) 
    {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    public void remove(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }


        List<Edge<T>> edgesOfNode = new ArrayList<>(nodes.get(node));


        for (Edge<T> edge : edgesOfNode) {
            disconnect(node, edge.getDestination());
        }


        nodes.get(node).clear();
        nodes.remove(node);
    }
    
    public void connect(T a, T b, String name, int weight) 
    {

        if (weight < 0) 
        {
            throw new IllegalArgumentException("Positive weight only");
        }

        if (!(getEdgeBetween(a, b)==null)) 
        {
            throw new IllegalStateException();
        }

        if(nodes.containsKey(a) && nodes.containsKey(b)) // KOLLA OM NODERNA FINNS
        {
            add(a);
            add(b);
                
            Set<Edge<T>> edgeListA = nodes.get(a);
            Set<Edge<T>> edgeListB = nodes.get(b);
    
            if (edgeListA.contains(b) && edgeListB.contains(a)) //KANSKE OR?
            {
                throw new IllegalStateException("Cannot");
            }

            edgeListA.add(new Edge<T>(b, name, weight));
            edgeListB.add(new Edge<T>(a, name, weight));
        }

        else
        {
            throw new NoSuchElementException("No such elemt");
        }

    }

public void disconnect(T a, T b) {
        if(!nodes.containsKey(a) || !nodes.containsKey(b))
            throw new NoSuchElementException();
        else if(getEdgeBetween(a, b) == null)
            throw new IllegalStateException();
        else{
            nodes.get(a).remove(getEdgeBetween(a, b));
            nodes.get(b).remove(getEdgeBetween(b, a));
        }
    }
    

    public Set<T> getNodes()
    {
        return nodes.keySet();
    }

    public Set<Edge<T>> getEdgesFrom(T node) 
    {
        if (nodes.containsKey(node))
        {
            return nodes.get(node);
        }
        else 
        {
            throw new NoSuchElementException("No such node");
        }
    }

    public Edge<T> getEdgeBetween(T a, T b) 
    {
  
        if (!nodes.containsKey(a) || !nodes.containsKey(b)) 
        {
            throw new NoSuchElementException("No such nodes");
        }

        for(Edge<T> edge : nodes.get(a)) 
        {
            if (edge.getDestination() == b) 
            {
                return edge;
            }
        }
        return null;
    }

    public void setConnectionWeight(T a, T b, int weight) 
    {

    boolean edgeFound = false;

    if (weight < 0){
        throw new IllegalArgumentException("Weight must be more than 0");
    }

    if (!nodes.containsKey(a) || !nodes.containsKey(b)) 
    {
        throw new NoSuchElementException("No such nodes");
    }
    
        for(Edge<T> edge : nodes.get(a)) 
        {
            if (edge.getDestination() == b) 
            {
                edge.setWeight(weight);
                edgeFound = true;

            }
        }
        for(Edge<T> edge : nodes.get(b)) 
        {
            if (edge.getDestination() == a) 
            {
                edge.setWeight(weight);
                edgeFound = true;
                return;
            }
        }
        if (!edgeFound) 
        {
            throw new NoSuchElementException("Finns nt");
        }

        //KVAR ATT GÖRA: om noderna inte har någon edge?
    }

    public String toString() 
    {
        String result = "till ";

        for (T node : getNodes()) 
        {
            result += node.toString();
            result += " has these edges ";

            for (Edge<T> edge : nodes.get(node))
            {
                result += edge.toString();
                result += " ";
            }
        }
        return result;

    }

    public boolean pathExists(T a, T b)
    {
        Set<T> visited = new HashSet<>();

        if (!(nodes.containsKey(a) && nodes.containsKey(b)))   
        {
            return false;
        }  

        dfs(a, b, visited);
        return visited.contains(b);
    }
    
    private void dfs(T current, T searchedFor, Set<T> visited) {
        visited.add(current);
        if (current.equals(searchedFor)) {
            // return;
        }
        for (Edge<T> edge : nodes.get(current)) {
            if (!visited.contains(edge.getDestination())) {
                dfs(edge.getDestination(), searchedFor, visited);
            }
        }
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {


        Set<T> visited = new HashSet<>();
        Stack<Edge<T>> path = new Stack<>();

        //  path.push(from);

        path = depthFirstSearch(from, to, visited, path);
        if (path.isEmpty()) {
            return null;
        }

        return new ArrayList<>(path);
    }


    private Stack<Edge<T>> depthFirstSearch(T current, T searchedFor, Set<T> visited, Stack<Edge<T>> pathSoFar) {
        visited.add(current);

        if (current.equals(searchedFor)) {
            //  pathSoFar.add(current);
            return pathSoFar;
        }
        for (Edge<T> edge : nodes.get(current)) {
            T n = edge.getDestination();
            if (!visited.contains(n)) {
                pathSoFar.push(edge);
                Stack<Edge<T>> p = depthFirstSearch(n, searchedFor, visited, pathSoFar);
                if (!p.isEmpty()) {
                    return p;
                } else {
                    pathSoFar.pop();
                }
            }
        }
        // System.out.println("Dead end in " + current.getName());
        return new Stack<Edge<T>>();
    }
}

