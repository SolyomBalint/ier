public class Vacuum {

	public static final int startingSpace = 30;
	public static final int startingCharge = 50;
	public static final int SpaceLimit = 15;
	public static final int ChargeLimit = 25;
	public static final int CleaningSpaceCost = 5;
	
	public static final int CleaningChargeCost = 5;
	public static final int MovingChargeCost = 1;
	
	String name;
	public boolean isInside;
	int power;
	int space;
	
	Vacuum(String name) {
		this.name = name;
		power = startingCharge;
		space = startingSpace;
		isInside = false;	
	}
	
	void recharge() { power = startingCharge; }
	
	void empty() { space = startingSpace; }
	
	void reducePower(int cost) throws Exception {
		if (power - cost < 0)
		{
			throw new Exception("Agent " + name + " is dead");
		}
		power -= cost;
	}
	
	void reduceSpace(int cost) throws Exception {
		if (space - cost < 0)
		{
			throw new Exception("Agent" + name + " is full");
		}
		space -= cost;
	}
	
}