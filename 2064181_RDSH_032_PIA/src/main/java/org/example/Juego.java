package org.example;

import java.util.Random;
import static java.lang.Math.abs;

public class Juego {
    private Dulce[][] tablero;//matriz bidimensional para la cuadricula
    private int total_dulces;
    private int medidas_pix;//medida AxH en pixeles
    private int x0, y0, x, y;//coordenadas en pixeles y en el tablero (o sea las normales de la matriz)
    private int click;
    private boolean isSwap;//el usuario se esta moviendo
    private boolean isMoving;//se esta rellenando el tablero
    private int speedSwap; // animación más rápida
    private int puntaje;
    private boolean bomba_activada;
    private boolean rayado_activado;
    private int movimientos; // Contador de movimientos restantes
    private final int puntaje_ganador = 1500; // Puntaje ganador

    public Juego(int total_dulces, int medidas_pix){
        this.total_dulces = total_dulces;
        this.medidas_pix = medidas_pix;
        this.tablero = new Dulce [10][10];
        this.click = 0;
        this.isSwap = false;
        this.isMoving = false;
        this.speedSwap = 8;
        this.puntaje = 0;
        this.bomba_activada = false;
        this.rayado_activado = false;
        this.movimientos = 10; //cantidad de movimientos al inicio

        for (int i = 0; i < 10; i++) {
            //posicion de la fila 0
            for (int j = 0; j < 10; j++) {
                //posicion de la fila [0,0], luego [0,1], ..., [0,9]
                tablero[i][j] = new Dulce(); //rellena el tablero con "celdas" para los objetos Dulce
                //cuando se acaban las columnas de la primera fila, se llena ahora la segunda fila y asi sucesivamente
            }
        }
    }

