## Model
type=scorer.uniform id=uniform0Scorer   class=lexEntry  weight=0.0
type=scorer.lenexp id=expScorer coef=10.0 exp=1.1
type=scorer.lex.skipping   id=skippingScorer cost=%{wordSkippingCost}  baseScorer=uniform0Scorer
type=feat.logexp.coordination id=featLogexpCoordination rept=true
type=feat.lex init=skippingScorer id=featLex
type=feat.lexeme id=featLexeme
type=feat.rules.count id=featRules scale=0.1
type=feat.lextemplate id=featLextemplate scale=0.1
type=lexicon.factored id=lexicon
type=model.joint	id=model	lexicon=lexicon	lexicalFeatures=featLex,featLextemplate,featLexeme parseFeatures=featLogexpCoordination,featRules
type=logger.model id=loggerModel cluster=true
## Model init
type=init.lex	id=init.lex	lexicon=initialLexicon
type=init.lex.weights id=initLexWeights tag=LEX scorer=expScorer lexicon=initialLexicon
type=init.feats.rept id=init.rept tag=LOGEXP name=REPT weight=-10
type=init.feats.templates id=init.templates tag=XTMP lexicon=initialLexicon
