## Learner
#type=chart.logger id=chartLogger outputDir=/scratch/yoav
type=learner.trc id=learner maxSentenceLength=%{trainingMaxSentenceLength} genlexbeam=%{lexbeam} iter=%{gIter} data=train hard=true validator=wValidator genlex=genlex
type=learner.stocgrad.trc id=learnerStocGrad maxSentenceLength=%{trainingMaxSentenceLength} genlexbeam=%{lexbeam} iter=%{gIter} data=train validator=validator genlex=genlex
# parseLogger=chartLogger
## Testers
type=tester.exec id=tester
type=tester.exec.set id=testerSet
type=exec.set id=execSet model=model beam=10 pruneFails=true
type=exec.single id=execSingle model=model pruneFails=true
type=exec.single id=execSingleNaive model=model pruneFails=false
type=exec.set.naive id=execSetNaive model=model pruneActionless=false
