//package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.environment.model.state.UnitTemplate.UnitTemplateView;
import edu.cwru.sepia.util.Direction;

import java.util.*;

/**
 * This class stores all of the information the agent
 * needs to know about the state of the game. For example this
 * might include things like footmen HP and positions.
 *
 * Add any information or methods you would like to this class,
 * but do not delete or change the signatures of the provided methods.
 */
public class GameState {
	
    public int xExtent;
	public int yExtent;
	public int [][] resource_loc; //first is x, second is y
	public int enemy_ID;
	public List<Unit_info> My_Units;
	public List<Unit_info> Enemy_Units;
	public int path_cost;
	//public double v;
	
	public double heuristic;
	
	public class unit_action {
		public Action move(int unitID, String Command){
			if (Command == "NORTH") {
				//return Action.createPrimitiveMove(unitID, Direction.NORTH);
			}
			if (Command == "EAST") {
				//return Action.createPrimitiveMove(unitID, Direction.EAST);
			}
			if (Command == "SOUTH") {
				//return Action.createPrimitiveMove(unitID, Direction.SOUTH);
			}
			if (Command == "WEST") {
				//return Action.createPrimitiveMove(unitID, Direction.WEST);
			}
			return null;
		}
		public Action attack(int my_unitID, int enemy_unitID) {
			return Action.createPrimitiveAttack(my_unitID, enemy_unitID);
		}
	}
	
	public class Unit_info {
		public int health;
		public int range;
		public int damage;
		public int x_loc;
		public int y_loc;
		public Integer unit_ID;
		public String name;
		
		public Unit_info(int health, int range, int damage, int x_loc, int y_loc, Integer unit_ID, String name) {
			this.health = health;
			this.range = range;
			this.damage = damage;
			this.x_loc = x_loc;
			this.y_loc = y_loc;
			this.unit_ID = unit_ID;
			this.name = name;
		}
		
	}
	
