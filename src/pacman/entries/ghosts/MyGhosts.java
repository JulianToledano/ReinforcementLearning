package pacman.entries.ghosts;

import java.util.EnumMap;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	private Qlearning qlearn;
	
	public MyGhosts(Game ng, double value, double eps, double alpha, double gamma){
		qlearn = new Qlearning(ng, value, eps, alpha, gamma);
	}
	
	public MyGhosts(Qlearning q){
		qlearn = q;
	}
	
	public MOVE getMove(State s){
		return qlearn.chooseMove(s);
	}
	
	public Qlearning getQ(){return qlearn;}
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
	{	
		myMoves.clear();
		for(GHOST ghosts : GHOST.values()){
			State s = new State(game, ghosts);
			MOVE move = getMove(s);
			myMoves.put(ghosts, move);
		}
		return myMoves;
	}
}