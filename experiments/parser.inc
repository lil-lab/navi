# Parsing rules
type=rule.composition id=ruleComp
type=rule.application id=ruleApp
type=rule.shifting.pp id=shiftPP
type=rule.shifting.adj id=shiftADJ
type=rule.shifting.ap id=shiftAP
type=rule.shifting.ap.topic id=shiftAPTopic
type=rule.shifting.sentence.ap id=shiftSAP
type=rule.set.overload id=overloadedRules rules=ruleComp,ruleApp functions=shiftPP,shiftAP,shiftAPTopic,shiftSAP,shiftADJ
type=rule.skipping id=ruleSkipping
type=rule.set id=ruleSet rules=overloadedRules
type=ckyrule.marked id=markedCKYRule baseRule=ruleSet maxEntries=1
# Parse filter
type=parsefilter.lambda.simple id=parseFilter syntax=S
# CKY Parser
type=parser.cky.multi id=ckyParser parseFilter=parseFilter beam=%{gBeam} preChartPruning=true pruneLexicalCells=true rules=ruleSkipping executor=executor ckyRules=markedCKYRule
type=parser.cky.mark id=baseParser parser=ckyParser
# Joint parser
type=parser.joint.navi.graph id=parser baseParser=baseParser evalTimeout=120000
