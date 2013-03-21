, :- AP : (lambda $0:a true:t)
, :- S/S : (lambda $0:t $0)
? :- S\S : (lambda $0:<a,t> $0)
a :- NP/N : a:<<e,t>,e>
about :- NP/NP : (lambda $0:m $0)
against :- PP/NP : intersect:<ps,<ps,t>>
all the way down :- AP : (lambda $0:a (to:<a,<ps,t>> $0 (a:<<e,t>,e> wall:<ps,t>)))
all the way down :- AP/NP : (lambda $0:e (lambda $1:a (while:<a,<ps,t>> $1 $0)))
all the way down to :- AP/NP : (lambda $0:e (lambda $1:a (to:<a,<ps,t>> $1 $0)))
alley :- N : hall:<ps,t>
along :- AP/NP : (lambda $0:e (lambda $1:a (while:<a,<ps,t>> $1 $0)))
an :- NP/N : a:<<e,t>,e>
and :- C : conj:c
and :- S/NP\(S/NP)/(S/NP) : (lambda $0:<e,<a,t>> (lambda $1:<e,<a,t>> (lambda $2:e (lambda $3:a[] (and:<t*,t> ($1 $2 (i:<a[],<ind,a>> $3 0:ind)) (bef:<a,<a,t>> (i:<a[],<ind,a>> $3 0:ind) (i:<a[],<ind,a>> $3 1:ind)) ($0 $2 (i:<a[],<ind,a>> $3 1:ind)))))))
and :- S\S/S : (lambda $0:<a,t> (lambda $1:<a,t> (lambda $2:a[] (and:<t*,t> ($1 (i:<a[],<ind,a>> $2 0:ind)) (bef:<a,<a,t>> (i:<a[],<ind,a>> $2 0:ind) (i:<a[],<ind,a>> $2 1:ind)) ($0 (i:<a[],<ind,a>> $2 1:ind))))))
are at :- S\NP/NP : intersect:<ps,<ps,t>>
are in :- PP\NP : intersect:<ps,<ps,t>>
at :- AP/NP : (lambda $0:e (lambda $1:a (pre:<a,<ps,t>> $1 $0)))
back :- NP : back:dir
bare concrete :- ADJ : cement:<ps,t>
bench :- N : sofa:<ps,t>
black :- ADJ : stone:<ps,t>
blue :- ADJ : blue:<ps,t>
blue :- N : blue:<ps,t>
butterflies on the wall :- NP : (a:<<e,t>,e> butterfly_w:<ps,t>)
carpet hallway :- N : hall:<ps,t>
chair :- N : chair:<ps,t>
containing :- PP/NP : intersect:<ps,<ps,t>>
contains :- S\NP/NP : intersect:<ps,<ps,t>>
corner :- N : corner:<ps,t>
dead end :- N : deadend:<ps,t>
down :- AP/NP : (lambda $0:e (lambda $1:a (post:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
down :- AP/NP : (lambda $0:e (lambda $1:a (while:<a,<ps,t>> $1 $0)))
easel :- N : easel:<ps,t>
empty :- ADJ : empty:<ps,t>
end :- N : (lambda $0:e (end:<ps,<ps,t>> $0 (io:<<e,t>,e> hall:<ps,t>)))
end of :- N/NP : (lambda $0:e (lambda $1:e (end:<ps,<ps,t>> $1 $0)))
face :- S/NP : (lambda $0:e (lambda $1:a (post:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
far :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 dist:<ps,n>))))
fish hallway :- N : fish_w:<ps,t>
floor :- N : hall:<ps,t>
flooring :- N : hall:<ps,t>
floors :- N : hall:<ps,t>
follow this :- S : move:<a,t>
forward :- NP : forward:dir
four :- NP : 4:n
get to :- S\NP/NP : intersect:<ps,<ps,t>>
go :- S : move:<a,t>
go :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (move:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
grass :- N : grass:<ps,t>
hall :- N : hall:<ps,t>
halls :- N : hall:<ps,t>
hallway :- N : hall:<ps,t>
has :- S\NP/NP : intersect:<ps,<ps,t>>
hatrack :- N : hatrack:<ps,t>
hit :- S\NP/NP : intersect:<ps,<ps,t>>
intersection :- N : intersection:<ps,t>
into :- AP/NP : (lambda $0:e (lambda $1:a (to:<a,<ps,t>> $1 $0)))
is :- S\NP/NP : intersect:<ps,<ps,t>>
is at :- S\NP/NP : intersect:<ps,<ps,t>>
is facing :- S\NP/NP : intersect:<ps,<ps,t>>
keep going :- S : move:<a,t>
lamp :- N : lamp:<ps,t>
left :- NP : left:dir
longer end of :- N/NP : (lambda $0:e (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (end:<ps,<ps,t>> $2 $0)) dist:<ps,n>))))
make a :- S/NP : (lambda $0:m (lambda $1:a (dir:<a,<dir,t>> $1 $0)))
move :- S : move:<a,t>
next :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (order:<<ps,t>,<<ps,n>,<n,ps>>> $0 frontdist:<ps,n> 1:n))))
octagons :- N : honeycomb:<ps,t>
of :- PP/NP : intersect:<ps,<ps,t>>
on its spot :- PP : (lambda $0:e true:t)
once :- AP : (lambda $0:a (len:<a,<n,t>> $0 1:n))
once :- S/S/S : (lambda $0:t (lambda $1:t (lambda $2:a (post:<a,<t,t>> $2 (and:<t*,t> $0 $1)))))
one :- NP : 1:n
only one direction :- N : deadend:<ps,t>
onto :- AP/NP : (lambda $0:e (lambda $1:a (post:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
other :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 dist:<ps,n>))))
out of :- AP/NP : (lambda $0:e (lambda $1:a (pre:<a,<ps,t>> $1 $0)))
passing :- AP/NP : (lambda $0:e (lambda $1:a (pass:<a,<ps,t>> $1 $0)))
past :- AP/NP : (lambda $0:e (lambda $1:a (pass:<a,<ps,t>> $1 $0)))
path :- N : hall:<ps,t>
piece of furniture :- N : furniture:<ps,t>
pink on the floor :- N : rose:<ps,t>
pink-flowered :- ADJ : rose:<ps,t>
place :- S/PP/NP : (lambda $0:e (lambda $1:<e,t> (lambda $2:a (post:<a,<t,t>> $2 ($1 $0)))))
position x :- NP : x:ps
reach :- S\NP/NP : intersect:<ps,<ps,t>>
red :- ADJ : brick:<ps,t>
red brick :- ADJ : brick:<ps,t>
right :- NP : right:dir
right :- S : (lambda $0:a (dir:<a,<dir,t>> $0 right:dir))
road :- N : hall:<ps,t>
rose floored :- ADJ : rose:<ps,t>
see :- S\NP/NP : (lambda $0:e (lambda $1:e (front:<ps,<ps,t>> $1 $0)))
segment :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
segments :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
so that :- AP/S : (lambda $0:t (lambda $1:a (post:<a,<t,t>> $1 $0)))
sofa chair :- N : sofa:<ps,t>
stone :- ADJ : stone:<ps,t>
stool :- N : barstool:<ps,t>
t intersection :- N : t_intersection:<ps,t>
take :- S/NP : (lambda $0:e (lambda $1:a (and:<t*,t> (move:<a,t> $1) (while:<a,<ps,t>> $1 $0))))
take a :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (turn:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
that :- NP/N : io:<<e,t>,e>
the :- NP/N : io:<<e,t>,e>
then :- AP/S : (lambda $0:t (lambda $1:a (post:<a,<t,t>> $1 $0)))
then :- S\S/S : (lambda $0:<a,t> (lambda $1:<a,t> (lambda $2:a[] (and:<t*,t> ($1 (i:<a[],<ind,a>> $2 0:ind)) (bef:<a,<a,t>> (i:<a[],<ind,a>> $2 0:ind) (i:<a[],<ind,a>> $2 1:ind)) ($0 (i:<a[],<ind,a>> $2 1:ind))))))
this :- NP : (io:<<e,t>,e> (lambda $0:e true:t))
this :- NP/N : io:<<e,t>,e>
three :- NP : 3:n
till :- AP/S : (lambda $0:t (lambda $1:a (post:<a,<t,t>> $1 $0)))
times :- AP\NP : (lambda $0:m (lambda $1:a (len:<a,<n,t>> $1 $0)))
to :- AP/NP : (lambda $0:e (lambda $1:a (to:<a,<ps,t>> $1 $0)))
to :- S\NP/NP : (lambda $0:e (lambda $1:e (front:<ps,<ps,t>> $1 $0)))
to go :- S\N : (lambda $0:<e,t> (intersect:<ps,<ps,t>> you:ps (a:<<e,t>,e> $0)))
to the along :- AP/NP : (lambda $0:e (lambda $1:a (while:<a,<ps,t>> $1 $0)))
towards :- AP/NP : (lambda $0:e (lambda $1:a (pre:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
towards the direction on :- AP/NP : (lambda $0:e (lambda $1:a (post:<a,<t,t>> $1 (front:<ps,<ps,t>> you:ps $0))))
turn :- S : turn:<a,t>
turn :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (turn:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
two :- NP : 2:n
until :- AP/S : (lambda $0:t (lambda $1:a (post:<a,<t,t>> $1 $0)))
walk :- S : move:<a,t>
walk :- S/NP : (lambda $0:m (lambda $1:a (and:<t*,t> (move:<a,t> $1) (dir:<a,<dir,t>> $1 $0))))
wall :- N : wall:<ps,t>
when :- AP/S : (lambda $0:t (lambda $1:a (pre:<a,<t,t>> $1 $0)))
which leads to :- PP/NP : intersect:<ps,<ps,t>>
white cement :- N : cement:<ps,t>
winding hall :- N : hall:<ps,t>
with :- AP/S : (lambda $0:t (lambda $1:a (pre:<a,<t,t>> $1 $0)))
with :- PP/NP : intersect:<ps,<ps,t>>
with no :- PP/N : (lambda $0:<e,t> (lambda $1:e (not:<t,t> ($0 $1))))
wood :- ADJ : wood:<ps,t>
wood floor :- ADJ : wood:<ps,t>
wooden-floored :- ADJ : wood:<ps,t>
x :- NP : x:ps
yellow :- ADJ : honeycomb:<ps,t>
yellow-tiled:- ADJ : honeycomb:<ps,t>
you :- NP : you:ps
your :- NP/NP : (lambda $0:m (orient:<ps,<dir,ps>> you:ps $0))
