# LogMap: Logic-based and Scalable Ontology Matching

LogMap is a highly scalable ontology matching system with ‘built-in’ reasoning and inconsistency repair capabilities. LogMap extract mappings between classes, properties and instances.

To the best of our knowledge, LogMap is one of the few matching systems that: 
1. can efficiently match semantically rich ontologies containing tens (and even hundreds) of thousands of classes, 
2. incorporates sophisticated reasoning and repair techniques to minimise the number of logical inconsistencies, and 
3. provides support for user intervention during the matching process (see [Web interface](http://csu6325.cs.ox.ac.uk/)). 

Please refer to the [OAIE campaign](http://oaei.ontologymatching.org/) for official results about LogMap.



## Using LogMap

LogMap accepts the same ontology formats as the OWL API: e.g., RDF/XML, OWL/XML, OWL Functional, OBO, KRSS, and Turtle (n3).

### As an Ontology Matching System

LogMap can be used from the command line with the standalone distribution or the SEALS-OAEI package, or directly from its Web interface

LogMap can also be easily integrated in other Java applications.

### As a Mapping Debugging System

LogMap can also be used as a mapping debugging system from the command line or integrated in a Java application.


## Contact
Ernesto Jimenez-Ruiz (ernesto [.] jimenez [.] ruiz [at] gmail.com)


## Main Publications

- Daniel Faria, Ernesto Jimenez-Ruiz, Catia Pesquita, Emanuel Santos and Francisco M. Couto. **Towards annotating potential incoherences in BioPortal mappings**. 13th International Semantic Web Confernece (ISWC 2014). ([PDF]())

- Ernesto Jiménez Ruiz‚ Bernardo Cuenca Grau‚ Yujiao Zhou and Ian Horrocks. **Large−scale Interactive Ontology Matching: Algorithms and Implementation**. In the 20th European Conference on Artificial Intelligence (ECAI 2012). ([PDF]())

- Ernesto Jiménez-Ruiz, Bernardo Cuenca Grau. **LogMap: Logic-based and Scalable Ontology Matching**. In the 10th International Semantic Web Confernece (ISWC 2011). ([PDF]())

- Ernesto Jiménez-Ruiz, Christian Meilicke, Bernardo Cuenca Grau and Ian Horrocks. **Evaluating Mapping Repair Systems with Large Biomedical Ontologies**. In 26th International Workshop on Description Logics (DL 2013). ([PDF]())


## Acknowledgements

LogMap has been created in the [Knowledge Representation and Reasoning group](http://www.cs.ox.ac.uk/isg/krr/) at the [Department of Computer Science](http://www.cs.ox.ac.uk/) of the [University of Oxford](http://www.ox.ac.uk/). 
Development has been supported by The Royal Society, the EPSRC project LogMap and EU FP7 projects SEALS and Optique.
We also thank the organisers of the [OAEI evaluation campaigns](http://oaei.ontologymatching.org/) for providing test data and infrastructure.