	public Unit_info deepCopy(Unit_info unit){
		Unit_info copy = new Unit_info(unit.health, unit.range, unit.damage, unit.x_loc, unit.y_loc, unit.unit_ID, unit.name);
		return copy;
	}


	
	public GameState(int xExtent, int yExtent, int[][] resource_loc, int enemy_ID, List<Unit_info> My_Units, List<Unit_info> Enemy_Units, int path_cost) {
		this.xExtent = xExtent;
		this.yExtent = yExtent;
		this.resource_loc = resource_loc;
		this.enemy_ID = enemy_ID;
		this.My_Units = My_Units;
		this.Enemy_Units = Enemy_Units;
		this.path_cost = path_cost;
		
		//calculating node heuristic:
		//constants:
		
		//function:
		double heuristic;
		
		int total_dist = 0;
		int enemy_total_health = 0;
		
		for (int index = 0; index < My_Units.size(); index ++) {
			int shortest_dist = 99999;
			for (int index1 = 0; index < Enemy_Units.size(); index ++) {
				int temp_dist = Math.abs(My_Units.get(index).x_loc-Enemy_Units.get(index1).x_loc) + Math.abs(My_Units.get(index).y_loc-Enemy_Units.get(index1).y_loc);
				if (temp_dist < shortest_dist) {
					shortest_dist = temp_dist;
				}
			}
			total_dist = total_dist + shortest_dist;
		}
		
		for (int index = 1; index < Enemy_Units.size(); index ++) {
			enemy_total_health = enemy_total_health + Enemy_Units.get(index).health;
		}
		
		heuristic = -enemy_total_health - total_dist;
		
		this.heuristic = heuristic;
	}
	/**
     * You will implement this constructor. It will
     * extract all of the needed state information from the built in
     * SEPIA state view.
     *
     * You may find the following state methods useful:
     *
     * state.getXExtent() and state.getYExtent(): get the map dimensions
     * state.getAllResourceIDs(): returns all of the obstacles in the map
     * state.getResourceNode(Integer resourceID): Return a ResourceView for the given ID
     *
     * For a given ResourceView you can query the position using
     * resource.getXPosition() and resource.getYPosition()
     *
     * For a given unit you will need to find the attack damage, range and max HP
     * unitView.getTemplateView().getRange(): This gives you the attack range
     * unitView.getTemplateView().getBasicAttack(): The amount of damage this unit deals
     * unitView.getTemplateView().getBaseHealth(): The maximum amount of health of this unit
     *
     * @param state Current state of the episode
     */
    public GameState(State.StateView state) {
    	
    	List<Integer> resourceIDs = state.getAllResourceIds();
    	
    	int [][] resource_loc = new int [2][resourceIDs.size()];
    	
    	for (int index = 0; index < resourceIDs.size(); index ++) {
    		ResourceNode.ResourceView resource = state.getResourceNode(resourceIDs.get(index));
    		resource_loc[0][index] = resource.getXPosition();
    		resource_loc[1][index] = resource.getYPosition();
    	}
     	
    	
     	//System.out.println(resource_loc[0].length);
     	
     	int my_ID = 0;
     	int enemy_ID = -1;
     	Integer[] player_IDs = state.getPlayerNumbers();
     	for (Integer player_ID : player_IDs) {
     		if (player_ID != my_ID) {
     			enemy_ID = player_ID;
     			break;
     		}
     	}
     	
     	
     	if (enemy_ID != -1) {
     		List<Integer> my_unitIDs = state.getUnitIds(my_ID);
     		List<Integer> enemy_unitIDs = state.getUnitIds(enemy_ID);
     		List<Integer> all_unitIDs = state.getAllUnitIds();
     		
     		List<Unit_info> My_Units = new ArrayList<Unit_info>();
     		List<Unit_info> Enemy_Units = new ArrayList<Unit_info>();
     		if (enemy_unitIDs.size() != 0) {
     			for (int index = 0; index < state.getAllUnitIds().size(); index ++) {
     				int x_temp = state.getUnit(all_unitIDs.get(index)).getXPosition();
     				int y_temp = state.getUnit(all_unitIDs.get(index)).getYPosition();
     				int health_temp = state.getUnit(all_unitIDs.get(index)).getHP();
     				String name_temp = state.getUnit(all_unitIDs.get(index)).getTemplateView().getName();
     				int damage_temp = state.getUnit(all_unitIDs.get(index)).getTemplateView().getBasicAttack();
     				int range_temp = state.getUnit(all_unitIDs.get(index)).getTemplateView().getRange();
     				int player_temp = state.getUnit(all_unitIDs.get(index)).getTemplateView().getPlayer();
     				
     				if (player_temp == my_ID) {
     					Unit_info my_unit_temp = new Unit_info(health_temp,range_temp,damage_temp,x_temp,y_temp,state.getAllUnitIds().get(index),name_temp);
     					My_Units.add(my_unit_temp);
     				}
     				if (player_temp == enemy_ID) {
     					Unit_info enemy_unit_temp = new Unit_info(health_temp,range_temp,damage_temp,x_temp,y_temp,state.getAllUnitIds().get(index),name_temp);
     					Enemy_Units.add(enemy_unit_temp);
     				}
     			}
     		}
     		this.xExtent = state.getXExtent();
        	this.yExtent = state.getYExtent();
        	this.resource_loc = resource_loc;
         	this.enemy_ID = enemy_ID;
     		this.My_Units = My_Units;
     		this.Enemy_Units = Enemy_Units;
     		
     		int path_cost = 0;
     		this.path_cost = path_cost;
     		
     		//heuristic:
    		double heuristic;
    		
    		int total_dist = 0;
    		int enemy_total_health = 0;
    		
    		for (int index = 1; index < My_Units.size(); index ++) {
    			int shortest_dist = 99999;
    			for (int index1 = 1; index < Enemy_Units.size(); index ++) {
    				int temp_dist = Math.abs(My_Units.get(index).x_loc-Enemy_Units.get(index1).x_loc) + Math.abs(My_Units.get(index).y_loc-Enemy_Units.get(index1).y_loc);
    				if (temp_dist < shortest_dist) {
    					shortest_dist = temp_dist;
    				}
    			}
    			total_dist = total_dist + shortest_dist;
    		}
    		
    		for (int index = 1; index < Enemy_Units.size(); index ++) {
    			enemy_total_health = enemy_total_health + Enemy_Units.get(index).health;
    		}
    		
    		heuristic = -enemy_total_health - total_dist;
    		
    		this.heuristic = heuristic;
     	}
    }

