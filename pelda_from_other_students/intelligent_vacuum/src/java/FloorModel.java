import java.util.Random;
import jason.environment.grid.GridWorldModel;
import java.util.ArrayList;
import jason.environment.grid.Location;


class FloorModel extends GridWorldModel {
    
    Vacuum vacuum_B = new Vacuum("vacuum_B");
    Vacuum vacuum_A = new Vacuum("vacuum_A");
    Vacuum vacuum_C = new Vacuum("vacuum_C");
    
    FloorModel() {
        super(7, 7, 3);
        try {
            setAgPos(0, 0, 0);
            setAgPos(1, 1, 0);
            setAgPos(2, 2, 0);
            addWall(2, 1, 4, 1);
            addWall(0, 1, 0, 1);
            addWall(6, 1, 6, 1);
            addWall(3, 1, 3, 6);
            add(8, 0, 0); //állomás
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(16, 2, 4); //alap kosz
    }

    public void addDirt(int x, int y){
    	add(16, x, y);
    }
    
    public void empty(String vacuum_name) {
    	switch(vacuum_name) {
		case "vacuum_B":
			vacuum_B.empty();
			break;
		case "vacuum_A":
			vacuum_A.empty();
			break;
		case "vacuum_C":
			vacuum_C.empty();
			break;		
		}	
	}

	public void recharge(String vacuum_name) {
		switch(vacuum_name) {
		case "vacuum_B":
			vacuum_B.recharge();
			break;
		case "vacuum_A":
			vacuum_A.recharge();
			break;
		case "vacuum_C":
			vacuum_C.recharge();
			break;		
		}	
	}

	public boolean set_resource_limits(String vacuum_name, int x, int y, int space) {
		Location location = new Location(0,0);
		switch(vacuum_name) {
		case "vacuum_B":
    		location = getAgPos(0);
			break;
		case "vacuum_A":
    		location = getAgPos(1);
			break;
		case "vacuum_C":
    		location = getAgPos(2);
			break;		
		}
    	if(find_distance_and_path_to_dirt(location.x, location.y, x, y).get(2) + find_distance_and_path_to_dirt(x, y, 0, 0).get(2) >= space - 5) {
    		return false;
    	}
    	return true;
		
	}

	public void clean(String vacuum_name, int x, int y) throws Exception {
		
		switch(vacuum_name) {
		case "vacuum_B":
			vacuum_B.reducePower(Vacuum.CleaningChargeCost);
			vacuum_B.reduceSpace(Vacuum.CleaningSpaceCost);
			break;
		case "vacuum_A":
			vacuum_A.reducePower(Vacuum.CleaningChargeCost);
			vacuum_A.reduceSpace(Vacuum.CleaningSpaceCost);
			break;
		case "vacuum_C":
			vacuum_C.reducePower(Vacuum.CleaningChargeCost);
			vacuum_C.reduceSpace(Vacuum.CleaningSpaceCost);
			break;		
		}
		remove(16, x, y);
    }
    
    public void move_closer_to(String vacuum_name, int x, int y) {
    	Location location = new Location(0,0);
    	int agent_no = 0;
    	switch(vacuum_name) {
		case "vacuum_B":
    		location = getAgPos(0);
			break;
		case "vacuum_A":
    		location = getAgPos(1);
    		agent_no = 1;
			break;
		case "vacuum_C":
    		location = getAgPos(2);
    		agent_no = 2;
			break;		
		}
    	ArrayList<Integer> path = find_distance_and_path_to_dirt(location.x, location.y, x, y);
    	location.x += path.get(0);
    	location.y += path.get(1);
    	setAgPos(agent_no, location);
    }

    ArrayList<Integer> find_distance_and_path_to_dirt(int x1, int y1, int x2, int y2){
    	int first_is_inside = is_inside(x1, y1);
    	int second_is_inside = is_inside(x2, y2);
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	if(first_is_inside == -1 && second_is_inside == -1){
    		list.add(x2 - x1);
    		list.add(y2 - y1);
    		list.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    	} 
    	else if(first_is_inside == 1 && second_is_inside == 1){
    		if(x1 <= 2 && x2 <= 2){
    			list.add(x2 - x1);
	    		list.add(y2 - y1);
	    		list.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    		}
    		else if(x1 > 2 && x2 > 2){
    			list.add(x2 - x1);
	    		list.add(y2 - y1);
	    		list.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    		}
    		else if(x1 <= 2){
    			list.add(1 - x1);
		    	list.add(1 - y1);
		    	list.add(Math.abs(x1 - 1) + Math.abs(y1 - 1));
    		}
    		else{
    			list.add(5 - x1);
		    	list.add(1 - y1);
		    	list.add(Math.abs(x1 - 5) + Math.abs(y1 - 1));
    		}
    	}
    	else if(first_is_inside == 1){
    		if(second_is_inside == 0){
    			if(x1 <= 2 && x2 == 1){
    				list.add(x2 - x1);
		    		list.add(y2 - y1);
		    		list.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    			}
    			if(x1 <= 2){
    				list.add(1 - x1);
		    		list.add(1 - y1);
		    		list.add(Math.abs(x1 - 1) + Math.abs(y1 - 1) + 6);
    			}
    			if(x2 == 5){
    				list.add(x2 - x1);
		    		list.add(y2 - y1);
		    		list.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    			}
    			list.add(5 - x1);
		    	list.add(1 - y1);
		    	list.add(Math.abs(x1 - 5) + Math.abs(y1 - 1) + 6);
    		}
    		else{
    			if(x1 <= 2){
    				list.add(1 - x1);
    				list.add(1 - y1);
    				list.add(Math.abs(1 - y1) + Math.abs(x1 - 1));
    			}
    			else {
    				list.add(5 - x1);
    				list.add(1 - y1);
    				list.add(Math.abs(1 - y1) + Math.abs(x1 - 5));
    			}
    		}
    	}
    	else if(first_is_inside == -1){
    		if(second_is_inside == 0){
    			if(x2 <= 2){
    				list.add(1 - x1);
		    		list.add(1);
		    		list.add(Math.abs(x1 - x2) + 1);
    			} 
    			else{
	    			list.add(5 - x1);
			    	list.add(1);
			    	list.add(Math.abs(x1 - 5) + 1);
		   		}
    		}
    		else{
    			if(x2 <= 2){
    				list.add(1 - x1);
		    		list.add(1);
		    		list.add(Math.abs(x1 - x2) + 1);
    			}
    			else {
    				list.add(5 - x1);
			    	list.add(1);
			    	list.add(Math.abs(x1 - 5) + 1);
    			}
    		}
    	}
    	else if(first_is_inside == 0){
    		if(second_is_inside == 1){
    			if(x1 <= 2 && x2 <= 2){
    				list.add(0);
			    	list.add(1);
			    	list.add(6);
    			} 
    			else if(x1 <= 2 && x2 > 2){
		    		list.add(0);
			    	list.add(-1);
			    	list.add(6);
    			}
    			else if(x1 > 2 && x2 <= 2){
    				list.add(0);
			    	list.add(-1);
			    	list.add(6);
    			}
    			else{
    				list.add(0);
			    	list.add(1);
			    	list.add(6);
    			}
    		}
    		else{
	    		list.add(0);
		    	list.add(-1);
		    	list.add(6);
    		}
    	}
    	else{
	    	list.add(0);
	    	list.add(-1);
	    	list.add(6);
    	}
    	return list;
    }

    int is_inside(int x, int y){
    	if(y == 0) {
    		return -1;
    	}
    	if(y == 1) {
    		return 0;
    	}
    	return 1;
    }
}