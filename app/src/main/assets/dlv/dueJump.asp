numArchs(0..34).
maxMenNum(0..12).
arch(33,O,ID,none,0) :- rootNode(O,ID,_).
idRootNodes(I) :- rootNode(_,I,_).

inPath(O,A,B,ID,S,N) | notInPath(O,A,B,ID,S,N) :- arch(O,A,ID,_,_), arch(A,B,ID,S,N), A<>B, A<>O, O<>B.

:- not #count{O,A,B : inPath(O,A,B,ID,_,N)} = 1,inPath(_,_,_,ID,_,N).%unicità
:- inPath(_,A,B,ID,_,N), inPath(A,B,_,ID,_,N2), N1=N+1,N2<>N1.%consecutività
:- inPath(33,A,B,ID,_,N),N<>1. %inizio

parent(A,B,ID) :- inPath(A,B,_,ID,_,_).
children(A,B,ID) :- inPath(_,A,B,ID,_,_).

:- inPath(A,B,_,ID,_,_), not children(A,B,ID), A<>33, B<>O, rootNode(O,ID,_).
:- inPath(A,B,C,ID,_,N), inPath(A,B,C,ID,_,N1), N<>N1.
:- inPath(A,B,C,ID,_,_), inPath(A,B,D,ID,_,_), C<>D.
:- inPath(A,B,_,ID,_,_), inPath(_,B,A,ID,_,_).
:- inPath(_,B,C,ID,_,_), inPath(_,C,B,ID,_,_).
:- inPath(A,B,C,ID,_,_), inPath(D,B,C,ID,_,_), D<>A.
:- not parent(33,O,ID), rootNode(O,ID,_), inPath(O,_,_,ID,_,_).
:- inPath(_,_,_,ID,_,_), inPath(_,_,_,ID2,_,_), ID<>ID2.

:~ notInPath(O,A,B,ID,_,_).[1:4]
:~inPath(33, O, _, ID,_,_), rootNode(O,_,man).[1:3]
menInPath(ID,N) :- idRootNodes(ID), N = #count{O,A,B : inPath(O,A,B,ID,man,_)}, maxMenNum(N).
:~menInPath(I,N).[N:2]
:~ inPath(O,A,B,ID,king,N).[N:1]
