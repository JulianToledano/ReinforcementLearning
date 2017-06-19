package pacman.entries.ghosts;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

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