    /**
     * You will implement this function.
     *
     * You should use weighted linear combination of features.
     * The features may be primitives from the state (such as hp of a unit)
     * or they may be higher level summaries of information from the state such
     * as distance to a specific location. Come up with whatever features you think
     * are useful and weight them appropriately.
     *
     * It is recommended that you start simple until you have your algorithm working. Then watch
     * your agent play and try to add features that correct mistakes it makes. However, remember that
     * your features should be as fast as possible to compute. If the features are slow then you will be
     * able to do less plys in a turn.
     *
     * Add a good comment about what is in your utility and why you chose those features.
     *
     * @return The weighted linear combination of the features
     */
    //public double getUtility(GameStateChild child, State.StateView state, int playernum) {
    	//Changed due to not needed State.StateView and playernum?
    	/* features to include:
    	 * distance to enemy units in total
    	 * my units total health
    	 * enemy units total health
    	 * path cost taken to get to current state
    	 */
    public double getUtility(GameStateChild child) {
    	
    	List<Unit_info> My_units = child.state.My_Units;
    	List<Unit_info> Enemy_units = child.state.Enemy_Units;
    	
    	//calculate my total health:
    	int my_total_health = 0;
    	int enemy_total_health = 0;
    	int[] distance = new int[My_units.size()];
    	for (int index = 0; index < My_units.size(); index ++) {
    		my_total_health += My_units.get(index).health;
    		
    		for (int index1 = 0; index1 < Enemy_units.size(); index1++) {
    			enemy_total_health += Enemy_units.get(index1).health;
    			distance[index] = Math.abs(My_units.get(index).x_loc-Enemy_units.get(index1).x_loc)+Math.abs(My_units.get(index).y_loc-Enemy_units.get(index1).y_loc);
    		}
    	}
    	int total_distance = 0;
    	for (int index = 0; index < distance.length; index ++) {
    		total_distance += distance[index];
    	}
    	
    	int total_cost = AStarCalc(child);
    	
    	double utility = my_total_health - 5*enemy_total_health - total_distance - 10*total_cost;
    	
    	System.out.println("Shortest path cost: " + total_cost);
    	return utility;
    	
    }
    
    public class path_loc {
    	int x_loc;
    	int y_loc;
    	int cost;
    	public path_loc(int x_loc, int y_loc, int cost) {
    		this.x_loc = x_loc;
    		this.y_loc = y_loc;
    		this.cost = cost;
    	}
    }
    
    //Calculate pathfinding heuristic:
    public int AStarCalc(GameStateChild child) {
    	List<Unit_info> My_units = child.state.My_Units;
    	List<Unit_info> Enemy_units = child.state.Enemy_Units;
    	int[][] Resource_loc = child.state.resource_loc;
    	int[][] Movable_space = new int[2][4];
    	int[] Closest_movement_cost = new int [My_units.size()];
    	for (int index =0; index < My_units.size(); index ++){
    		Closest_movement_cost[index] = 9999;
    	}
    	for (int index =0; index < 4; index ++){
    		Movable_space[0][index] = -1;
    		Movable_space[1][index] = -1;
    	}
    	
    	//Find open locations next to enemy unit:
    	for(int index0 = 0; index0 < My_units.size(); index0 ++) {
    		int x_mine = My_units.get(index0).x_loc;
    		int y_mine = My_units.get(index0).y_loc;
	    	for(int index = 0; index < Enemy_units.size(); index ++) {
	    		int x_temp = Enemy_units.get(index).x_loc;
	    		int y_temp = Enemy_units.get(index).y_loc;
		    		int north_closed = 0;
		    		int south_closed = 0;
		    		int east_closed  = 0;
		    		int west_closed  = 0;
		    		if (Math.abs(x_mine-x_temp) + Math.abs(y_mine-y_temp) > 1) {
			    		for(int index1 = 0; index1 < Resource_loc[0].length; index1 ++) {
			    			int resource_dist_temp = Math.abs(resource_loc[0][index1] - x_temp) + Math.abs(resource_loc[1][index1] - y_temp);
							if (resource_dist_temp == 1) {
								//determine resource location:
								if (resource_loc[0][index1] - x_temp == 1) 	{
									east_closed = 1;
								}
								if (resource_loc[0][index1] - x_temp == -1) {
									west_closed = 1;
								}
								if (resource_loc[1][index1] - y_temp == -1) {
									north_closed = 1;
								}
								if (resource_loc[1][index1] - y_temp == 1) 	{
									south_closed = 1;
								}
							}
			    		}
			    		if (north_closed == 0){
			    			Movable_space[0][0] = x_temp; 
			    			Movable_space[1][0] = y_temp-1;
			    		}
			    		if (east_closed == 0){
			    			Movable_space[0][1] = x_temp+1;
			    			Movable_space[1][1] = y_temp;
			    		}
			    		if (south_closed == 0){
			    			Movable_space[0][2] = x_temp;
			    			Movable_space[1][2] = y_temp+1;
			    		}
			    		if (west_closed == 0){
			    			Movable_space[0][3] = x_temp-1;
			    			Movable_space[1][3] = y_temp;
			    		}
			    		for (int index2 = 0; index2 < Movable_space[0].length; index2 ++) {
			    			if (Movable_space[0][index2] != -1 | Movable_space[1][index2] != -1) {
			    				path_loc temp_start = new path_loc(x_temp, y_temp, 0);
			    				path_loc temp_goal =  new path_loc(Movable_space[0][index2], Movable_space[1][index2], 0);
			    				int cost_temp = AStarHeuristic(temp_start, temp_goal, Resource_loc);
			    				if (Closest_movement_cost[index0] > cost_temp) {
			    					Closest_movement_cost[index0] = cost_temp;
			    				}
			    			}
			    		}
			    	}
	    	}
    	}
    	
    	int total_cost = 0;
    	for (int index = 0; index < My_Units.size(); index ++) {
    		total_cost = Closest_movement_cost[index]; 
    	}
    	return total_cost;
    }
    
