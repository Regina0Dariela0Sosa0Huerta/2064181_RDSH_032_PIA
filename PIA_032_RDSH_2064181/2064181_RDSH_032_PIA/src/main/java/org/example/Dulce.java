package org.example;

public class Dulce {
    public int tipo;//0,1,2,3,4,5
    public int fila;
    public int columna;
    public int x;
    public int y;
    public int match;
    public boolean isChocolate;
    public boolean isStripedVertical;
    public boolean isStripedHorizontal;

    //CONSTRUCTOR
    public Dulce() {
        this.tipo = 0;
        this.fila = 0;
        this.columna = 0;
        this.x = 0;
        this.y = 0;
        this.match = 0;
        this.isChocolate = false;
        this.isStripedVertical = false;
        this.isStripedHorizontal = false;
    }
}
