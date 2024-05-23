// Agent bob in project kokanyek

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").





    <- !cleanHouse

+!cleanHouse : true
    <- !goNextTile

+!goNextTile : not bagFull &
    <- !check
    !goNextTile

+bagFull : true
    <- sendSignalToHuman.

+bagEmptied : true
    <- -bagFull;
    !cleanHouse;
    .print("bababa")