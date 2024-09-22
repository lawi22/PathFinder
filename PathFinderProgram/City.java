// PROG VT2023, Inlämningsuppgift, del 2
// Grupp 380
// Louis Guerpillon logu5907

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class City extends Circle {
    private String name;

    public City(String name, double x, double y) {
        super(x, y, 10);
        this.name = name;
        setFill(Color.BLUE);
    }

    public String getName(){
        return name;
    }

    public void paintCovered(){
        setFill(Color.RED);
    }

    public void paintUncovered(){
        setFill(Color.BLUE);
    }
    @Override
    public String toString(){
        return name + ";" + getCenterX() + ";" + getCenterY();
    }

}
