# How to use the mediating ontologies package

The purpose of this package is to allow you to fetch and store a number of mediating ontologies supporting the ontology alignment (mapping) between a source and a target ontology. From these mediating ontologies the composed mappings are extracted. The goal is to see if we gain a richer mapping set that with a simple /direct mapping using logmap. 

## Initial setup

###1. Write your config file

The config file needs to be called config.json and is stored inside your parent folder for these experiments. The number of mediating ontologies that are gathered is typically set in the parameters.txt file, otherwise a default value (currently 10) is set in the Parameters class. Since not all ontologies can be successfully downloaded via Bioportal API, you need to account for potential download fails. If you want to override the initial number of candidate ontologies listed (i.e. maxMediatingOntologies), the code currently stores only the first successfully accessible 10 from that list. For a list of "bad" ontologies, look at `doc/bioportal_failing_ontologies.txt`, we enlist what we have found so far.

```
{
  "sourceOntologyFullPath": "<full path to source ontology> example1.owl",
  "targetOntologyFullPath": "<full path to target ontology> example2.owl",
  "repoMediatingOntologiesFullPath": "<full path to folder where to check for and store mediating ontologies>",
  "referenceMappingsFullPath": <full path to reference mappings> (optional, leave blank otherwise),
  "logmapLLMDefaultPath" : <full path to logmapLLM mappings (default setting)> (optional, leave blank otherwise),
  "logmapLLMMSubPath":<full path to logmapLLM mappings (mutual subsumption setting)> (optional, leave blank otherwise),
  "annotatedComposedLLMDefaultPath" :<full path to annotated composed mappings (with LLM default setting)> (optional, leave blank otherwise),
  "annotatedComposedLLMMSubtPath":<full path to annotated composed mappings (with LLM mutual subsumption setting)> (optional, leave blank otherwise),
  "overrideMaxMediatingOntologies" : "true",
  "maxMediatingOntologies": 12
}

```

### 2.Choose your parent folder

Please  be aware that, once you have a parent folder for the tasks, the system will create the following folder sub-structure. The config.json needs to be inside the folder for the program to work.

**KNOWN ISSUE** the path of your parent folder should not contain any "." otherwise the code breaks. So `/home/myname/downloads/hello-there/` is fine, but `/home/myname/downloads/hello.there/` breaks. Apologies.

```
<parent folder>/
├── store-source-target/
├── store-simple-mappings/
├── store-composed-mappings/
├── store-unique-mappings/
└── config.json
```
In store-source-target the program will store the direct mappings obtained by logmap when matching the source and target ontology. 
In store-simple-mappings you will get, for each mo_i (mediating ontology i from 1 to max) the pairs
* `source-mo_i.txt` and `source-mo_i.tsv` 
* `mo_i-target.txt` and `mo_i-target.tsv`

The mappings between source and mediating ontology (in two flat formats, txt and tsv) and the mappings between mediating and target ontology. These are used as intermediate step to produce the composed mappings.
In store-composed-mappings you will get these composed mappings, obtained by getting all mappings that are present both in the set of mappings for source-mediating ontology as in the set for mediating-target ontology. They are obtained simply by performing an intersection over the two sets. The logic is available in the method `CreateComposedMappings.aggregateComposedMappings(params)`

## Understanding the pipeline

* Step 1 FetchAndStoreMediatingOntologies
* Step 2 CreateComposedMappings
* Step 3 ProcessComposedMappings

While Step 1 and 3 can be run within Ecplise, Step 2 is RAM intensive and should be run from command line. A quick reminder on how to set this up is given below.

## Build and run from command line

You will need to run from terminal the following to create the packages you need

```
<your-machine>:~/git-repos/logmap-matcher$ mvn -e clean install
```

Among all the others, this will give you two jar files in the target folder 

* `logmap-matcher-4.0.jar`
* `logmap-matcher-4.0-tests.jar`

This is what you will need to run from command line.

### Step 1 FetchAndStoreMediatingOntologies

* The most important parameter to tailor below is the ***max RAM allowed*** to be used for the task. If, for example you have 16GB of RAM, we recommend setting `-Xmx12GB`, to allow for other system processes to run undisturbed.
* The only program argument required is the full path to the parent folder. For example "/home/myname/Downloads/GoodStuff/" (remember the final / in there, we don't yet check for this kind of thing... java will complain otherwise)
To run from command line, modify the snippet below and copy-paste in terminal at the location of your target folder, usually found in `logmap-matcher/target`. You will notice that provided you are calling two separate targets for logmap `logmap-matcher-4.0.jar` and its tests ` logmap-matcher-4.0-tests.jar`.

```
java -Xms500M -Xmx12G -DentityExpansionLimit=10000000 --add-opens=java.base/java.lang=ALL-UNNAMED -cp logmap-matcher-4.0-tests.jar:logmap-matcher-4.0.jar uk.ac.ox.krr.logmap2.mediating_ontologies.FetchAndStoreMediatingOntologies <parent folder>

```


### 2. CreateComposedMappings

This is a memory-heavy program, we highly recommend to run from terminal. Do keep the system monitor open while you run to check that memory is not filling up. 

```
java -Xms500M -Xmx12G -DentityExpansionLimit=10000000 --add-opens=java.base/java.lang=ALL-UNNAMED
     -cp logmap-matcher-4.0-tests.jar:logmap-matcher-4.0.jar 
          uk.ac.ox.krr.logmap2.mediating_ontologies.CreateComposedMappings <parent folder>

```
### 3. ProcessComposedMappings

Where we take the mappings obtained via each mediating ontology and we subtract those that logmap had already found when aligning the source and target. No need to specify settings for logmap as before, this is a pure post-processing program. It is also quite lightweight and it can be safely run from within IDE.

```
java -cp logmap-matcher-4.0-tests.jar:logmap-matcher-4.0.jar 
          uk.ac.ox.krr.logmap2.mediating_ontologies.ProcessComposedMappings <parent folder>
          
```
*As intermediate steps*
This step stores in the folder `store-unique-mappings` all the txt/tsv files with the new unique mappings produced via mediating ontologies. These are the result of subtracting the set of Logmap direct mappings between source and target, from the set of all mappings obtained using Logmap via the top (available) 10 (or more, depending on Parameters.java) mediating ontologies.

As **output for this step**, which is then passed into the next step of annotation, the class produces:
- `all-composed.txt` contains all the composed mappings obtained via the top10 mediating ontologies.

- `all-composed-minus-llm-default.txt` is the set of composed mappings after removing all the mappings produced by Logmap-llm in default setting.

- `all-composed-minus-llm-mutualsub.txt` is the set of composed mappings after removing all the mappings produced by Logmap-llm with mutual subsumption.

### 4. Annotation of composed mappings using LLM

*Currently carried out outside of this pipeline*
We take the composed mappings and share them so that they can be annotated using the same LLM used for LogmapLLM. 

### 5. LogmapBioLLMResults

(once for LLM default and once for LLM mutual subsumption)

- [ ] Take as input the annotated composed mappings (these are the ones annotated via LLM framework)
- [ ] Take as input Logmap LLM mappings
- [ ] Filter only the annotated composed mappings that are annotated as "true"
- [ ] **Add these "true" composed mappings to LogmapLLM maps. This is Logmap BIO LLM**
- [ ] Then compare this to reference (RA)