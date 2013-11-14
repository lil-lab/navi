# Jobs
type=init id=init model=model init=init.lex,init.templates,init.rept,initLexWeights
type=train id=train model=model learner=learner dep=init 
type=test stats=exact.trace,exact.position,exact.coordinates id=test.post tester=tester data=test dep=train exec=execSingle
type=test stats=exact.trace,exact.position,exact.coordinates id=test.naive.post tester=tester data=test exec=execSingleNaive dep=train
type=test id=test.set.post tester=testerSet data=testSet exec=execSet stats=exact.set.coordinates,exact.set.xcoordinates dep=train
type=test id=test.set.naive.post tester=testerSet data=testSet exec=execSetNaive stats=exact.set.coordinates,exact.set.xcoordinates dep=train
type=test stats=exact.position,exact.lf,exact.lf.dup,exact.lf.dup.nl id=test.lf.post tester=tester data=testLF exec=execSingle dep=train
type=log id=log.post model=model dep=train logger=loggerModel
