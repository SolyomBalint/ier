// Agent bob in project kokanyek

/* Initial beliefs and rules */
pos(table,2,8).
pos(work,13,13).
pos(sofa,10,10).
pos(bed,2,2).
pos(outside, 14, 14).
pos(vacuum,9, 3).

/* Initial goals */
!start.

/* Plans */
+!start : true <- .print("Hi everyone!").

+morning : not alarm & not vacuuming
    <- .print("Good Morning, I am going to eat my breakfast");
       !go(table);
       .print("I am at the table now!");
       eat.

+duringDay : not alarm & not vacuuming
    <- .print("I am going to work now!");
       !go(work);
       .print("I am at my working spot");
       work.

+evening : not alarm & not vacuuming
    <- .print("I am going to go home and eat dinner!");
       !go(table);
       .print("I am at the table now!");
       eat.

+lateEvening : not alarm & not vacuuming
    <- .print("I am going to go sit down and read!");
       !go(sofa);
       .print("I am at the sofa now!");
       read.

+night : not alarm & not vacuuming
    <- .print("I am going to sleep!");
       !go(bed);
       .print("I am at my bed now!");
       sleep.

@lg[atomic]
+emptyVacuumBag : not alarm
    <- .print("I am going to clean the vacuum!");
       !go(vacuum);
       .print("I am at the vacuum now!");
       cleanVacuum;
       .send(vacuum, tell, emptyBag).


@atomic
+alarm : humanInTheHouse
    <- .print("I am going to go outside, because there is an emergency!");
       !go(outside);
       .print("I am outside now!");
       .print("Calling 911");
       call(911).

-alarm : true
    <- .print("No longer Emergency!");
    stopCall.

+!go(L) : pos(L, X1, Y1) & pos(human,X1,Y1)
    <- true.

+!go(L)
    <- ?pos(L,X,Y);
       human_move_towards(X,Y);
       !go(L).
