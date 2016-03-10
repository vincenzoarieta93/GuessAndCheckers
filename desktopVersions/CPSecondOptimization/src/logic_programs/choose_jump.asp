#maxint = 34.
arch(33,O,ID, none) :- rootNode(O,ID).
archsNumber(ID,N) :- arch(_,_,ID,_), N=#count{O, A, B : inPath(O,A,B, ID,_)}, #int(N).
counterArchsNumber(ID,N) :- #int(N), N<AN, archsNumber(ID,AN).
idRootNodes(I) :- rootNode(_,I).

inPath(O,A,B,ID,S) | notInPath(O,A,B,ID,S) :- arch(O,A,ID,_), arch(A,B,ID,S), A<>B, A<>O, O<>B.
orderedJumpStep(O,A,B,I,ID,S) | notOrderedJumpStep(O,A,B,I,ID,S) :- inPath(O,A,B,ID,S), counterArchsNumber(ID,I).

inOrderedJumpStep(O,A,B,ID) :- orderedJumpStep(O,A,B,_,ID,_).

:- not #count{O,A,B : orderedJumpStep(O,A,B,I,ID,_)} = 1, orderedJumpStep(_,_,_,I,ID,_).
:- inPath(O,A,B,ID,S), not inOrderedJumpStep(O,A,B,ID).
:- orderedJumpStep(_,A,B,I,ID,_), orderedJumpStep(A,B,_, I2,ID,_), I1=I+1,I2<>I1.

parent(A,B,ID) :- inPath(A,B,_,ID,_).
children(A,B,ID) :- inPath(_,A,B,ID,_).

:- inPath(A,B,_,ID,_), not children(A,B,ID), A<>33, B<>O, rootNode(O,ID).
:- inPath(A,B,C,ID,_), inPath(A,B,D,ID,_), C<>D.
:- inPath(A,B,_,ID,_), inPath(_,B,A,ID,_).
:- inPath(_,B,C,ID,_), inPath(_,C,B,ID,_).
:- inPath(A,B,C,ID,_), inPath(D,B,C,ID,_), D<>A.
:- inPath(_,_,_,ID,_), not parent(33, O,ID), rootNode(O, ID).
:- inPath(_,_,_,ID,_), inPath(_,_,_,ID2,_), ID<>ID2.

:~ notInPath(O,A,B,ID,_).[1:4]
:~inPath(33, O, _, ID,_), pawn(O, man).[1:3]
menInPath(I,N) :- idRootNodes(I), N = #count{O,A,B : inPath(O,A,B,I,man)}, #int(N).
:~menInPath(I,N).[N:2]
:~ orderedJumpStep(O,A,B,I,ID,king).[I:1]