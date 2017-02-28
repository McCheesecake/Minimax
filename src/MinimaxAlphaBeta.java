//package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class MinimaxAlphaBeta extends Agent {

    private final int numPlys;

    public MinimaxAlphaBeta(int playernum, String[] args)
    {
        super(playernum);

        if(args.length < 1)
        {
            System.err.println("You must specify the number of plys");
            System.exit(1);
        }

        numPlys = Integer.parseInt(args[0]);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView newstate, History.HistoryView statehistory) {
        return middleStep(newstate, statehistory);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView newstate, History.HistoryView statehistory) {
        GameStateChild bestChild = alphaBetaSearch(new GameStateChild(newstate),
                5,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        
        

        return bestChild.action;
    }

    @Override
    public void terminalStep(State.StateView newstate, History.HistoryView statehistory) {

    }

    @Override
    public void savePlayerData(OutputStream os) {

    }

    @Override
    public void loadPlayerData(InputStream is) {

    }

    /**
     * You will implement this.
     *
     * This is the main entry point to the alpha beta search. Refer to the slides, assignment description
     * and book for more information.
     *
     * Try to keep the logic in this function as abstract as possible (i.e. move as much SEPIA specific
     * code into other functions and methods)
     *
     * @param node The action and state to search from
     * @param depth The remaining number of plys under this node
     * @param alpha The current best value for the maximizing node from this node to the root
     * @param beta The current best value for the minimizing node from this node to the root
     * @return The best child of this node with updated values
     */
    public GameStateChild alphaBetaSearch(GameStateChild node, int depth, double alpha, double beta)
    {
        GameState temp_state = node.state;
        //DEBUG
        
    	List<GameStateChild> children_nodes = node.state.getChildren(node, 1);
    	children_nodes = orderChildrenWithHeuristics(children_nodes);
    	
        System.out.println("Required depth: " + depth);
    	
        saved_node Chosen_node = Max_Value(node, alpha, beta, 1, depth);
        GameStateChild output_node = Chosen_node.chosen_state;
        
        System.out.println("chosen action: " + output_node.action);
        System.out.println("Utility " + Chosen_node.utility);
        return output_node;
    }
    
    public saved_node Max_Value(GameStateChild Input_state, double alpha, double beta, int current_depth, int max_depth) {
    	if (current_depth == max_depth) {
    		double v = Input_state.state.getUtility(Input_state);
    		//System.out.println("is this being reached? " + v);
    		saved_node chosen_node = new saved_node(v,Input_state);
    		return chosen_node;
    	}
    	//System.out.println("Current depth (max): " + current_depth);
    	double v = Double.NEGATIVE_INFINITY;
    	
    	List<GameStateChild> children_nodes = Input_state.state.getChildren(Input_state, 1);
    	children_nodes = orderChildrenWithHeuristics(children_nodes);
    	
    	//for (int index = 0; index < children_nodes.size(); index ++) {
    		//System.out.println("Max child actions: " + children_nodes.get(index).action);
    	//}
    	//System.out.println(" ");
    	
    	saved_node chosen_node = new saved_node(0, Input_state);
    	
    	for (int index = 0; index < children_nodes.size(); index ++) {
    		double v_temp = Min_Value(children_nodes.get(index),alpha,beta,current_depth+1,max_depth).utility;
    		if (v < v_temp) {
    			v = v_temp;
    			chosen_node.utility = v;
    			chosen_node.chosen_state = children_nodes.get(index);
    		}
    		
    		if (v > beta) {
    			return chosen_node;
    		}
    		alpha = Math.max(alpha, v);
    	}
    	
    	return chosen_node;
    }
    
    public saved_node Min_Value(GameStateChild Input_state, double alpha, double beta, int current_depth, int max_depth) {
    	if (current_depth == max_depth) {
    		double v = Input_state.state.getUtility(Input_state);
    		//System.out.println("is this being reached? " + v);
    		saved_node chosen_node = new saved_node(v,Input_state);
    		return chosen_node;
    	}
    	//System.out.println("Current depth (min): " + current_depth);
    	double v = Double.POSITIVE_INFINITY;
    	
    	List<GameStateChild> children_nodes = Input_state.state.getChildren(Input_state, 0);
    	children_nodes = orderChildrenWithHeuristics(children_nodes);
    	
    	//for (int index = 0; index < children_nodes.size(); index ++) {
    		//System.out.println("Min child actions: " + children_nodes.get(index).action);
    	//}
    	
    	saved_node chosen_node = new saved_node(0, Input_state);
    	
    	for (int index = 0; index < children_nodes.size(); index ++) {
    		double v_temp = Max_Value(children_nodes.get(index),alpha,beta,current_depth+1,max_depth).utility;
    		//System.out.println("Is this being reached?");
    		if (v > v_temp) {
    			v = v_temp;
    			chosen_node.utility = v;
    			chosen_node.chosen_state = children_nodes.get(index);
    		}
    		if (v <= alpha) {
    			return chosen_node;
    		}
    		beta = Math.min(beta, v);
    	}
    	
    	//System.out.println(current_depth);
    	
    	return chosen_node;
    }
    
    //Return this node from Min_Value and Max_Value functions
    public class saved_node {
    	double utility;
    	GameStateChild chosen_state;
    	public saved_node(double utility, GameStateChild chosen_state) {
    		this.utility = utility;
    		this.chosen_state = chosen_state;
    	}
    }
    
    /**
     * You will implement this.
     *
     * Given a list of children you will order them according to heuristics you make up.
     * See the assignment description for suggestions on heuristics to use when sorting.
     *
     * Use this function inside of your alphaBetaSearch method.
     *
     * Include a good comment about what your heuristics are and why you chose them.
     *
     * @param children
     * @return The list of children sorted by your heuristic.
     */
    public List<GameStateChild> orderChildrenWithHeuristics(List<GameStateChild> children)
    {
    	List<GameStateChild> sorted_children = new ArrayList<GameStateChild>();
    	double[][] heuristic_array = new double[children.size()][2];
    	for (int index = 0; index < children.size(); index ++) {
    		heuristic_array[index][0] = (double) index;
    		heuristic_array[index][1] = children.get(index).state.heuristic;
    	}
    	
    	java.util.Arrays.sort(heuristic_array, java.util.Comparator.comparingDouble(a -> a[1]));
    	
    	for (int index = 0; index < children.size(); index ++) {
    		sorted_children.add(children.get((int) heuristic_array[index][0]));
    	}
    	
    	return sorted_children;
    }
}
