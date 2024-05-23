// Agent bob in project kokanyek

/* Initial beliefs and rules */
pos(docker,13, 8).
-bagFull.
-busy.
/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Hi, I am the vacuum cleaner!.").

+morning : not busy
    <- .print("Good Morning");
    ready.

+night : not busy
    <- .print("Good Night!");
    !go(docker);
    docked.

+needToClean(X1, Y1) : not bagFull
    <- .print("I am going to clean a dirt");
    +busy;
    !goToCord(X1, Y1);
    cleanDirtyFloor.

+bagFull : true
    <- .print("BagFull");
    .send(human, achieve, emptyVacuumBag);
    .print("I need to be cleaned!").

+!emptyBag : not night
    <- .print("I am now empty");
    -bagFull;
    !go(docker);
    ready;
    -busy.

+emptyBag : night
    <- .print("I am now empty");
    !go(docker);
    docked;
    -busy.

+goHome : not night
    <- .print("I am going Back");
    !go(docker);
    ready.

+goHome : night
    <- .print("I am going Back");
    !go(docker);
    docked;
    -busy.

+!go(L) : pos(L, X1, Y1) & pos(vacuum,X1,Y1)
    <- true.

+!go(L)
    <- ?pos(L,X,Y);
       vacuum_move_towards(X,Y);
       !go(L).

+!goToCord(X1, Y1) : pos(vacuum,X1,Y1)
    <- true.

+!goToCord(X1, Y1)
    <- vacuum_move_towards(X1,Y1);
       !goToCord(X1, Y1).



