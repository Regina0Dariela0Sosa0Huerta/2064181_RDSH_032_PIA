package org.example;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clics extends MouseAdapter{
    private Juego juego;
    private int centrar_X;
    private int centrar_Y;
    private MouseEvent ultimo_click;

    public Clics(Juego juego, int centrar_X, int centrar_Y){
        this.juego = juego;
        this.centrar_X = centrar_X;
        this.centrar_Y = centrar_Y;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ultimo_click = e; //clics del juego
    }

    public MouseEvent getLatestMouseEvent(){
     MouseEvent temp = ultimo_click;
     ultimo_click = null;
     return temp;
     //resetea los clic para que se puedan hacer nuevos clic al momento de hacer movimientos
    }
}