    public int AStarHeuristic(path_loc start, path_loc goal, int[][] Resource_loc) {
    	List<path_loc> Open_list = new ArrayList<path_loc>();
    	List<path_loc> Closed_list = new ArrayList<path_loc>();
    	List<path_loc> Child_list = new ArrayList<path_loc>();
    	int shortest_path_cost = 0;
    	Open_list.add(start);
    	while (Open_list.size() > 0) {
    		int lowest_cost = 999999;
    		int lowest_cost_index = -1;
    		for (int index = 0; index < Open_list.size(); index++) {
    			int lowest_cost_temp = heuristic_calc(Open_list.get(index),goal) + Open_list.get(index).cost;
    			if(lowest_cost_temp <= lowest_cost) {
    				lowest_cost_index = index;
    				lowest_cost = lowest_cost_temp;
    			}
    			path_loc chosen_node = new path_loc(Open_list.get(lowest_cost_index).x_loc, Open_list.get(lowest_cost_index).y_loc, Open_list.get(lowest_cost_index).cost);
    			if (chosen_node.cost >= 999) {
    				System.out.println("No path to target");
    				shortest_path_cost = -1;
    			}
    			if (chosen_node.x_loc == goal.x_loc & chosen_node.y_loc == goal.y_loc) {
    				Open_list.clear();
    				Closed_list.clear();
    				Child_list.clear();
    				shortest_path_cost = chosen_node.cost;
    				System.out.println("shortest path cost for 1 unit: " + shortest_path_cost);
    				break;
    			}
    			Child_list = AStarChildrenGen(chosen_node, Resource_loc);
    			int remove_count = 0;
    			for (int index1 = 0; index1 < Child_list.size(); index1 ++) {
    				for (int index2 = 0; index2 < Closed_list.size(); index2 ++) {
    					if (Child_list.get(index1).x_loc == Closed_list.get(index2).x_loc & Child_list.get(index1).y_loc == Closed_list.get(index2).y_loc) {
    						Child_list.remove(index1 - remove_count);
    						remove_count ++;
    					}
    				}
    			}
    			remove_count = 0;
    			int open_remove_count = 0;
    			for (int index1 = 0; index1 < Child_list.size(); index1 ++) {
    				for (int index2 = 0; index2 < Open_list.size(); index2 ++) {
    					if (Child_list.get(index1).x_loc == Open_list.get(index2).x_loc & Child_list.get(index1).y_loc == Open_list.get(index2).y_loc) {
    						if(Child_list.get(index1).cost < Open_list.get(index2).cost) {
    							Open_list.remove(index2 - open_remove_count);
    							open_remove_count ++;
    						}
    						if(Child_list.get(index1).cost > Open_list.get(index2).cost) {
    							Child_list.remove(index1 - remove_count);
    							remove_count ++;
    						}
    					}
    				}
    			}
    			for (int index1 = 0; index1 < Child_list.size(); index1 ++) {
    				Open_list.add(Child_list.get(index1));
    			}
    		}
    	}
    	return shortest_path_cost;
    }
    
