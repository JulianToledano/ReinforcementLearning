package pacman.entries.ghosts;

import pacman.game.Constants.MOVE;

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
