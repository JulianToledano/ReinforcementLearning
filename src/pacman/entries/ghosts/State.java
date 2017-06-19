package pacman.entries.ghosts;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
/**
 * Esta clase representa el estado del juego en un momento t
 * Es una tupla
 * S = {S0, S1, S2, S3} donde:
 * S0	->	Representa la posición del fantasma.
 * S1	->	Representa el último movimiento realizado por el fantasma.
 * S2	->	Representa la posición de Ms Pacman.
 * S3	-> 	Representa si el fantasma puede ser comido.
 * @author julian
 *
 */
public class State {
	private int ghostPosition;
	private MOVE ghostLastMove;
	private int msPosition;
	private int edible;
	
	public State(){}
	public State(int ghostPosition, MOVE ghostLastMove, int msPosition, int edible){
		this.ghostPosition = ghostPosition;
		this.ghostLastMove = ghostLastMove;
		this.msPosition = msPosition;
		this.edible = edible;
	}
	public State(Game game, GHOST ghost){
		this.ghostPosition = game.getGhostCurrentNodeIndex(ghost);
		this.ghostLastMove = game.getGhostLastMoveMade(ghost);
		this.msPosition = game.getPacmanCurrentNodeIndex();
		if(game.getGhostEdibleTime(ghost) != 0) edible = 1;
		else edible = 0;
	}
	
	/**
	 * Funciones hashCode y equals necesarioas para utilizar la clase como clave dentro
	 * de una hash.
	 * 
	 * El hashcode se basa en la multiplicación de números primos.
	 */
	@Override
	public int hashCode(){
		 int result = 17;
		 result = 31 * result + ghostPosition;
		 if(ghostLastMove == MOVE.UP)
			 result = 31 * result + 23;
		 if(ghostLastMove == MOVE.DOWN)
			 result = 31 * result + 29;
		 if(ghostLastMove == MOVE.LEFT)
			 result = 31 * result + 31;
		 else
			 result = 31 * result + 37;
		 result = 31 * result + msPosition;
		 result = 31 * result + edible;
		 return result;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this)return true;
		 if (!(o instanceof State))
			 return false;
		 State s = (State) o;
		 return (s.getGhostPosition() == ghostPosition &&
				 s.getLastMoveMade() == ghostLastMove &&
				 s.getMsPosition() == msPosition &&
				 s.getEdible() == edible);
	}
	
	// Metodos get
	public int getGhostPosition(){return ghostPosition;}
	public MOVE getLastMoveMade(){return ghostLastMove;}
	public int getMsPosition(){return msPosition;}
	public int getEdible(){return edible;}
	
	public String toString(){
		return(ghostPosition + ";" + ghostLastMove + ";" + msPosition + ";" + edible + ";");
	}
}