    public int heuristic_calc(path_loc start, path_loc goal) {
    	int h_cost = 0;
    	h_cost = Math.abs(start.x_loc - goal.x_loc) + Math.abs(start.y_loc - goal.y_loc);
    	return h_cost;
    }
    
    public List<path_loc> AStarChildrenGen(path_loc parent, int[][] resource_loc) {
    	List<path_loc> children_nodes = new ArrayList<path_loc>();
    	int xp_check = 0;
    	int xm_check = 0;
    	int yp_check = 0;
    	int ym_check = 0;
    	for (int index = 0; index < resource_loc[0].length; index ++) {
    		if (resource_loc[0][index] == parent.x_loc+1 & resource_loc[1][index] == parent.y_loc) {
    			xp_check = 1;
    		}
    		if (resource_loc[0][index] == parent.x_loc-1 & resource_loc[1][index] == parent.y_loc) {
    			xm_check = 1;
    		}
    		if (resource_loc[0][index] == parent.x_loc & resource_loc[1][index] == parent.y_loc+1) {
    			yp_check = 1;
    		}
    		if (resource_loc[0][index] == parent.x_loc & resource_loc[1][index] == parent.y_loc-1) {
    			ym_check = 1;
    		}
    	}
    	
    	for (int index = 0; index < 4; index ++) {
    		if (xp_check != 1) {
    		children_nodes.add(new path_loc(parent.x_loc+1, parent.y_loc, parent.cost+1));
    		}
    		if (xm_check != 1) {
    		children_nodes.add(new path_loc(parent.x_loc-1, parent.y_loc, parent.cost+1));
    		}
    		if (yp_check!= 1) {
    		children_nodes.add(new path_loc(parent.x_loc, parent.y_loc+1, parent.cost+1));
    		}
    		if (ym_check!= 1) {
    		children_nodes.add(new path_loc(parent.x_loc, parent.y_loc-1, parent.cost+1));
    		}
    	}
    	return children_nodes;
    }

