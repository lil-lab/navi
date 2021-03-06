# _**Navi v1.2**_

Yoav Artzi and Luke Zettlemoyer. [Weakly Supervised Learning of Semantic Parsers for Mapping Instructions to Actions](http://yoavartzi.com/pub/az-tacl.2013.pdf). In Transactions of the Association for Computational Linguistics (TACL), 2013.

## Building

To compile Navi use: `ant dist`. The output JAR file will be in the `dist` directory. You can also download the compiled JAR file from the [Downloads](https://bitbucket.org/yoavartzi/navi/downloads) section.

## Package root file structure

    README.md
        This README file.  
    build.xml
    build.properites
    	ANT build files.
    <experiments>
        Experiments including data (see details below).  
    <lib>  
        External libraries, including UW SPF.  
    <navi>  
        Source code (an Eclipse project).  
    <resources>
        Various resource files, such as: ontology, type files, seed lexicon and maps.  
    <resource-test>
        Unit tests resources.  

## Data

Navi includes two corpora: SAIL and Oracle, as described in the paper. SAIL experiments use cross-validation across the three environment maps. Each instruction sequence in SAIL is annotated with an execution trace segmented by sentences. The Oracle corpus includes similar annotation. In addition, randomly sampled subsets of the Oracle evaluation and development datasets were annotated with complete logical forms, in addition to execution traces. 

**Note:** All experiment data files have both `.settrc` and `.ccgsettrc` versions. The `.settrc` version includes segmented instruction sequences paired with traces. The `.ccgsettrc` files additionally include the logical forms. However, in most cases `.ccgsettrc` files include a dummy logical form for file formatting compatibility. The dummy logical form is **never** used in training or evaluation and doesn't describe the language.

## Running Experiments

Experiments are defined by `.exp` files. To run an experiment `file.exp` use:  
	`java -Xmx8g -jar navi-1.2.jar /path/to/exp/dir/file.exp` 

It's best to allocate as much memory as possible. However, the code will probably run with less than 8GB (and will benefit from more).

The output logs will be dumped into logging directories under the executed experiment directory. The data of each experiment is also located under the experiment directory, usually in a directory called `data`.

## Included Experiments

### Development cross validation (dev.cross)

Cross validation experiment on the Oracle development set. Folds were randomly sampled over instruction sequences.  

Experiment files:  
`experiments/dev.cross/dev.fold0.exp`  
`experiments/dev.cross/dev.fold1.exp`  
`experiments/dev.cross/dev.fold2.exp`  
`experiments/dev.cross/dev.fold3.exp`  
`experiments/dev.cross/dev.fold4.exp`  
								
### Implicit actions development cross validation (dev.abimp.cross)

Cross validation experiment on the Oracle development set without the ability to infer implicit actions. The folds are the same as in dev.cross.

Experiment files:  
`experiments/dev.abimp.cross/dev.fold0.exp`  
`experiments/dev.abimp.cross/dev.fold1.exp`  
`experiments/dev.abimp.cross/dev.fold2.exp`  
`experiments/dev.abimp.cross/dev.fold3.exp`  
`experiments/dev.abimp.cross/dev.fold4.exp`  

### Held-out evaluation (eval)

Held-out evaluation experiment on the Oracle corpus. Training is done on the entire development set.  

Experiment file:  
`experiments/eval/eval.exp`

### Development cross validation SAIL experiment (dev.chen.cross)

Cross validation experiment on development set on the SAIL corpus from Chen and Mooney (2011). Folds were split according to the map used. The development set was created by removing all instructions sequences in the Oracle evaluation set.

Experiment files:  
`experiments/dev.chen.cross/fold-grid.exp`  
`experiments/dev.chen.cross/fold-jelly.exp`  
`experiments/dev.chen.cross/fold-l.exp`  
						
### Evaluation cross validation SAIL experiment (eval.chen.cross)

Cross validation experiment on the entire SAIL corpus from Chen and Mooney (2011). Folds were split according to the map used. 

Experiment files:  
`experiments/eval.chen.cross/fold-grid.exp`  
`experiments/eval.chen.cross/fold-jelly.exp`  
`experiments/eval.chen.cross/fold-l.exp`  

## Eclipse

To work with the code in Eclipse create a workspace and import the  project `navi` into it. You will also need to define the [classpath variable](http://stackoverflow.com/questions/3557369/how-to-use-variables-for-classpath-definition-in-eclipse-launch-configurations) `NAVI_REPO` to point to the source repository. 

## Understanding the code

Navi uses [UW SPF](http://yoavartzi.com/spf) (The University of Washington Semantic Parsing Framework). The experiments included use the experiment platform provided by SPF. The best way to start understanding the code and the various features is to first understand the SPF experiment platform (documented in SPF) and then use the experiment files as a starting point to explore the code. 

## License

Navi. Copyright (C) 2013 Yoav Artzi

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or any later version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 51
Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
