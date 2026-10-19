/*
 * Take input mappings, compare with reference mappings and compute Precision, Recall and F1-score.
 */

package uk.ac.ox.krr.logmap2.mediating_ontologies;

import java.util.Set;

import uk.ac.ox.krr.logmap2.mappings.objects.MappingObjectStr;

public class CompareToReference{
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		MediatingOntologiesUtils moUtils = new MediatingOntologiesUtils();
		
		moUtils.getParentFolder(args);
		moUtils.readConfigJSON();

		
		// Load reference mappings
		String raMapsFile = moUtils.referenceMapsPath;
		Set<MappingObjectStr> referenceMappings = moUtils.readMappingsFromFile(raMapsFile);
		System.out.println("Reference set of mappings contains " + referenceMappings.size() + " mappings");
		
		
		// Load input mappings (as in, those that need testing)
		String inputMapsFile = moUtils.annotatedComposedLLMMSubtPath;
		Set<MappingObjectStr> inputMappings = moUtils.readMappingsFromFile(inputMapsFile);
		System.out.println(" Input set of mappings contains " + inputMappings.size() + " mappings\n\n");

		// Compare
		
		double precision = moUtils.computePrecisionFromSets(referenceMappings, inputMappings);
		double recall = moUtils.computeRecallFromSets(referenceMappings, inputMappings);
		double f1score = moUtils.computeF1score(precision, recall);
		System.out.println("Precision: " + precision+ 
				" \t recall:" + recall + "\t F1:"+ f1score +"\n\n");
		
	}
	
}