    /**
     * You will implement this function.
     *
     * This will return a list of GameStateChild objects. You will generate all of the possible
     * actions in a step and then determine the resulting game state from that action. These are your GameStateChildren.
     *
     * You may find it useful to iterate over all the different directions in SEPIA.
     *
     * for(Direction direction : Directions.values())
     *
     * To get the resulting position from a move in that direction you can do the following
     * x += direction.xComponent()
     * y += direction.yComponent()
     *
     * @return All possible actions and their associated resulting game state
     */
    public List<GameStateChild> getChildren(GameStateChild parent_state_orig, int status) {
    	GameState parent_state = parent_state_orig.state;
    	List<GameStateChild> children_nodes = new ArrayList<GameStateChild>();
    	
    	//status == 1: friendly turn
    	//status == 0: enemy turn
    	List<Unit_info> moving_units = new ArrayList<Unit_info>();
    	List<Unit_info> static_units = new ArrayList<Unit_info>();
    	
    	if (status == 1) {
    		moving_units = parent_state.My_Units;
    		static_units = parent_state.Enemy_Units;
    	}
    	if (status == 0) {
    		moving_units = parent_state.Enemy_Units;
    		static_units = parent_state.My_Units;
    	}
    	
    	if (moving_units.size() == 0 | static_units.size() == 0) {
    		System.out.println("No units to move!");
    		return null;
    	}
    	
    	String[] valid_actions = {"NORTH", "EAST", "SOUTH", "WEST", "Attack"};
    	
    	//System.out.println("Unit 1 name: " + moving_units.get(0).name + "; Range: " + moving_units.get(0).range);
    	
    	if (moving_units.size() != 0) {
    		for (int index1 = 0; index1 < (int)Math.pow(valid_actions.length, moving_units.size()); index1 ++) {
    			int action_num = index1;
    			Map<Integer,Action> child_node_action = new HashMap<Integer,Action>(); 
    			
    			List<Unit_info> moving_units_temp = new ArrayList<Unit_info>();
    			List<Unit_info> static_units_temp = new ArrayList<Unit_info>();
    			for (int array_ind = 0; array_ind < moving_units.size(); array_ind ++) {
    				moving_units_temp.add(deepCopy(moving_units.get(array_ind)));
    			}
    			for (int array_ind = 0; array_ind < static_units.size(); array_ind ++) {
    				static_units_temp.add(deepCopy(static_units.get(array_ind)));
    			}
    			
    			//System.out.println(moving_units_temp.get(1).x_loc + " " + moving_units_temp.get(1).y_loc);
    			
    			for (int index2 = 0; index2 < moving_units.size(); index2++) {
    				Unit_info current_unit = moving_units_temp.get(index2);
    				
    				//Binary check for resource nearby:
    				int resource_north = 0;
    				int resource_east = 0;
    				int resource_south = 0;
    				int resource_west = 0;
    				
    				for (int rs_index = 0; rs_index < parent_state.resource_loc[0].length; rs_index ++) {
    					int resource_dist_temp = Math.abs(resource_loc[0][rs_index] - current_unit.x_loc) + Math.abs(resource_loc[1][rs_index] - current_unit.y_loc);
    					if (resource_dist_temp == 1) {
    						//determine resource location:
    						if (resource_loc[0][rs_index] - current_unit.x_loc == 1) {
    							resource_east = 1;
    						}
    						if (resource_loc[0][rs_index] - current_unit.x_loc == -1) {
    							resource_west = 1;
    						}
    						if (resource_loc[1][rs_index] - current_unit.y_loc == -1) {
    							resource_north = 1;
    						}
    						if (resource_loc[1][rs_index] - current_unit.y_loc == 1) {
    							resource_south = 1;
    						}
    					}
    				}
    				
    				//check if running into one another:
    				for (int index = 0; index < moving_units.size(); index ++) {
    					int unit_dist_check = Math.abs(current_unit.x_loc - moving_units.get(index).x_loc) + Math.abs(current_unit.y_loc - moving_units.get(index).y_loc);  
    					if (unit_dist_check == 1) {
    						int unit_dist_x = current_unit.x_loc - moving_units.get(index).x_loc;
    						int unit_dist_y = current_unit.y_loc - moving_units.get(index).y_loc;
    						if (unit_dist_x == 1) {
    							resource_west = 1;
    						}
    						if (unit_dist_x == -1) {
    							resource_east = 1;
    						}
    						if (unit_dist_y == 1) {
    							resource_north = 1;
    						}
    						if (unit_dist_y == -1) {
    							resource_south = 1;
    						}
    					}
    				}
    				
    				int added_cost = 0;
					//System.out.println("North East South West Check: " + resource_north + " " + resource_east + " " + resource_south + " " + resource_west);
					//System.out.println("Current unit location: " + current_unit.x_loc + " " + current_unit.y_loc);
    				//System.out.println("Attempting to move unit: " + current_unit.unit_ID + " direction: " + Math.floorMod(action_num, valid_actions.length));
    				
    				if (Math.floorMod(action_num, valid_actions.length) == 0) {
    					if (current_unit.y_loc < parent_state.yExtent && resource_north == 0) {
	    					//current_unit.y_loc += 1;
    						moving_units_temp.get(index2).y_loc -= 1;
    						
	    					child_node_action.put(current_unit.unit_ID, Action.createPrimitiveMove(current_unit.unit_ID, Direction.NORTH));
	    					
	    					added_cost += 1;
    					}
    				}
    				if (Math.floorMod(action_num, valid_actions.length) == 1) {
    					if (current_unit.x_loc < parent_state.xExtent && resource_east == 0) {
    						//current_unit.x_loc += 1;
    						moving_units_temp.get(index2).x_loc += 1;
    						child_node_action.put(current_unit.unit_ID, Action.createPrimitiveMove(current_unit.unit_ID, Direction.EAST));
    						
    						added_cost += 1;
    					}
    				}
    				if (Math.floorMod(action_num, valid_actions.length) == 2) {
    					if (current_unit.y_loc != 0 && resource_south == 0) {
    						//current_unit.y_loc -= 1;
    						moving_units_temp.get(index2).y_loc += 1;
    						child_node_action.put(current_unit.unit_ID, Action.createPrimitiveMove(current_unit.unit_ID, Direction.SOUTH));
    						
    						added_cost += 1;
    					}
    				}
    				if (Math.floorMod(action_num, valid_actions.length) == 3) {
    					if (current_unit.x_loc != 0 && resource_west == 0) {
    						//current_unit.x_loc -= 1;
    						moving_units_temp.get(index2).x_loc -= 1;
    						child_node_action.put(current_unit.unit_ID, Action.createPrimitiveMove(current_unit.unit_ID, Direction.WEST));
    						
    						added_cost += 1;
    					}
    				}
    				if (Math.floorMod(action_num, valid_actions.length) == 4) {
    					int closest_unit_index = 0;
    					int closest_distance = 999999;
    					for (int index3 = 0; index3 < static_units.size(); index3 ++) {
    						int closest_distance_temp = Math.abs(static_units.get(index3).x_loc-current_unit.x_loc) + Math.abs(static_units.get(index3).y_loc-current_unit.y_loc);
    						if (closest_distance_temp < closest_distance) {
    							closest_distance = closest_distance_temp;
    							closest_unit_index = index3;
    						}
    						if (current_unit.range == 1) {
    							if (closest_distance_temp == 1) {
    								if (static_units.get(closest_unit_index).health > 0) {
    									child_node_action.put(current_unit.unit_ID, Action.createPrimitiveAttack(current_unit.unit_ID, static_units.get(closest_unit_index).unit_ID));
    									static_units_temp.get(closest_unit_index).health -= current_unit.damage;
    								}
    							}
    						}
    						if (current_unit.range > 1) {
    							if (closest_distance_temp <= current_unit.range) {
    								if (static_units.get(closest_unit_index).health > 0) {
	    								child_node_action.put(current_unit.unit_ID, Action.createPrimitiveAttack(current_unit.unit_ID, static_units.get(closest_unit_index).unit_ID));
	    								static_units_temp.get(closest_unit_index).health -= current_unit.damage;
    								}
    							}
    						}
    					}
    				}
    				//first child state should have been created already here:
    				action_num /= valid_actions.length;
    				
    				
    				
    				if (child_node_action.size() == moving_units.size()) {
    					GameState temp_state;
	    				int new_path_cost = parent_state.path_cost + added_cost;
	    				
	    				//System.out.println(" ");
	    			//	for(int a = 0; a < moving_units_temp.size(); a ++) {
	    				//	System.out.println("temp moving units final loc: " + moving_units_temp.get(a).x_loc + " " + moving_units_temp.get(a).y_loc);
	    			//	}
	    			//	System.out.println(" ");
	    				
	    				List<Unit_info> moving_units_temp1 = new ArrayList<Unit_info>(moving_units_temp);
	    				List<Unit_info> static_units_temp1 = new ArrayList<Unit_info>(static_units_temp);
	    				if (status == 1) {
	    					temp_state = new GameState(parent_state.xExtent, parent_state.yExtent, parent_state.resource_loc, parent_state.enemy_ID,moving_units_temp1,static_units_temp1,new_path_cost);
	    		    	}
	    				else {
	    		    		temp_state = new GameState(parent_state.xExtent, parent_state.yExtent, parent_state.resource_loc, parent_state.enemy_ID,static_units_temp1,moving_units_temp1,new_path_cost);
	    		    	}
	    		    	GameStateChild temp_child_state = new GameStateChild (child_node_action, temp_state);
	    		    	
	    		    	//System.out.println("North East South West Check: " + resource_north + " " + resource_east + " " + resource_south + " " + resource_west);
	    		    	//System.out.println("Generating child node: " + child_node_action);
	    		    	//System.out.println(" ");
	    		    	
	    		    	children_nodes.add(temp_child_state);
	    		    	
	    		    	moving_units_temp.clear();
	    		    	static_units_temp.clear();
    				}
    			}
    		}
    		
    	}
      //  for (int index = 0; index < children_nodes.size(); index ++) {
     //   	System.out.println("Children node with " + children_nodes.get(index).state.My_Units.size() + " " + children_nodes.get(index).state.Enemy_Units.size() + " units");
     //   }
        return children_nodes;
    }
}