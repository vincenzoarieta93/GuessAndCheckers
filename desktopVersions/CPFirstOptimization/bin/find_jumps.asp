row(0..7).
col(0..7).

status(king).
status(man).

busyTile(R,C) :- pawn(R,C,_,_,_).
emptyTile(R,C) :- not busyTile(R,C), row(R), col(C).

enemy(R, C) :- pawn(R, C, EC, _, _), userColor(UC), EC <> UC.
myPawn(R, C, ID) :- pawn(R, C, Color, _, ID), userColor(Color).

whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R-1, EC = C-1, TR = ER - 1, TC = EC -1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R-1, EC = C+1, TR = ER - 1, TC = EC +1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R+1, EC = C+1, TR = ER+1,TC = EC+1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R+1, EC = C-1, TR = ER+1,TC = EC-1, col(TC), row(TR).

blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R+1, EC = C+1, TR = ER+1,TC = EC+1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R+1, EC = C-1, TR = ER+1,TC = EC-1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R-1, EC = C-1, TR = ER - 1, TC = EC -1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R-1, EC = C+1, TR = ER - 1, TC = EC +1, col(TC), row(TR).

origin(R,C,TR,TC,ID,S,ES) :- userColor(white), pawn(R,C,white,S,ID), whiteJumper(R,C,TR,TC,S,ES).
origin(R,C,TR,TC,ID,S,ES) :- userColor(black), pawn(R,C,black,S,ID), blackJumper(R,C,TR,TC,S,ES).

jump(R,C,R1,C1,ID,S,ES) :- origin(R,C,R1,C1,ID,S,ES).
jump(R,C,R1,C1,ID,S,ES) :- jump(_,_,R,C,ID,S,_), userColor(white), whiteJumper(R,C,R1,C1,S,ES).
jump(R,C,R1,C1,ID,S,ES) :- jump(_,_,R,C,ID,S,_), userColor(black), blackJumper(R,C,R1,C1,S,ES).
jump(R,C,R1,C1,ID,S,ES) :- origin(R1,C1,R,C,ID,S,ES), S=king.
