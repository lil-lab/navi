turn :- S : turn:<a,t>
turn :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (turn:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
move :- S : move:<a,t>
move :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (move:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
walk :- S : move:<a,t>
walk :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (move:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
go :- S : move:<a,t>
go :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (move:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
forward :- NP : forward:dir
straight :- NP : forward:dir
back :- NP : back:dir
right :- NP : right:dir
left :- NP : left:dir
to :- AP/NP : (lambda $0:e (lambda $1:a (to:<a,<ps,t>> $1 $0)))
along :- AP/NP : (lambda $0:e (lambda $1:a (pre:<a,<ps,t>> $1 $0)))
passing :- AP/NP : (lambda $0:e (lambda $1:a (pass:<a,<ps,t>> $1 $0)))
into :- AP/NP : (lambda $0:e (lambda $1:a (to:<a,<ps,t>> $1 $0)))
the :- NP/N : io:<<e,t>,e>
a :- NP/N : a:<<e,t>,e>
an :- NP/N : a:<<e,t>,e>
far :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 frontdist:<ps,n>))))
end :- N : (lambda $0:e (end:<ps,<ps,t>> $0 (a:<<e,t>,e> hall:<ps,t>)))
end of :- N/NP : (lambda $0:e (lambda $1:e (end:<ps,<ps,t>> $1 $0)))
and :- AP/S : (lambda $0:t (lambda $1:a (post:<a,<t,t>> $1 $0)))
and :- S\S/S : (lambda $0:<a,t> (lambda $1:<a,t> (lambda $2:a[] (and:<t*,t> ($1 (i:<a[],<ind,a>> $2 0:ind)) (bef:<a,<a,t>> (i:<a[],<ind,a>> $2 0:ind) (i:<a[],<ind,a>> $2 1:ind)) ($0 (i:<a[],<ind,a>> $2 1:ind))))))
and :- S/NP\(S/NP)/(S/NP) : (lambda $0:<e,<a,t>> (lambda $1:<e,<a,t>> (lambda $2:e (lambda $3:a[] (and:<t*,t> ($1 $2 (i:<a[],<ind,a>> $3 0:ind)) (bef:<a,<a,t>> (i:<a[],<ind,a>> $3 0:ind) (i:<a[],<ind,a>> $3 1:ind)) ($0 $2 (i:<a[],<ind,a>> $3 1:ind)))))))
and :- C : conj:e
or :- C : disj:e
of :- PP/NP : intersect:<ps,<ps,t>>
to :- PP/NP : intersect:<ps,<ps,t>>
to :- PP/NP : front:<ps,<ps,t>>
with :- PP/NP : intersect:<ps,<ps,t>>
containing :- PP/NP : intersect:<ps,<ps,t>>
contains :- PP/NP : intersect:<ps,<ps,t>>
in :- PP/NP : intersect:<ps,<ps,t>>
down :- PP/NP : intersect:<ps,<ps,t>>
green octagon :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (honeycomb:<ps,t> $1))))
yellow :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (honeycomb:<ps,t> $1))))
flooring :- N : hall:<ps,t>
this :- NP : you:ps
this :- S/N : (lambda $0:<e,t> (intersect:<ps,<ps,t>> you:ps (a:<<e,t>,e> $0)))
is :- S\NP/NP : (lambda $0:e (lambda $1:e (eq:<e,<e,t>> $1 $0)))
is :- S\NP/NP : (lambda $0:e (lambda $1:e (intersect:<ps,<ps,t>> $1 $0)))
position x :- NP : x:ps
four :- NP : 4:n
times :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
segments :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
segment :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
you :- NP : you:ps
see :- S\NP/NP : (lambda $0:e (lambda $1:e (front:<ps,<ps,t>> $1 $0)))
should be facing :- S\NP/NP : (lambda $0:e (lambda $1:e (front:<ps,<ps,t>> $1 $0)))
longer :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 (a:<<e,t>,e> $0))) dist:<ps,n>))))
wooden floored :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (wood:<ps,t> $1))))
wooden-floored :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (wood:<ps,t> $1))))
alley :- N : hall:<ps,t>
alley :- N : intersection:<ps,t>
square :- N : intersection:<ps,t>
once :- AP : (lambda $0:a (len:<a,<n,t>> $0 1:n))
one :- NP : 1:n
x :- NP : x:ps
twice :- AP : (lambda $0:a (len:<a,<n,t>> $0 2:n))
turning :- S/S/NP : (lambda $0:m (lambda $1:t (lambda $2:a (and:<t*,t> (turn:<a,t> $2) (dir:<a,<dir,t>> $2 $0) (post:<a,<t,t>> $2 $1)))))
you see :- S/N : (lambda $0:<e,t> (exists:<<e,t>,t> (lambda $1:e ($0 $1))))
flowered :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (rose:<ps,t> $1))))
flowered :- NP/N : (lambda $0:<e,t> (a:<<e,t>,e> (lambda $1:e (and:<t*,t> ($0 $1) (rose:<ps,t> $1)))))
carpet :- N : hall:<ps,t>
three :- NP : 3:n
there is :- S/NP : (lambda $0:e (exists:<<e,t>,t> (lambda $1:e (eq:<e,<e,t>> $1 $0))))
bench :- N : sofa:<ps,t>
hall :- N : hall:<ps,t>
your :- NP/NP : (lambda $0:m (orient:<ps,<dir,ps>> you:ps $0))
chair :- N : chair:<ps,t>
passing :- AP/NP : (lambda $0:e (lambda $1:a (pass:<a,<ps,t>> $1 $0)))
, :- AP : (lambda $0:a true:t)
, if not turn around :- S\S : (lambda $0:t $0)
opposite :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 frontdist:<ps,n>))))
corner :- N : corner:<ps,t>
empty :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (empty:<ps,t> $1))))
easel :- N : easel:<ps,t>
lamp :- N : lamp:<ps,t>
hat rack :- N : hatrack:<ps,t>
hatrack :- N : hatrack:<ps,t>
intersection :- N : intersection:<ps,t>
blue-tiled :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (blue:<ps,t> $1))))
bare concrete :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (cement:<ps,t> $1))))
brick :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (brick:<ps,t> $1))))
grass :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (grass:<ps,t> $1))))
stone :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> ($0 $1) (stone:<ps,t> $1))))
it :- NP : (io:<<e,t>,e> (lambda $0:e (hatrack:<ps,t> $0)))
face :- S/NP : (lambda $0:e (lambda $1:a (post:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
next :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (order:<<ps,t>,<<ps,n>,<n,ps>>> $0 frontdist:<ps,n> 1:n))))
in front of :- PP/NP : front:<ps,<ps,t>>
six :- NP : 6:n
t intersection :- N : t_intersection:<ps,t>
wall :- N : wall:<ps,t>
place :- S/PP/NP : (lambda $0:e (lambda $1:<e,t> (lambda $2:a (post:<a,<t,t>> $2 ($1 $0)))))
