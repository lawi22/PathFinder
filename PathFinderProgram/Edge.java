// PROG VT2023, Inlämningsuppgift, del 1
// Grupp 380
// Louis Guerpillon logu5907


import java.io.Serializable;


public class Edge<T> implements Serializable {

    private final T destination;
    private int weight;
    private String name;


public Edge(T destination, String name, int weight) {

        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

public T getDestination() {

    return this.destination;

}

public int getWeight() {

    return this.weight;
}

public void setWeight(int weight) {

if (weight > 0) {

    this.weight = weight;

}
else 
{
    throw new IllegalArgumentException("Weight must be larger than zero");
}

}

public String getName() {

    return this.name;

}

public String toString() {
    return "till " + destination + " med " + name + " tar " + weight;
}



}