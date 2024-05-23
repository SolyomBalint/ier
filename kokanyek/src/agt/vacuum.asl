// Agent bob in project kokanyek

/* Initial beliefs and rules */
pos(docker,12, 2).
/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Hi, I am th vacuum cleaner!.").

+bagFull : true
    <- .send(human, tell, emptyVacuumBag);
    .print("I need to be cleaned!").

+emptyBag : true
    <- .print("I am now empty");
    -bagFull.
