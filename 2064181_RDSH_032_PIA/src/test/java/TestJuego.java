import org.example.Dulce;
import org.example.Juego;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestJuego {

    public Juego juego;
    private final int total_dulces = 6;
    private final int medidas_pix = 49;
    //rellena el tablero antes que todos los tests
    @Before
    public void setUp() {
        juego = new Juego(total_dulces, medidas_pix);
        juego.rellenar_tablero();
    }
    //checa si los movimientos iniciales son 10
    @Test
    public void testGetMovimientos() {
        assertEquals(10, juego.getMovimientos());
    }
    //checa si el puntaje inicial si es 0
    @Test
    public void testGetPuntaje() {
        assertEquals(0, juego.getPuntaje());
    }

    @Test
    public void testGetTablero() {
        Dulce[][] tablero = juego.getTablero();
        //verifica que si este relleno el tablero
        assertNotNull(tablero);
        // se verifica que el tablero tenga 10 filas y que la fila 0 tiene 10 cols
        assertEquals(10, tablero.length);
        assertEquals(10, tablero[0].length);
    }

    @Test
    public void testGanador() {
        // Puntuaje mayor al de la meta
        juego.setPuntaje(1800);
        //verifica que si tiene mas de 1500 sea el ganador
        assertTrue("Debio haber ganado", juego.ganador());
    }

    @Test
    public void testPerdedor() {
        // Movimientos 0 como si ya estuvieran agotados y el puntaje no fue mayor o igual a 1500
        juego.setMovimientos(0);
        juego.setPuntaje(1000);
        // verifica que pierda
        assertTrue("Debio haber perdido", juego.perdedor());
    }
}