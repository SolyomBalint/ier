// Agent vacuum in project intelligent_vacuum

/* Initial beliefs and rules */

pos(station,0,0).


/* Initial goals */

/* Plans */

+told_to_vacuum(Vacuum, Place) : pos(Place, X1, Y1) & isSelf(Vacuum)
 <- set_resource_limits(Vacuum, X1, Y1);
    !go(Place);
    !check_resources(Vacuum).

+dirt(Vacuum) : is_allowed_to_clean(Vacuum)
 <- ?pos(Vacuum, X1, Y1);
    clean(X1, Y1);
    !check_resources(Vacuum).


+!check_resources(Vacuum) : has_charge(Charge, Vacuum)  & needs_charge(Charge_limit, Vacuum) & Charge < Charge_limit & isSelf(Vacuum)
 <- !go(station);
    recharge(Vacuum);
    !check_resources(Vacuum).
    
+!check_resources(Vacuum) :  has_space(Space, Vacuum) & needs_space(Space_limit, Vacuum) & Space < Space_limit & isSelf(Vacuum)
 <- !go(station);
    empty(Vacuum);
    !check_resources(Vacuum).

+!check_resources(Vacuum) :  true
 <- done(Vacuum).
    
+!check_resources(Vacuum) : true
	<- true.

+!go(Position) : pos(Position,Xl,Yl) & pos(Vacuum,Xl,Yl) & isSelf(Vacuum) & not isSelf(Position)
    <- true.

+!go(Position) : true 
    <- ?pos(Position,Xl,Yl);
    moveTowards(Xl,Yl);
    !go(Position).