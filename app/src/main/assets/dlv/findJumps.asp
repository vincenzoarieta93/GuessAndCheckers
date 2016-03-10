row(0..7).
col(0..7).
#maxint = 24.

status(king).
status(man).

busyTile(R,C) :- pawn(R,C,_,_,_).
emptyTile(R,C) :- not busyTile(R,C), row(R), col(C).

enemy(R,C) :- pawn(R,C,EC,_,_), userColor(UC), EC <> UC.
myPawn(R, C, ID) :- pawn(R,C,Color,_,ID), userColor(Color).

whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R-1, EC = C-1, TR = ER - 1, TC = EC -1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R-1, EC = C+1, TR = ER - 1, TC = EC +1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R+1, EC = C+1, TR = ER+1,TC = EC+1, col(TC), row(TR).
whiteJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,black,ES,_), emptyTile(TR, TC), ER = R+1, EC = C-1, TR = ER+1,TC = EC-1, col(TC), row(TR).

blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R+1, EC = C+1, TR = ER+1,TC = EC+1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R+1, EC = C-1, TR = ER+1,TC = EC-1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R-1, EC = C-1, TR = ER - 1, TC = EC -1, col(TC), row(TR).
blackJumper(R,C,TR,TC,S,ES) :- row(R), col(C), status(S), S=king, pawn(ER,EC,white,ES,_), emptyTile(TR, TC), ER = R-1, EC = C+1, TR = ER - 1, TC = EC +1, col(TC), row(TR).

origin(R,C,TR,TC,ID,S,ES,N) :- userColor(white), pawn(R,C,white,S,ID), whiteJumper(R,C,TR,TC,S,ES),N=0.
origin(R,C,TR,TC,ID,S,ES,N) :- userColor(black), pawn(R,C,black,S,ID), blackJumper(R,C,TR,TC,S,ES),N=0.

jump(R,C,R1,C1,ID,S,ES,N) :- origin(R,C,R1,C1,ID,S,ES,ON),N=ON+1.
jump(R,C,R1,C1,ID,S,ES,N2) :- jump(_,_,R,C,ID,S,_,N), userColor(white), whiteJumper(R,C,R1,C1,S,ES),N2=N+1, N2<=NP, numPawns(NP).
jump(R,C,R1,C1,ID,S,ES,N2) :- jump(_,_,R,C,ID,S,_,N), userColor(black), blackJumper(R,C,R1,C1,S,ES),N2=N+1, N2<=NP, numPawns(NP).
jump(R,C,R1,C1,ID,S,ES,N2) :- origin(R1,C1,R,C,ID,S,ES,N), jump(RX,CX,R,C,ID,S,ES,NX), N2=NX+1,S=king, N2<=NP, numPawns(NP).
