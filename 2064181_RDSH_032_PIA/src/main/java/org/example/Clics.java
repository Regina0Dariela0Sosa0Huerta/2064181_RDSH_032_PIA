package org.example;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clics extends MouseAdapter{
    private MouseEvent latestMouseEvent = null;
    @Override
    public void mousePressed(MouseEvent e) {
        latestMouseEvent = e; //clics del juego
    }

    public MouseEvent getLatestMouseEvent( ){
     MouseEvent temp = latestMouseEvent;
     latestMouseEvent = null;
     return temp;
     //resetea los clic para que se puedan hacer nuevos clic al momento de hacer movimientos
    }
}


