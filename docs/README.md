## Logging failing ontologies
Whenever we try to download ontologies from Bioportal via their API (for example, when using `RunMediatingOntologiesPipeline`) 
and the download fails, we log the error received in  `bioportal_failing_ontologies.txt`. For example,

```
2026-08-31 15:16 UTC
*BioPortal is active: 
*Downloading ontology NATPRO. Attempt: 3
*Error: org.semanticweb.owlapi.model.UnloadableImportException: Could not load imported ontology: <http://protege.stanford.edu/plugins/owl/protege> Cause: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	at uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl.makeLoadImportRequest(OWLOntologyManagerImpl.java:1605)

```
//TODO
Some lists of "bad" ontologies are spread across the code, for example, see `MediatingOntologyExtractor`. We should try and download them again.