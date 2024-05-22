import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Floor extends Environment{

	Integer waitTime = 350;
	Integer maxCharge = Vacuum.startingCharge;
	Integer maxSpace = Vacuum.startingSpace;
	Integer dirt_pieceSpace = Vacuum.CleaningSpaceCost;
	
    public static final Term clean = Literal.parseLiteral("clean(x1, y1)");
    public static final Term recharge = Literal.parseLiteral("recharge(vacuum)");
    public static final Term empty = Literal.parseLiteral("empty(vacuum)");
    static Logger logger = Logger.getLogger(Floor.class.getName());
    FloorModel model;
    FloorView  view;
    ArrayList<Location> dirt_stains = new ArrayList<Location>();
    ArrayList<Boolean> dirtValidity = new ArrayList<Boolean>();
    Integer vacuum_BCharge = maxCharge;
    Integer vacuum_ACharge = maxCharge;
    Integer vacuum_CCharge = maxCharge;
    Integer vacuum_BSpace = maxSpace;
    Integer vacuum_ASpace = maxSpace;
    Integer vacuum_CSpace = maxSpace;
    Integer vacuum_BOldCharge;
    Integer vacuum_AOldCharge;
    Integer vacuum_COldCharge;
    Integer vacuum_BOldSpace;
    Integer vacuum_AOldSpace;
    Integer vacuum_COldSpace;
    private Location vacuum_BPrevious;
    private Location vacuum_APrevious;
    private Location vacuum_CPrevious;
   
    @Override
    public void init(String[] args) {
        model = new FloorModel();
        vacuum_BPrevious = model.getAgPos(0);
        vacuum_APrevious = model.getAgPos(1);
        vacuum_CPrevious = model.getAgPos(2);   
        addPercept("vacuum_B", Literal.parseLiteral("isSelf(vacuum_B)"));
        addPercept("vacuum_A", Literal.parseLiteral("isSelf(vacuum_A)"));
        addPercept("vacuum_C", Literal.parseLiteral("isSelf(vacuum_C)"));
        addPercept("vacuum_B", Literal.parseLiteral("needs_charge(" + Vacuum.ChargeLimit + ", vacuum_B)"));
        addPercept("vacuum_A", Literal.parseLiteral("needs_charge(" + Vacuum.ChargeLimit + ", vacuum_A)"));
        addPercept("vacuum_C", Literal.parseLiteral("needs_charge(" + Vacuum.ChargeLimit + ", vacuum_C)"));
        addPercept("vacuum_B", Literal.parseLiteral("needs_space(" + Vacuum.SpaceLimit + ", vacuum_B)"));
        addPercept("vacuum_A", Literal.parseLiteral("needs_space(" + Vacuum.SpaceLimit + ", vacuum_A)"));
        addPercept("vacuum_C", Literal.parseLiteral("needs_space(" + Vacuum.SpaceLimit + ", vacuum_C)"));
        addPercept("dirtsensor", Literal.parseLiteral("free(vacuum_B)"));
        addPercept("dirtsensor", Literal.parseLiteral("free(vacuum_A)"));
        addPercept("dirtsensor", Literal.parseLiteral("free(vacuum_C)"));
        dirt_stains.add(new Location(2,4));
        dirtValidity.add(true);
        view  = new FloorView(model);
		view.addClickListener(this);
        model.setView(view);
        addPercept("dirtsensor", Literal.parseLiteral("dirt_generated(dirt1)"));
        addPercept(Literal.parseLiteral("pos(dirt1, 2, 4)"));
        update_world();
    }
    
    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info("Agent " + ag + " is " + action);
        try {
        	switch(action.getFunctor()) {
        	case "moveTowards":
                model.move_closer_to(ag,(int)((NumberTerm)action.getTerm(0)).solve(),(int)((NumberTerm)action.getTerm(1)).solve());
                int agent;
                switch(ag) {
                case "vacuum_A":
                    agent = 1;
                	break;
                case "vacuum_C":
                    agent = 2;
                break;
                default:
                    agent = 0;
                	break;
                }
                Location loc = model.getAgPos(agent);
                int fuelCost = model.find_distance_and_path_to_dirt(loc.x, loc.y, (int)((NumberTerm)action.getTerm(0)).solve(), (int)((NumberTerm)action.getTerm(1)).solve()).get(2);
                switch(ag) {
                case "vacuum_A":
                    vacuum_ACharge -= fuelCost;
                	break;
                case "vacuum_C":
                    vacuum_CCharge -= fuelCost;
                break;
                default:
                    vacuum_BCharge -= fuelCost;
                	break;
                }
        		break;
        	case "clean":
                model.clean(ag,(int)((NumberTerm)action.getTerm(0)).solve(),(int)((NumberTerm)action.getTerm(1)).solve());
                dirtValidity.set(dirt_stains.indexOf(new Location((int)((NumberTerm)action.getTerm(0)).solve(), (int)((NumberTerm)action.getTerm(1)).solve())), false);
                switch(ag) {
                case "vacuum_A":
                    vacuum_ASpace -= dirt_pieceSpace;
                	break;
                case "vacuum_C":
                    vacuum_CSpace -= dirt_pieceSpace;
                break;
                default:
                    vacuum_BSpace -= dirt_pieceSpace;
                	break;
                }
        		break;
        	case "set_resource_limits":
                int fuel;
                if(ag.equals("vacuum_B")){
                    fuel = vacuum_BCharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_B)");
                    if(model.set_resource_limits(ag,(int)((NumberTerm)action.getTerm(1)).solve(),(int)((NumberTerm)action.getTerm(2)).solve(), fuel)){
                        addPercept("vacuum_A", allowance);
                    }
                    else{
                        removePercept("vacuum_B", allowance);
                    }
                }
                if(ag.equals("vacuum_A")){
                    fuel = vacuum_ACharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_A)");
                    if(model.set_resource_limits(ag,(int)((NumberTerm)action.getTerm(1)).solve(),(int)((NumberTerm)action.getTerm(2)).solve(), fuel)){
                        addPercept("vacuum_A", allowance);
                    }
                    else{
                        removePercept("vacuum_A", allowance);
                    }
                }
                if(ag.equals("vacuum_C")){
                    fuel = vacuum_CCharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_C)");
                    if(model.set_resource_limits(ag,(int)((NumberTerm)action.getTerm(1)).solve(),(int)((NumberTerm)action.getTerm(2)).solve(), fuel)){
                        addPercept("vacuum_C", allowance);
                    }
                    else{
                        removePercept("vacuum_C", allowance);
                    }
                }
        		break;
        	case "recharge":
                model.recharge(ag);
                System.out.println(vacuum_BCharge);
                switch(ag) {
                case "vacuum_A":
                    vacuum_ACharge = maxCharge;
                	break;
                case "vacuum_C":
                    vacuum_CCharge = maxCharge;
                break;
                default:
                    vacuum_BCharge = maxCharge;
                	break;
                }
        		break;
        	case "empty":
                model.empty(ag);
                System.out.println("vacuum_BSpace " + vacuum_BSpace);
                switch(ag) {
                case "vacuum_A":
                    vacuum_ASpace = Vacuum.ChargeLimit;
                	break;
                case "vacuum_C":
                    vacuum_CSpace = Vacuum.ChargeLimit;
                break;
                default:
                    vacuum_BSpace = Vacuum.ChargeLimit;
                	break;
                }
        		break;
        	case "task_cleaning":
                String vacuum = action.getTerm(0).toString();
                String position = action.getTerm(1).toString();
                removePercept("dirtsensor", Literal.parseLiteral("dirt_generated(" + position + ")"));
                removePercept("dirtsensor", Literal.parseLiteral("free(" + vacuum + ")"));
                addPercept(vacuum, Literal.parseLiteral("told_to_vacuum(" + vacuum + ", " + position + ")"));
        		break;
        	case "done":
                Literal free = Literal.parseLiteral("free(" + action.getTerm(0).toString() + ")");
                addPercept("dirtsensor", free);
                break;
            default:
            	return false;
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        update_world();

        try {
            Thread.sleep(waitTime);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }
    
    void update_world() {
	    Location vacuum_ALoc = model.getAgPos(1);
	    Location vacuum_BLoc = model.getAgPos(0);
	    Location vacuum_CLoc = model.getAgPos(2);

        removePercept("vacuum_A", Literal.parseLiteral("pos(vacuum_A," + vacuum_APrevious.x + ", " + vacuum_APrevious.y + ")"));
        removePercept("vacuum_B", Literal.parseLiteral("pos(vacuum_B," + vacuum_BPrevious.x + ", " + vacuum_BPrevious.y + ")"));
        removePercept("vacuum_C", Literal.parseLiteral("pos(vacuum_C," + vacuum_CPrevious.x + ", " + vacuum_CPrevious.y + ")"));

        removePercept("vacuum_A", Literal.parseLiteral("has_charge(" + vacuum_AOldCharge + ", vacuum_A)"));
        removePercept("vacuum_B", Literal.parseLiteral("has_charge(" + vacuum_BOldCharge + ", vacuum_B)"));
        removePercept("vacuum_C", Literal.parseLiteral("has_charge(" + vacuum_COldCharge + ", vacuum_C)"));

        vacuum_AOldCharge = vacuum_ACharge;
        vacuum_BOldCharge = vacuum_BCharge;
        vacuum_COldCharge = vacuum_CCharge;

        addPercept("vacuum_A", Literal.parseLiteral("has_charge(" + vacuum_ACharge + ", vacuum_A)"));
        addPercept("vacuum_B", Literal.parseLiteral("has_charge(" + vacuum_BCharge + ", vacuum_B)"));
        addPercept("vacuum_C", Literal.parseLiteral("has_charge(" + vacuum_COldCharge + ", vacuum_C)"));

        removePercept("vacuum_A", Literal.parseLiteral("has_space(" + vacuum_AOldSpace + ", vacuum_A)"));
        removePercept("vacuum_B", Literal.parseLiteral("has_space(" + vacuum_BOldSpace + ", vacuum_B)"));
        removePercept("vacuum_C", Literal.parseLiteral("has_space(" + vacuum_COldSpace + ", vacuum_C)"));

        vacuum_AOldSpace = vacuum_ASpace;
        vacuum_BOldSpace = vacuum_BSpace;
        vacuum_COldSpace = vacuum_CSpace;

        addPercept("vacuum_A", Literal.parseLiteral("has_space(" + vacuum_AOldSpace + ", vacuum_A)"));
        addPercept("vacuum_B", Literal.parseLiteral("has_space(" + vacuum_BOldSpace + ", vacuum_B)"));
        addPercept("vacuum_C", Literal.parseLiteral("has_space(" + vacuum_COldSpace + ", vacuum_C)"));

        if(dirt_stains.contains(vacuum_ALoc) && !dirtValidity.get(dirt_stains.indexOf(vacuum_ALoc))) removePercept("vacuum_A", Literal.parseLiteral("dirt(vacuum_A)"));
        if(dirt_stains.contains(vacuum_BLoc) && !dirtValidity.get(dirt_stains.indexOf(vacuum_BLoc))) removePercept("vacuum_B", Literal.parseLiteral("dirt(vacuum_B)"));
        if(dirt_stains.contains(vacuum_CLoc) && !dirtValidity.get(dirt_stains.indexOf(vacuum_CLoc))) removePercept("vacuum_C", Literal.parseLiteral("dirt(vacuum_C)"));

        if(dirt_stains.contains(vacuum_ALoc) && dirtValidity.get(dirt_stains.indexOf(vacuum_ALoc))){
            addPercept("vacuum_A", Literal.parseLiteral("dirt(vacuum_A)"));
            addPercept("vacuum_A", Literal.parseLiteral("is_allowed_to_clean(vacuum_A)"));
        }
        if(dirt_stains.contains(vacuum_BLoc) && dirtValidity.get(dirt_stains.indexOf(vacuum_BLoc))){
            addPercept("vacuum_B", Literal.parseLiteral("dirt(vacuum_B)"));
            addPercept("vacuum_B", Literal.parseLiteral("is_allowed_to_clean(vacuum_B)"));
        }
        if(dirt_stains.contains(vacuum_CLoc) && dirtValidity.get(dirt_stains.indexOf(vacuum_CLoc))){
            addPercept("vacuum_C", Literal.parseLiteral("dirt(vacuum_C)"));
            addPercept("vacuum_C", Literal.parseLiteral("is_allowed_to_clean(vacuum_C)"));
        }

        vacuum_APrevious = vacuum_ALoc;
        vacuum_BPrevious = vacuum_BLoc;
        vacuum_CPrevious = vacuum_CLoc;    

        addPercept("vacuum_A",Literal.parseLiteral("pos(vacuum_A," + vacuum_ALoc.x + "," + vacuum_ALoc.y + ")"));
        addPercept("vacuum_B",Literal.parseLiteral("pos(vacuum_B," + vacuum_BLoc.x + "," + vacuum_BLoc.y + ")"));
        addPercept("vacuum_C",Literal.parseLiteral("pos(vacuum_C," + vacuum_CLoc.x + "," + vacuum_CLoc.y + ")"));

        model.setAgPos(1, vacuum_ALoc);
        model.setAgPos(0, vacuum_BLoc);
        model.setAgPos(2, vacuum_CLoc);

        informAgsEnvironmentChanged();

    }

    void add_garbage(int loc_x, int loc_y){
        dirt_stains.add(new Location(loc_x, loc_y));
        dirtValidity.add(true);
        model.addDirt(loc_x, loc_y);
        addPercept("dirtsensor", Literal.parseLiteral("dirt_generated(dirt" + dirt_stains.size() +")"));
        addPercept(Literal.parseLiteral("pos(dirt" + dirt_stains.size() + ", " + loc_x + ", " + loc_y + ")"));
    }
}