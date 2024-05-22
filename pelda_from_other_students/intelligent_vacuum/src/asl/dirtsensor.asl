// Agent dirtsensor in project intelligent_vacuum

/* Initial beliefs and rules */



/* Initial goals */

/* Plans */

+dirt_generated(Position) : true <- !try_to_find_vacuum(Position).

+!try_to_find_vacuum(Position) : free(Vacuum) & not free(Vacuum2) & not free(Vacuum3) & 
								dirt_generated(Position2) & time_to_clean(Position, Time1) & 
								time_to_clean(Position2, Time2) & Time1 > Time2
 <- task_cleaning(Vacuum, Position2);
    -dirt_generated(Position2);
    !try_to_find_vacuum(Position).
    
+!try_to_find_vacuum(Position) : free(Vacuum) & free(Vacuum2) & free(Vacuum3) & 
								distance(Position, Vacuum, d1) & distance(Position, Vacuum2, d2) & 
								distance(Position, Vacuum3, d3) & d1 < d2 & d1 < d3
 <- task_cleaning(Vacuum, Position);
    -dirt_generated(Position).

+!try_to_find_vacuum(Position) : free(Vacuum) & free(Vacuum2) & 
								distance(Position, Vacuum, d1) & 
								distance(Position, Vacuum2, d2) & d1 < d2
 <- task_cleaning(Vacuum, Position);
    -dirt_generated(Position).

+!try_to_find_vacuum(Position) : free(Vacuum)
 <- task_cleaning(Vacuum, Position);
    -dirt_generated(Position).

+!try_to_find_vacuum(Position) : true
 <- !try_to_find_vacuum(Position).