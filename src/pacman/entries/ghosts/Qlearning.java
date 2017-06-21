package pacman.entries.ghosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Esta clase es el propio algoritmo de Qlearning.
 * Posee el HashMap donde se inicializar��n todos los Q(S,a)
 * y otro HashMap que guarda todos los indices de los nodos del juego junto a un booleano
 * que indica si se trata de una esquina. Esto es un lugar donde debe cambiar el movimiento de forma obligatoria por otro
 * que no sea justo el contrario. (UP -> LEFT, LEFT -> DOWN), nunca del tipo (LEFT -> RIGHT o UP -> DOWN).
 * 
 *  Dentro de Q(S,a) solo se guardan los estados en los que el fantasma est�� en un cruce (que tiene m��s de dos vecinos) o
 *  en una esquina.
 *  
 *  eps -> ��
 *  alpha -> ��
 *  gamma -> �� 
 * @author julian
 *
 */
public class Qlearning {
	private Map<State, ArrayList<StateAction>> Q = new HashMap<State,ArrayList<StateAction>>();
	private Map<Integer, Boolean>corners = new HashMap<Integer, Boolean>();
	private double eps;
	private double alpha;
	private double gamma;
	private Game game;
	/**
	 * Constructor
	 * @param game se utiliza para obtener todos los indices de cruces y esquinas.
	 * @param eps
	 * @param alpha
	 * @param gamma
	 */
	public Qlearning(Game game, double value, double eps, double alpha, double gamma){
		this.eps = eps;
		this.alpha = alpha;
		this.gamma = gamma;
		this.game = game;
		InitQ(game,value);
	}
	
