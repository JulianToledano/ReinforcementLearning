package pacman.entries.ghosts;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Qlearning {
	private Map<state, ArrayList<StateAction>> Q = new HashMap<state,ArrayList<StateAction>>();
	private Map<Integer, Boolean>corners = new HashMap<Integer, Boolean>();
	private double eps;
	private double alpha;
	private double gamma;
	private Game game;
	
	public Qlearning(Game game, double eps, double alpha, double gamma){
		this.eps = eps;
		this.alpha = alpha;
		this.gamma = gamma;
		this.game = game;
		InitQ(game,10);
		System.out.println("Busando...");
		state s = new state(1271,MOVE.DOWN,1292,1);
	/*	ArrayList <StateAction> aa = getMovesValues(s);
		for(int i = 0; i < aa.size(); i++)
			System.out.println(s.toString() + aa.get(i).toString());*/
	}
	
	private void InitQ(Game game, double value){
		System.out.println("hola");
		customInitQ(game, value);
		MOVE totalMoves [] = {MOVE.UP, MOVE.RIGHT, MOVE.LEFT, MOVE.DOWN};
		int junctions[] = game.getJunctionIndices();
		for(int i = 0; i < junctions.length; i++){
			MOVE moves[] = game.getPossibleMoves(junctions[i]);
			for(int j = 0; j < 1292; j++){
				for(int z = 0; z < 2; z++){
					for(int k = 0; k < totalMoves.length; k++){
						state s = new state(junctions[i], totalMoves[k],j,z);
						ArrayList<StateAction> movValue = new ArrayList<StateAction>();
						for(int x = 0; x < moves.length; x++){
							if((totalMoves[k] == MOVE.UP && moves[x] != MOVE.DOWN) || (totalMoves[k] == MOVE.DOWN && moves[x] != MOVE.UP) ||
							(totalMoves[k] == MOVE.RIGHT && moves[x] != MOVE.LEFT) || (totalMoves[k] == MOVE.LEFT && moves[x] != MOVE.RIGHT)){
								StateAction as = new StateAction(moves[x], value);
								movValue.add(as);
								//System.out.println(s.toString() + ";" + moves[x]);
								//Q.put(as, value);
							}
							//for(int l = 0; l < movValue.size(); l++)
							//	System.out.println(s.toString() + movValue.get(i).toString());
							Q.put(s, movValue);
						}
					}
				}
			}
		}
	}
	
	// Guarda los nodos que nos son cruces pero en los que se cambia de dirección
	private void customInitQ(Game game, double value){
		for(int i = 0; i < 1292; i++){
			corners.put(i, false);
			// Nodos que no son cruces
			if(!game.isJunction(i)){
				MOVE moves[] = game.getPossibleMoves(i);
				
				if(!((moves[0] == MOVE.UP && moves[1] == MOVE.DOWN) || (moves[0] == MOVE.DOWN && moves[1] == MOVE.UP) && 
				(moves[0] == MOVE.LEFT && moves[1] == MOVE.RIGHT) || (moves[0] == MOVE.RIGHT && moves[1] == MOVE.LEFT))){
					corners.put(i, true);
					MOVE contrario0, contrario1;
					if(moves[0] == MOVE.DOWN)contrario0 = MOVE.UP;
					else if(moves[0] == MOVE.UP)contrario0 = MOVE.DOWN;
					else if(moves[0] == MOVE.LEFT)contrario0 = MOVE.RIGHT;
					else contrario0 = MOVE.LEFT;
					
					if(moves[1] == MOVE.DOWN)contrario1 = MOVE.UP;
					else if(moves[1] == MOVE.UP)contrario1 = MOVE.DOWN;
					else if(moves[1] == MOVE.LEFT)contrario1 = MOVE.RIGHT;
					else contrario1 = MOVE.LEFT;
					
					for(int j = 0; j < 1292; j++){
						for(int z = 0; z < 2; z++){
							state s0 = new state(i,contrario0,j,z);
							ArrayList<StateAction> movValue0 = new ArrayList<StateAction>();
							StateAction as0 = new StateAction(moves[1], value);
							movValue0.add(as0);
							Q.put(s0, movValue0);
							
							s0 = new state(i,contrario1,j,z);
							movValue0 = new ArrayList<StateAction>();
							as0 = new StateAction(moves[0], value);
							movValue0.add(as0);
							Q.put(s0, movValue0);
						}
					}
				}			
			}
		}
	}
	
	public MOVE chooseMove(state s){
		MOVE myMove = MOVE.NEUTRAL;
		// Observar si el estado existe dentro del conjunto Q(S,A)
		ArrayList <StateAction> as = getMovesValues(s);
		if(as != null){
			
			myMove = policy(s, as);	
			double reward = reward(s,myMove);
			
		//	System.out.print("viejo estado: " + s.toString() + myMove +";"+ reward);
			// Buscamos el siguiente estado tras realiar el movimiento. Esto es
			// el siguiente cruce o esquina que nos encontramos desde el actual tras tomar el movimiento myMove.
			int nextStateIndex = game.getNeighbour(s.getGhostPosition(), myMove);
			while(!game.isJunction(nextStateIndex) && !corners.get(nextStateIndex)){
				//System.out.println(nextStateIndex);
				nextStateIndex = game.getNeighbour(nextStateIndex, myMove);
			}
			state nextState = new state(nextStateIndex, myMove, s.getMsPosition(), s.getEdible());
		//	System.out.print("nuevo Estado" + nextState.toString());
			ArrayList <StateAction> nextStateValues = getMovesValues(nextState);
			// Obtenemos el movimiento con mejor score dentro del siguiente estado que alcanzaremos
			double max = -10000;
			for(int i = 0; i < nextStateValues.size(); i++)
				if(max < nextStateValues.get(i).getValue())
					max = nextStateValues.get(i).getValue();
			
			// Actualizamos el valor de Q(S,a)
			// Buscamos cual de todos tenemos que actualizar dentro del arrayList
			for(int i = 0; i < as.size(); i++)
				if(myMove == as.get(i).getMove()){
					as.get(i).setValue(update(as.get(i).getValue(), reward, max));
				//	System.out.println(as.get(i).getValue());
				}
		}
		return myMove;
	}
	
	private MOVE policy(state s, ArrayList <StateAction> as){
		double p = Math.random();
		MOVE myMove = MOVE.NEUTRAL;
		if(p < eps){
			int rand = (int) (Math.random() * as.size());
			myMove = as.get(rand).getMove();
		}
		else{
			double max = -1000;
			for(int i = 0; i < as.size(); i++){
				if(max < as.get(i).getValue()){
					max = as.get(i).getValue();
					myMove = as.get(i).getMove();
				}
			}
		}
		return myMove;
	}
	
	private double reward(state s, MOVE move){
		// Si el fantasma no puede ser comido
	//	System.out.println(move+";"+s.getLastMoveMade()+";"+s.getGhostPosition()+";" + s.getMsPosition());
		try{
			if(s.getEdible() == 0){
			//	System.out.println("Antes de la llamada");
				MOVE bestMove = game.getApproximateNextMoveTowardsTarget(s.getGhostPosition(), s.getMsPosition(), s.getLastMoveMade(), DM.PATH);
			//	System.out.println("Despues de la llamada");
				// Si el movimiento elegido corresponde con el mejor reward positivo
				if(move == bestMove)return 1.0;
				else return -1.0;
			}
			// En caso de que el fantasma pueda ser comido el mejor movimiento es alejarse del pacman
			else{
			//	System.out.println("Antes de la llamada");
				MOVE bestMove = game.getApproximateNextMoveAwayFromTarget(s.getGhostPosition(), s.getMsPosition(), s.getLastMoveMade(), DM.PATH);
			//	System.out.println("Despues de la llamada");
				if(move == bestMove)return 1.0;
				else return -1.0;
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return 0;
		}
	}
	
	private double update(double qsa, double reward, double qsb){
		return(qsa + alpha * (reward + gamma * qsb - qsa));
	}
	public ArrayList <StateAction> getMovesValues(state s){
		ArrayList <StateAction> as = new ArrayList <StateAction>();
		as = Q.get(s);
		return as;
	}
	
	public void search(state s){
		ArrayList <StateAction> as = getMovesValues(s);
		for(int j = 0; j < as.size(); j++)
			System.out.println(s.toString() + as.get(j).toString());
	}
}
