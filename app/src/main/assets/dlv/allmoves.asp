row(0..7).
col(0..7).
color(white).
color(black).

busyTile(R,C) :- pawn(R,C,_,_,_).
emptyTile(R,C) :- not busyTile(R,C), row(R), col(C), pawn(_,_,_,_,_).

myPawn(R,C,S) :- pawn(R,C,Color,S,_), userColor(Color).
whiteLegalMove(R,C,R1,C1) :- pawn(R,C,white,_,_), R1 = R-1, C1 = C-1, R1>=0, C1>=0, emptyTile(R1,C1).
whiteLegalMove(R,C,R1,C1) :- pawn(R,C,white,_,_), R1 = R-1, C1 = C+1, R1>=0, C1<=7, emptyTile(R1,C1).
whiteLegalMove(R,C,R1,C1) :- pawn(R,C,white,king,_), R1 = R+1, C1 = C-1, R1<=7, C1>=0, emptyTile(R1,C1).
whiteLegalMove(R,C,R1,C1) :- pawn(R,C,white,king,_), R1 = R+1, C1 = C+1, R1<=7, C1<=7, emptyTile(R1,C1).

blackLegalMove(R,C,R1,C1) :- pawn(R,C,black,_,_), R1 = R+1, C1 = C-1, R1<=7, C1>=0, emptyTile(R1,C1).
blackLegalMove(R,C,R1,C1) :- pawn(R,C,black,_,_), R1 = R+1, C1 = C+1, R1<=7, C1<=7, emptyTile(R1,C1).
blackLegalMove(R,C,R1,C1) :- pawn(R,C,black,king,_), R1 = R-1, C1 = C-1, R1>=0, C1>=0, emptyTile(R1,C1).
blackLegalMove(R,C,R1,C1) :- pawn(R,C,black,king,_), R1 = R-1, C1 = C+1, R1>=0, C1<=7, emptyTile(R1,C1).

myLegalMove(R,C,R1,C1) :- userColor(white), myPawn(R,C,S), whiteLegalMove(R,C,R1,C1).
myLegalMove(R,C,R1,C1) :- userColor(black), myPawn(R,C,S), blackLegalMove(R,C,R1,C1).

move(R,C,R1,C1) | notMove(R,C,R1,C1) :- myLegalMove(R,C,R1,C1).
:-not #count{R,C,R1,C1: move(R,C,R1,C1)} = 1.
