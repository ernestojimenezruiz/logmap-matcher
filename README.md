# LogMap: An Ontology Alignment and Alignment Repair System

**News:**

1. LogMap is now relying on the [OWL API 4](https://github.com/owlcs/owlapi/wiki/Migrate-from-version-3.4-and-3.5-to-4.0). See here the (non-maintained) branch that uses [OWL API 3](https://github.com/ernestojimenezruiz/logmap-matcher/tree/logmap-owlapi-3)
2. Check out the new material based on [OWL2Vec\*](https://github.com/KRR-Oxford/OWL2Vec-Star) about integrating machine leaning techniques within the ontology alignment task [here](https://github.com/KRR-Oxford/OntoAlign).
3. Check out the new material about the division of the ontology alignment task [here](#division-of-the-ontology-alignment-task).

## About

LogMap is a highly scalable ontology matching system with ‘built-in’ reasoning and inconsistency repair capabilities. LogMap extract mappings between classes, properties and instances.

To the best of our knowledge, LogMap is one of the few matching systems that:

1. can efficiently match semantically rich ontologies containing tens (and even hundreds) of thousands of classes,

2. incorporates sophisticated reasoning and repair techniques to minimise the number of logical inconsistencies, and 

3. provides support for user intervention during the matching process (see [Web interface](http://krrwebtools.cs.ox.ac.uk/logmap/)). 

Please refer to the [OAIE campaign](http://oaei.ontologymatching.org/) for official results about LogMap.


## Downloading and Development

LogMap standalone distributions and OAEI packages can be downloaded from [SourceForge](https://sourceforge.net/projects/logmap-matcher/).  

Development requires a clone of this git repository. You can use the pre-configured Eclipse (Maven) project. Dependencies are automatically downloaded with the exception of google translate which needs to be manually added to the maven local repository (see [lib/readme_mvn_install_google_translate.txt](./lib/readme_mvn_install_google_translate.txt)).
 
To generate a JAR file for LogMap from the command line using Maven, run: `mvn package` or `mvn clean install`. This will also generate a folder *java-dependencies* with all the necessary libraries. This folder together with the *parameters.txt* file should be placed in the same path as the generated "logmap-matcher-3.0.jar" file. 




## Using LogMap

LogMap accepts the same ontology formats as the OWL API: e.g., RDF/XML, OWL/XML, OWL Functional, OBO, KRSS, and Turtle (n3).

**As an Ontology Matching System**

LogMap can be used from the command line with the [standalone distribution](https://sourceforge.net/projects/logmap-matcher/files/Standalone%20distribution/) or 
the [OAEI packages](https://sourceforge.net/projects/logmap-matcher/files/OAEI%20packages/), or directly from its [Web interface](http://krrwebtools.cs.ox.ac.uk/logmap/)

It has recently been implemented support to be run in the [HOBBIT platform](https://project-hobbit.eu/outcomes/hobbit-platform/). See details [here](https://gitlab.com/ernesto.jimenez.ruiz/logmap-hobbit).

LogMap can also be easily integrated in other Java applications. See [wiki](https://code.google.com/archive/p/logmap-matcher/wikis) for additional information.


**As a Mapping Debugging System**

LogMap can also be used as a mapping debugging system from the command line or integrated in a Java application. See [wiki](https://code.google.com/archive/p/logmap-matcher/wikis) for additional information.

We have also implemented a variant of LogMap to minimize the violations of the conservativity principle. Check details [here](https://github.com/ernestojimenezruiz/logmap-conservativity).


## Division of the Ontology Alignment Task

LogMap also includes a novel module to divide the ontology alignment task into manageable subtasks.

Resources: [source classes](https://github.com/ernestojimenezruiz/logmap-matcher/tree/master/src/main/java/uk/ac/ox/krr/logmap2/division), [test classes](https://github.com/ernestojimenezruiz/logmap-matcher/tree/master/src/test/java/uk/ac/ox/krr/logmap2/test/overlapping), [neural embeddings](https://github.com/plumdeq/neuro-onto-part), [datasets](https://doi.org/10.5281/zenodo.1214148), [paper](https://arxiv.org/abs/2003.05370), [slides](https://drive.google.com/file/d/1zWkRWmk7Jo-p9QJr1ZGdk5HFJse_1pmv/view?usp=sharing), [video slides](https://drive.google.com/file/d/1-DDNurn_xmgMQkVWnUU3CT_q6Aokjhru/view?usp=sharing)



## Contact
Ernesto Jiménez-Ruiz (ernesto [.] jimenez [.] ruiz [at] gmail.com)

Please report any issue related to LogMap in our <a href="https://groups.google.com/forum/#!forum/logmap-matcher-discussion" target="_blank">discussion group</a> 
or in our <a href="https://github.com/ernestojimenezruiz/logmap-matcher/issues" target="_blank">issue tracker</a>.


## Main Publications

**Matching System**

- Ernesto Jiménez Ruiz‚ Bernardo Cuenca Grau‚ Yujiao Zhou and Ian Horrocks. **Large−scale Interactive Ontology Matching: Algorithms and Implementation**. In the 20th European Conference on Artificial Intelligence (ECAI 2012). ([PDF](http://www.cs.ox.ac.uk/files/4801/LogMap_ecai2012.pdf))([Slides](https://www.slideshare.net/ernestojimenezruiz/logmap-largescale-logicbased-and-interactive-ontology-matching))

- Ernesto Jiménez-Ruiz, Bernardo Cuenca Grau. **LogMap: Logic-based and Scalable Ontology Matching**. In the 10th International Semantic Web Confernece (ISWC 2011). ([PDF](http://www.cs.ox.ac.uk/isg/projects/LogMap/papers/paper_ISWC2011.pdf))([Slides](https://www.slideshare.net/ernestojimenezruiz/logmap-logicbased-and-scalable-ontology-matching))

- Ernesto Jiménez-Ruiz et al. **LogMap family results for OAEI 2014**. 9th International Workshop on Ontology Matching (OM 2014). ([PDF](http://disi.unitn.it/~p2p/OM-2014/oaei14_paper4.pdf))

**Combination with Machine Learning**

- Jiaoyan Chen, Ernesto Jimenez-Ruiz, Ian Horrocks, Denvar Antonyrajah, Ali Hadian, Jaehun Lee. **Augmenting Ontology Alignment by Semantic Embedding and Distant Supervision**. European Semantic Web Conference, ESWC 2021. ([PDF](https://openaccess.city.ac.uk/id/eprint/25810/1/ESWC2021_ontology_alignment_LogMap_ML.pdf)) ([OWL2Vec\* work](https://github.com/KRR-Oxford/OWL2Vec-Star))


**Repair**

- Alessandro Solimando, Ernesto Jiménez-Ruiz, Giovanna Guerrini:
**Minimizing conservativity violations in ontology alignments: algorithms and evaluation**. Knowl. Inf. Syst. 51(3): 775-819 (2017). ([PDF](https://www.cs.ox.ac.uk/files/8299/kais-conservativity.pdf)) 

- Ernesto Jiménez-Ruiz, Terry R. Payne, Alessandro Solimando, Valentina A. M. Tamma:
**Limiting Logical Violations in Ontology Alignnment Through Negotiation**. KR 2016: 217-226. ([PDF](https://www.cs.ox.ac.uk/files/8036/kr2016_jimenez-ruiz.pdf))([Slides](https://www.slideshare.net/ernestojimenezruiz/limiting-logical-violations-in-ontology-alignnment-through-negotiation))

- Daniel Faria, Ernesto Jiménez-Ruiz, Catia Pesquita, Emanuel Santos and Francisco M. Couto. **Towards annotating potential incoherences in BioPortal mappings**. 13th International Semantic Web Confernece (ISWC 2014). ([PDF](https://www.cs.ox.ac.uk/files/6655/paper_ISWC_BioPortal.pdf))([Slides](https://www.slideshare.net/ernestojimenezruiz/towards-annotating-potential-incoherences-in-bioportal-mappings))

- Ernesto Jiménez-Ruiz, Christian Meilicke, Bernardo Cuenca Grau and Ian Horrocks. **Evaluating Mapping Repair Systems with Large Biomedical Ontologies**. In 26th International Workshop on Description Logics (DL 2013). ([PDF](http://ceur-ws.org/Vol-1014/paper_63.pdf))([Slides](https://www.slideshare.net/ernestojimenezruiz/evaluating-mapping-repair-systems-with-large-biomedical-ontologies))

- Ernesto Jiménez-Ruiz, Bernardo Cuenca Grau, Ian Horrocks and Rafael Berlanga. **Logic-based assessment of the compatibility of UMLS ontology sources**. Journal of Biomedical Semantics,  volume 2, 2011 ([PDF](https://jbiomedsem.biomedcentral.com/track/pdf/10.1186/2041-1480-2-S1-S2.pdf)) ([HTML](https://jbiomedsem.biomedcentral.com/articles/10.1186/2041-1480-2-S1-S2)). Preliminary repair work that led to LogMap's repair.


**Division Matching Task**

- Ernesto Jiménez-Ruiz, Asan Agibetov, Jiaoyan Chen, Matthias Samwald, Valerie Cross. **Dividing the Ontology Alignment Task with Semantic Embeddings and Logic-based Modules**. In the 24th European Conference on Artificial Intelligence (ECAI 2020). 
([PDF](https://arxiv.org/abs/2003.05370)) ([Slides](https://drive.google.com/file/d/1zWkRWmk7Jo-p9QJr1ZGdk5HFJse_1pmv/view?usp=sharing)) ([Slides with Video](https://drive.google.com/file/d/1-DDNurn_xmgMQkVWnUU3CT_q6Aokjhru/view?usp=sharing))

- Ernesto Jiménez-Ruiz, Asan Agibetov, Matthias Samwald, Valerie Cross. **Breaking-down the Ontology Alignment Task with a Lexical Index and Neural Embeddings**. arXiv:1805.12402. ([PDF](https://arxiv.org/pdf/1805.12402.pdf))

Additional list of [LogMap-related publications](http://www.cs.ox.ac.uk/projects/publications/date/LogMap.html).


## Acknowledgements

LogMap has been created in the [Knowledge Representation and Reasoning group](http://www.cs.ox.ac.uk/isg/krr/) at the [Department of Computer Science](http://www.cs.ox.ac.uk/) of 
the [University of Oxford](http://www.ox.ac.uk/) by Ernesto Jiménez-Ruiz, Bernardo Cuenca Grau and Ian Horrocks. 

Development has been supported by The Royal Society, the EPSRC project LogMap, the EU FP7 projects SEALS and Optique, the [AIDA project](https://www.turing.ac.uk/research/research-projects/artificial-intelligence-data-analytics-aida), and the [SIRIUS Centre for Scalable Data Access](http://sirius-labs.no/).

We would like to thank Alessandro Solimando, Valerie Cross, Anton Morant, Yujiao Zhou, Weiguo Xia, Xi Chen, Yuan Gong and Shuo Zhang, who have contributed to the LogMap project in the past.

We also thank the organisers of the [OAEI evaluation campaigns](http://oaei.ontologymatching.org/) for providing test data and infrastructure.


## License
Copyright 2022 Department of Computer Science (University of Oxford) and Department of Computer Science (City, University of London)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. 