	/**
	 * Inicializa Q(S,a).
	 * Las esquinas se inicializan dentro de customInitQ() mientras que los cruces se inicializan aqu��.
	 * @param game se utiliza para obtener todos los indices de cruces y esquinas.
	 * @param value valor inicial que se les da a Q(S,a).
	 */
	private void InitQ(Game game, double value){
		// Inicializaci��n de las esquinas
		customInitQ(game, value);
		// Todos los ��ltimos posibles movimientos
		MOVE totalMoves [] = {MOVE.UP, MOVE.RIGHT, MOVE.LEFT, MOVE.DOWN};
		// Todos los incides de los cruces (m��s de dos vecinos)
		int junctions[] = game.getJunctionIndices();
		// Por cada cruce
		for(int i = 0; i < junctions.length; i++){
			// Posibles movimientos en el cruce i
			MOVE moves[] = game.getPossibleMoves(junctions[i]);
			// Por cada indice en el que podr��a estar Ms.
			for(int j = 0; j < 1292; j++){
				// Si puede ser comido
				for(int z = 0; z < 2; z++){
					// Por cada ��ltimo movimiento. Destacar que es posible que se creen estados a los que nunca se llegue.
					// Por ejemplo un estado con ��ndice I al que no se pueda llegar realizando MOVE.DOWN porque tiene una pared por encima.
					// Se inicializan pero solo suponen un gasto memoria ya que como nunca van a ser alcanzados no reportar��n problemas.
					for(int k = 0; k < totalMoves.length; k++){
						/**
						 * Creamos un estado con
						 * ghostPosition -> junctions[i]  ->  el cruce
						 * lastMove		-> totalMoves[k] 
						 * msPosition -> j todos los nodos posibles
						 * edible -> z 1 si s�� 0 si no.
						 */
						State s = new State(junctions[i], totalMoves[k],j,z);
						// Se crea una lista en la que se introducir��n los posibles movimientos en el ��ndice I.
						ArrayList<StateAction> movValue = new ArrayList<StateAction>();
						for(int x = 0; x < moves.length; x++){
							// Nos preocumapos de no meter el movimiento contrario a totalMoves[k]; ��ltimo movimiento realizado por el pacman.
							if((totalMoves[k] == MOVE.UP && moves[x] != MOVE.DOWN) || (totalMoves[k] == MOVE.DOWN && moves[x] != MOVE.UP) ||
							(totalMoves[k] == MOVE.RIGHT && moves[x] != MOVE.LEFT) || (totalMoves[k] == MOVE.LEFT && moves[x] != MOVE.RIGHT)){
								StateAction as = new StateAction(moves[x], value);
								movValue.add(as);
							}

							Q.put(s, movValue);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Guarda los nodos que nos son cruces pero en los que se cambia de direcci��n. Esquinas.
	 * @param game se utiliza para obtener todos los indices de cruces y esquinas.
	 * @param value valor inicial que se les da a Q(S,a).
	 */
	private void customInitQ(Game game, double value){
		for(int i = 0; i < 1292; i++){
			// Suponemos que no es una esquina
			corners.put(i, false);
			// Si el nodo no es un cruce.
			if(!game.isJunction(i)){
				MOVE moves[] = game.getPossibleMoves(i);
				// Y sus posibles movimientos no son los de un pasillo, esto es que no son movimientos contrarios.
				// Valen todas las combinaciones excepto: (UP -- DOWN) y (LEFT -- RIGHT).
				if(!((moves[0] == MOVE.UP && moves[1] == MOVE.DOWN) || (moves[0] == MOVE.DOWN && moves[1] == MOVE.UP) && 
				(moves[0] == MOVE.LEFT && moves[1] == MOVE.RIGHT) || (moves[0] == MOVE.RIGHT && moves[1] == MOVE.LEFT))){
					// Se trata de una esquina.
					corners.put(i, true);
					// Los movimientos contrarios a game.getPossibleMoves(i) son los movimientos que el fantasma
					// podr�� haber realizado como ��ltimo movimiento para llegar a el nodo I.
					MOVE contrario0, contrario1;
					if(moves[0] == MOVE.DOWN)contrario0 = MOVE.UP;
					else if(moves[0] == MOVE.UP)contrario0 = MOVE.DOWN;
					else if(moves[0] == MOVE.LEFT)contrario0 = MOVE.RIGHT;
					else contrario0 = MOVE.LEFT;
					
					if(moves[1] == MOVE.DOWN)contrario1 = MOVE.UP;
					else if(moves[1] == MOVE.UP)contrario1 = MOVE.DOWN;
					else if(moves[1] == MOVE.LEFT)contrario1 = MOVE.RIGHT;
					else contrario1 = MOVE.LEFT;
					
					// Por cada posici��n en la que pode��a estar el pacman.
					for(int j = 0; j < 1292; j++){
						// Si es comestible o no.
						for(int z = 0; z < 2; z++){
							// Creamos un estado con i, el contrario de moves[1], j y z.
							State s0 = new State(i,contrario0,j,z);
							// Creamos una lista con los posibles movimientos. En estos casos solo 1.
							ArrayList<StateAction> movValue0 = new ArrayList<StateAction>();
							// El ��nico posible movimiento es el contrario de contrario0, moves[1].
							StateAction as0 = new StateAction(moves[1], value);
							movValue0.add(as0);
							Q.put(s0, movValue0);
							
							// Exactamente igual que lo anterior pero cambiando los movimientos por contrario1 y moves[0]-
							s0 = new State(i,contrario1,j,z);
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
	
	/**
	 * Elige un movimiento y actualiza el valor de Q(S,a)
	 * @param s el estado del juego en el momento t.
	 * @return
	 */
	public MOVE chooseMove(State s){
		// Inicializamos a NEUTRAL ya que es posible que el fantasma no se encuentre en un cruce.
		MOVE myMove = MOVE.NEUTRAL;
		// Observar si el estado existe dentro del conjunto Q(S,A)
		ArrayList <StateAction> as = getMovesValues(s);
		if(as != null){
			// Dentro de los posibles movimientos la pol��tica elige uno de ellos.
			myMove = policy(as);
			// Tras el movimiento elegido se obtine un reward.
			double reward = reward(s,myMove);
			
			// Buscamos el siguiente estado tras realiar el movimiento.
			int nextStateIndex = game.getNeighbour(s.getGhostPosition(), myMove);
			// Mientras el siguiente estado no sea un cruce o esquina avanzamos.
			while(!game.isJunction(nextStateIndex) && !corners.get(nextStateIndex)){
				nextStateIndex = game.getNeighbour(nextStateIndex, myMove);
			}
			State nextState = new State(nextStateIndex, myMove, s.getMsPosition(), s.getEdible());
			ArrayList <StateAction> nextStateValues = getMovesValues(nextState);
			// Obtenemos el movimiento con mejor score dentro del siguiente estado que alcanzamos.
			double max = -10000;
			for(int i = 0; i < nextStateValues.size(); i++)
				if(max < nextStateValues.get(i).getValue())
					max = nextStateValues.get(i).getValue();
			
			// Buscamos cual de todos tenemos que actualizar dentro del arrayList.
			for(int i = 0; i < as.size(); i++)
				// Actualizamos el valor de Q(S,a).
				if(myMove == as.get(i).getMove())
					as.get(i).setValue(update(as.get(i).getValue(), reward, max));
				
		}
		return myMove;
	}
	/**
	 * Pol��tica ��-greedy
	 * Se crea un n��mero aleatorio, si este es superior a a ��, se toma el mejor movimiento,
	 * en caso contrario se toma un movimiento aleatorio.
	 * @param as arraylist con los posibles movimientos y sus scores.
	 * @return
	 */
	private MOVE policy(ArrayList <StateAction> as){
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
	/**
	 * @param s estado del juego en el momento t
	 * @param move movimiento elegido para llevar a cabo en el estado s
	 * @return 1.0 en caso de que sea un movimiento ��ptimo, -1.0 en caso contrario.
	 */
	private double reward(State s, MOVE move){		
		try{
			// Si el fantasma no puede ser comido
			if(s.getEdible() == 0){
				// El mejor movimiento es acercarse hacia el pacman.
				MOVE bestMove = game.getApproximateNextMoveTowardsTarget(s.getGhostPosition(), s.getMsPosition(), s.getLastMoveMade(), DM.PATH);
				// Si el movimiento elegido corresponde con el mejor reward positivo
				if(move == bestMove)return 1.0;
				else return -1.0;
			}
			// En caso de que el fantasma pueda ser comido el mejor movimiento es alejarse del pacman
			else{
				MOVE bestMove = game.getApproximateNextMoveAwayFromTarget(s.getGhostPosition(), s.getMsPosition(), s.getLastMoveMade(), DM.PATH);
				if(move == bestMove)return 1.0;
				else return -1.0;
			}
		}catch(NullPointerException e){
			System.out.println(e.getMessage());
			return 0;
		}
	}
	/**
	 * Q(s, a) ��� Q(s, a) + �� �� [r + �� �� maxQ(s���, b) ��� Q(s,a)]
	 * @param qsa ��� score en de Q(S,a)
	 * @param reward ��� r
	 * @param qsb ��� score de maxQ(s���, b)
	 * @return el resultado de la f��rmula
	 */
	private double update(double qsa, double reward, double qsb){
		return(qsa + alpha * (reward + gamma * qsb - qsa));
	}
	
	/**
	 * @param s un estado en el momento t
	 * @return lista de los posibles movimientos en el estado s
	 */
	public ArrayList <StateAction> getMovesValues(State s){
		ArrayList <StateAction> as = new ArrayList <StateAction>();
		as = Q.get(s);
		return as;
	}
}