    public void rellenar_tablero() {
        Random rand = new Random();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++) {
                do {
                    tablero[i][j].tipo = rand.nextInt(total_dulces);//rellena el tablero con dulces aleatorios
                } while (crea_match(i, j, tablero[i][j].tipo));// mientras el tablero no este por completo relleno por crear matchs iniciales no va dejar de llenarse

                tablero[i][j].fila = i;
                tablero[i][j].columna = j;
                tablero[i][j].x = j * medidas_pix;
                tablero[i][j].y = i * medidas_pix;
                tablero[i][j].isChocolate = false;
                tablero[i][j].isStripedVertical = false;
                tablero[i][j].isStripedHorizontal = false;

                if (rand.nextInt(20) == 0) {//probabilidad de que caiga uno rayado vertical y si no uno rayado horizontal
                    if (rand.nextBoolean()) {//que sean menos frecuentes que los dulces normales
                        tablero[i][j].isStripedVertical = true;
                    } else {
                        tablero[i][j].isStripedHorizontal = true;
                    }
                }
            }
        }
    }

    private boolean crea_match(int fila, int col, int kind) {
        //match horizotal
        if (col >= 3 && tablero[fila][col - 1].tipo == kind && tablero[fila][col - 2].tipo == kind) {
            return true;
        }//match vertical
        if (fila >= 3 && tablero[fila - 1][col].tipo == kind && tablero[fila - 2][col].tipo == kind) {
            return true;
        }
        return false;
    }

    public void procesamiento_clics(int mouseX, int mouseY, int offsetX, int offsetY) {
        // No permite clics si no quedan movimientos y el juego no se ha ganado
        if (movimientos <= 0 && puntaje < puntaje_ganador) {return;}
        //si se esta rellenado el tablero o activando algun efecto de los dulces especiales no permitir clics
        if (isSwap || isMoving || bomba_activada || rayado_activado) {return;}

        int posX = mouseX - offsetX;
        int posY = mouseY - offsetY;
        click++;//si es un clic a penas voy a hacer un movimiento
        if (click == 1) {
            x0 = posX / medidas_pix + 1;
            y0 = posY / medidas_pix + 1;
        } else if (click == 2) {//si es el segundo clic es que ya se realizo el intercambio de dulces
            x = posX / medidas_pix + 1;
            y = posY / medidas_pix + 1;
            //se obtienen los dulces que se intercambiaron
            Dulce dulce1 = tablero[y0][x0];
            Dulce dulce2 = tablero[y][x];

            // si son dos bombas de chocolate se activa su efecto
            if (dulce1.isChocolate || dulce2.isChocolate) {
                efecto_bomba();
                movimientos--; // Decrementa movimientos
                click = 0;
                return;
            }// si uno de los dulces es uno rayado vertical se activa el efecto con true
            if (dulce1.isStripedVertical) {
                efecto_rayado(y0, x0, true);
                movimientos--; // Decrementa movimientos
                click = 0;
                return;
            }//si es uno de los dulces rayado horizontal se activa el efecto con false
            if (dulce1.isStripedHorizontal) {
                efecto_rayado(y0, x0, false);
                movimientos--; // Decrementa movimientos
                click = 0;
                return;
            }//si el dulce 2 es vertical se activa el efecto con true
            if (dulce2.isStripedVertical) {
                efecto_rayado(y, x, true);
                movimientos--; // Decrementa movimientos
                click = 0;
                return;
            }// si el dulce 2 es horizontal se activa el efecto con false
            if (dulce2.isStripedHorizontal) {
                efecto_rayado(y, x, false);
                movimientos--; // Decrementa movimientos
                click = 0;
                return;
            }

            // Si los dos son dulces normales y una esta a ladod e la otra se hace el cambio
            if (abs(x - x0) + abs(y - y0) == 1) {
                swap(dulce1, dulce2);
                isSwap = true;//ya se hizo un movimiento del usuario
                click = 0;//se reinicia el clic para poder hacer mas
            } else {
                click = 1;//si no eran dulces vecinos inmediatos entonces no se reinicia el segundo clic
                x0 = x;//para esperar el segundo clic correcto
                y0 = y;
            }
        }
    }

    private void efecto_bomba() {
        bomba_activada = true;//para activarlo se pone true
        for (int i = 1; i <= 8; i++) {//recorre toda la matriz
            for (int j = 1; j <= 8; j++) {
                tablero[i][j].match = 2; // todos los dulces se van a eliminar entonces
            }
        }
        puntaje += 100;//se aumenta el puntaje
    }

    private void efecto_rayado(int row, int col, boolean isVertical) {
        rayado_activado = true;//para activarlo se pone true
        if (isVertical) {//si es un dulce rayado
            for (int i = 1; i <= 8; i++) {
                tablero[i][col].match = 3; // como es vertical toda la columna de esa posicion fila se elimina
            }
            puntaje += 10 * 8;//se aumenta el puntaje
        } else {
            for (int j = 1; j <= 8; j++) {
                tablero[row][j].match = 3; // como es horizontal toda la fila de esa posicion columna se elimna
            }
            puntaje += 10 * 8;//se aumenta el puntaje
        }//match 3: efecto rayado activado
        tablero[row][col].match = 3;//se elimina el dulce activado
    }

    public void updateGame() {
        //si tiene match 3 tarde o temprano se van a eliminar entonces no se cambia a match 0
        // y los otros dulces que no estan en proceso de  eliminarse tienen match 0 para empezar el juego o actualizarlo
        if (!bomba_activada && !rayado_activado) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    if (tablero[i][j].match != 3) {
                        tablero[i][j].match = 0;
                    }
                }
            }
        }
        // si son dulces normales empieza a detercar matches normales
        if (!bomba_activada && !rayado_activado) {
            detecta_Matches();
        }

        isMoving = false;// el dulce o los dulces estan quietos
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                Dulce dulce = tablero[i][j];
                int dx = dulce.x - dulce.columna * medidas_pix; //distancia que falta para estar quieto el dulce
                int dy = dulce.y - dulce.fila * medidas_pix;

                if (dx != 0 || dy != 0) {//si su distancia aun no es 0 el dulce se esta moviendo
                    isMoving = true;//los dulces o el dulce se esta moviendo
                    dulce.x -= Integer.signum(dx) * Math.min(speedSwap, abs(dx));//disntancia positiva se mueve hacia izq si no derecha
                    dulce.y -= Integer.signum(dy) * Math.min(speedSwap, abs(dy));
                    //speedSwap es la velocidad de pixeles que va moverse el dulce pero puede que sea mayor a los pixeles de movimiento que tiene el duce
                }//entonces si es mayor, se asegura que se mueva solo los pixeles que alcance
            }
        }

        int sumar_este_puntaje = 0;//puntuaje actual del juego
        if (!isMoving) {
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    if (tablero[i][j].match > 0) {
                        sumar_este_puntaje++;
                    }
                }
            }
            if (sumar_este_puntaje > 0) {
                puntaje += sumar_este_puntaje;
            }
        }

        //si el usuario hizo una combinacion y no hay dulces cayendo ni efectos activados
        if (isSwap && !isMoving && !bomba_activada && !rayado_activado) {
            if (sumar_este_puntaje == 0) { //pero no genero ningun puntaje
                swap(tablero[y0][x0], tablero[y][x]); //no se puede hacer la combinacion
            } else {
                // Si hubo un intercambio y se hizo un match se decrementa el movimiento
                movimientos--;
            }
            isSwap = false;//termino la combinacion
        }

        if (!isMoving) {//si no hay dulces cayendo o fuera de su posicion ideal
            for (int j = 1; j <= 8; j++) {//itera por cols
                for (int i = 8; i >= 1; i--) {
                    if (tablero[i][j].match != 0) {//si la tal posicion hay un hueco que rellenar
                        for (int k = i; k > 0; k--) {//se recorrera hacia arriba
                            if (tablero[k][j].match == 0) {//si se encuentra un dulce que no se va a eliminar
                                swap(tablero[k][j], tablero[i][j]);//se pone en el hueco
                                break;
                            }
                        }
                    }
                }
            }
            for (int j = 1; j <= 8; j++) {//itera por columnas
                for (int i = 8, n = 0; i > 0; i--) {//se va ir checando en que fila esta el hueco
                    if (tablero[i][j].match != 0) {//se encuentra el hueco
                        tablero[i][j].tipo = new Random().nextInt(total_dulces);//se rellena random
                        tablero[i][j].y = -medidas_pix * (++n);//efecto lluvia para el relleno de dulces
                        tablero[i][j].match = 0;//dulces inicializados
                        tablero[i][j].isChocolate = false;
                        tablero[i][j].isStripedVertical = false;
                        tablero[i][j].isStripedHorizontal = false;
                    }
                }
            }
            //si los efectos que causaron la eliminacion de dulces ya pasaron vuelven a cambiarse
            if (bomba_activada) {
                bomba_activada = false;
                isSwap = false;
            }
            if (rayado_activado) {
                rayado_activado = false;
                isSwap = false;
            }
        }
    }

    private void detecta_Matches() {
        //detecta matches horizontales
        for (int i = 1; i <= 8; i++) {//recorrera la fila
            int count = 1;//contador de dulces para el match h
            int currentKind = tablero[i][1].tipo; //se guarda el color del primer dulce

            for (int j = 2; j <= 8; j++) {//recorrera la columna siguiente hasta el final
                if (tablero[i][j].tipo == currentKind && !tablero[i][j].isChocolate &&
                        !tablero[i][j].isStripedHorizontal && !tablero[i][j].isStripedVertical) {
                    count++;//si se encuentra un dulce normal (no especial) ya se encontro otro dulce para un match
                } else {//si no es un dulce normal del mismo color o es uno especial
                    if (count >= 3) {//verificar que los dulces anteriores si se tuvo al menos un match de 3
                        markMatch(i, j - count, i, j - 1, count);//se marca el match
                    }
                    count = 1;//vuelve a iniciarse el conteo
                    currentKind = tablero[i][j].tipo;//ahora el color de ese dulce es el que va a buscar hacer un match
                }
            }
            if (count >= 3) {//si se encuentra otro match al final se marca
                markMatch(i, 9 - count, i, 8, count);
            }
        }
        //Detecta match vertical
        for (int j = 1; j <= 8; j++) {//recorre las columnas
            int count = 1;//contador de dulces para el match v
            int currentKind = tablero[1][j].tipo;//el dulce actual es el que va buscar otros dulces como ese

            for (int i = 2; i <= 8; i++) {//recorre las filas
                if (tablero[i][j].tipo == currentKind && !tablero[i][j].isChocolate &&
                        !tablero[i][j].isStripedHorizontal && !tablero[i][j].isStripedVertical) {
                    count++;//si se encuentra un dulce normal (no especial) ya se encontro el dulce de 3 o 4 para un match
                } else {//si ya no hay otro dulce igual
                    if (count >= 3) {//se verifica que haya un match de 3 o 4
                        markMatch(i - count, j, i - 1, j, count); //se marca el match
                    }
                    count = 1;//se reinicia el contador de dulces
                    currentKind = tablero[i][j].tipo;//ahora se va buscar match del dulce en el que se quedo
                }
            }
            if (count >= 3) {//si encuentra otro match al final lo marca
                markMatch(9 - count, j, 8, j, count);
            }
        }
    }

    private void markMatch(int startRow, int startCol, int endRow, int endCol, int matchLength) {
        //recorrera desde el primer dulce hasta el ultimo dulce dentro del match
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                tablero[i][j].match = 1;//se marcan con match 1
            }
        }
        if (matchLength >= 4) {//si se tuvo un match de 4 o mas
            int randomRow = startRow + new Random().nextInt(endRow - startRow + 1);
            int randomCol = startCol + new Random().nextInt(endCol - startCol + 1);
            //se colocara en alguna posicion random de ese match una bomba
            tablero[randomRow][randomCol].isChocolate = true;
            tablero[randomRow][randomCol].match = 0;
            //la bomba no se va eliminar asi que su match se pone 0
            tablero[randomRow][randomCol].tipo = 0;
            tablero[randomRow][randomCol].isStripedVertical = false;
            tablero[randomRow][randomCol].isStripedHorizontal = false;
        }
    }

    private void swap(Dulce dulce1, Dulce dulce2) {
        int rowAux = dulce1.fila;//variable auxiliar para no perder la fila del dulce 1
        dulce1.fila = dulce2.fila;//al dulce 1 se le asigna la fila del dulce 2
        dulce2.fila = rowAux; //al dulce 2 se le asigna la fila 1 guardada en la variable auxiliar

        int colAux = dulce1.columna; //variable aux para no perder la col del dulce 1
        dulce1.columna = dulce2.columna;//al dulce 1 se le asigna la col del dulce 2
        dulce2.columna = colAux;//al dulce 2 se le asigna la col del dulce 1 guardada en la variable auxiliar

        tablero[dulce1.fila][dulce1.columna] = dulce1;
        tablero[dulce2.fila][dulce2.columna] = dulce2;
        //se intercambian las posiciones en el tablero
    }

    public Dulce[][] getTablero() {return tablero;}
    public int getX0() {return x0;}
    public int getY0() {return y0;}
    public int getClick() {return click;}
    public int getPuntaje() {return puntaje;}
    public int getMovimientos() {return movimientos;}
    public void setPuntaje(int puntaje) {this.puntaje = puntaje;}
    public void setMovimientos(int movimientos) {this.movimientos = movimientos;}

    public boolean ganador() {return puntaje >= puntaje_ganador;}
    public boolean perdedor() {return movimientos <= 0 && puntaje < puntaje_ganador;}

}
