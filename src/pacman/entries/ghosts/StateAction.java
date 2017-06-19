package pacman.entries.ghosts;

import pacman.game.Constants.MOVE;

/**
 * Esta clase representa el movimiento y su score dentro de un estado.
 * Una tabla hash estará compuesta de un estado como clave y una lista de 
 * elementos de esta clase.
 * 
 * De esta forma el estado S guardará todos sus posibles movimientos y tardará en acceder
 * O(1) a la tabla hash y O(4) dentro del array list en el peor de los casos.
 * @author julian
 *
 */
public class StateAction {
	private MOVE move;
	private double value;
	
	public StateAction(MOVE move, double value){
		this.move = move;
		this.value = value;
	}
	
	public double getValue(){return value;}
	public MOVE getMove(){return move;}
	
	public void setValue(double value){this.value = value;}
	
	public String toString(){
		return ( move + ";" + value + ";");
	}
}