/*System.out.println("\n\n**** LogmapLLM ****\n\n");

// Precision, Recall, F1
double[] statsLLMDefault = computePrecisionAndRecall(raMappings,llmDefaultMappings);
double f1LLMDefault = computeF1score(statsLLMDefault[0],statsLLMDefault[1]);
System.out.println("Logmap LLM(Default) precision: " + Double.toString(statsLLMDefault[0]) + 
		" \t recall:" + Double.toString(statsLLMDefault[1])+ "\t F1:"+ Double.toString(f1LLMDefault) +"\n\n");


LogmapLLMBio_default.addAll(llmDefaultMappings);
//System.out.println("LogmapBioLLM(Default) contains: " + LogmapLLMBio_default.size() + " mappings");

// Save the mappings
String LogmapLLMBio_defaultName = parentPath + "logmapBioLLMDefaultResults";
moUtils.saveOntologyMappings(LogmapLLMBio_default, LogmapLLMBio_defaultName, onto1_iri, onto2_iri);

//Get mappings from LogmapLLM mutual subsumption

try {
llmMutualSubMappings = llmMutualSubMappingReader.getMappingObjects();
System.out.println("Logmap LLM (mutual subsumption) set of mappings contains " + llmMutualSubMappings.size() + " mappings");
}
catch(Exception e) {
	e.printStackTrace();
}

System.out.println("LogmapBioLLM(Mutual Subsumtpion) contains: " + llmMutualSubMappings.size() + " mappings");


// Precision, Recall, F1
double[] statsLLMMSub = computePrecisionAndRecall(raMappings,llmMutualSubMappings);
double f1LLMMSub = computeF1score(statsLLMMSub[0],statsLLMMSub[1]);
System.out.println("Logmap LLM(Mutual Subsumption) precision: " + Double.toString(statsLLMMSub[0]) + 
		" \t recall:" + Double.toString(statsLLMMSub[1]) + "\t F1:" + Double.toString(f1LLMMSub) + "\n\n");



LogmapLLMBio_msub.addAll(llmMutualSubMappings);
// Save the mappings
String LogmapLLMBio_msubName = parentPath + "logmapBioLLMMSubResults";
moUtils.saveOntologyMappings(LogmapLLMBio_msub, LogmapLLMBio_msubName, onto1_iri, onto2_iri);

System.out.println("\n\n**** LogmapBioLLM ****\n\n");

// Precision, Recall, F1
double[] statsDefault = computePrecisionAndRecall(raMappings, LogmapLLMBio_default);
double f1Default = computeF1score(statsDefault[0], statsDefault[1]);
System.out.println("LogmapBioLLM(Default) precision: " + Double.toString(statsDefault[0]) + " \t recall:" + statsDefault[1]+ 
		"\tF1:" + f1Default + "\n\n");


double[] statsMSub = computePrecisionAndRecall(raMappings, LogmapLLMBio_msub);
double f1MSub = computeF1score(statsMSub[0], statsMSub[1]);
System.out.println("LogmapBioLLM(MutualSubsumption) precision: " + Double.toString(statsMSub[0]) + " \t recall:" + statsMSub[1]+ 
		"\tF1:" + f1MSub + "\n\n");

System.out.println("\n\n**** LogmapBioLLM with repair****\n\n");

*//** Default **//*

//fixed_mappings are llmDefaultMappings
// mappings2review are llmTrueComposedMapsWithLLMDefault
LogMap3_RepairFacility TrueWithDefault_repair = new LogMap3_RepairFacility(onto1, onto2, llmDefaultMappings, llmTrueComposedMapsWithLLMDefault);
Set<MappingObjectStr> LogmapBioLLM_default_r = TrueWithDefault_repair.getCleanMappings();
System.out.println("LLM Default - Size of fixed_mappings:" + llmDefaultMappings.size() + "\tmappings to review:" + llmTrueComposedMapsWithLLMDefault.size() +
		"\t repaired mappings: " + LogmapBioLLM_default_r.size());

LogmapBioLLM_default_r.addAll(llmDefaultMappings);
// Precision, Recall, F1
double[] statsRepDefault = computePrecisionAndRecall(raMappings, LogmapBioLLM_default_r);
double f1RepDefault = computeF1score(statsRepDefault[0], statsRepDefault[1]);
System.out.println("LogmapBioLLM(Default)Repaired precision: " + Double.toString(statsRepDefault[0]) + " \t recall:" + statsRepDefault[1]+ 
		"\tF1:" + f1RepDefault + "\n\n");
String LogmapLLmBio_default_rName = parentPath + "logmapBioLLMDefaultResults_repaired";
moUtils.saveOntologyMappings(LogmapBioLLM_default_r, LogmapLLmBio_default_rName, onto1_iri, onto2_iri);

//** Mutual Subsumption **/

/*
 * 

//fixed_mappings are llmMutualSubMappings
// mappings2review are llmTrueComposedMapsWithLLMDefault
LogMap3_RepairFacility TrueWithMSub_repair = new LogMap3_RepairFacility(onto1, onto2, llmMutualSubMappings, llmTrueComposedMapsWithLLMMSub);
Set<MappingObjectStr> LogmapBioLLM_msub_r = TrueWithMSub_repair.getCleanMappings();
System.out.println("LLM MSub - Size of fixed_mappings:" + llmMutualSubMappings.size() + "\tmappings to review:" + llmTrueComposedMapsWithLLMMSub.size() +
		"\t repaired mappings: " + LogmapBioLLM_msub_r.size());

LogmapBioLLM_msub_r.addAll(llmMutualSubMappings);
double[] statsRepMSub = computePrecisionAndRecall(raMappings, LogmapBioLLM_msub_r);
double f1RepMSub = computeF1score(statsRepMSub[0], statsRepMSub[1]);
System.out.println("LogmapBioLLM(MutualSubsumption)Repaired precision: " + Double.toString(statsRepMSub[0]) + " \t recall:" + statsRepMSub[1]+ 
		"\tF1:" + f1RepMSub + "\n\n");
String LogmapLLmBio_msub_rName = parentPath + "logmapBioLLMMSubResults_repaired";
moUtils.saveOntologyMappings(LogmapBioLLM_msub_r, LogmapLLmBio_msub_rName, onto1_iri, onto2_iri);

 * 
 */





