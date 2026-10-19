package uk.ac.ox.krr.logmap2.mediating_ontologies;

import org.junit.Test;
import static org.junit.Assert.*;

public class MediatingOntologiesUnitTest {
	
	@Test
	public void testItWorks() {
		
		int result = 1 + 1;
		assertEquals(2, result);
	}
	
	/*
	
	@Test
	public void utilsCreateSubFolderStructure(String[] args) {
		
		// Check that the folders are create properly from parent
		String parentPath = "/home/valentina/git-repos/test-data/test-folder-creation/";
		CreateComposedMappings compMapper = new CreateComposedMappings();
		compMapper.setAllPathsFromParent(parentPath);
		//this.basePath = parentPath + "store-mediating-ontologies/";
		//createSubDirectory(this.basePath);
		System.out.println(compMapper.basePath);
		//assert it exists
		
		//this.midPath = parentPath + "store-simple-mappings/";
		//createSubDirectory(this.midPath);
		System.out.println(compMapper.midPath);
		//assert it exists
		
		//this.outPath = parentPath + "store-composed-mappings/";
		//createSubDirectory(this.outPath);
		System.out.println(compMapper.outPath);

		//this.listFile = parentPath + "/logmap_top12_mediating_ontologies.txt";
		
		//System.out.println()
		
	}
	*/
	@Test
	public void testMappingSetsSubtraction() {
		//TODO create two test sets of mappings and subtract bigger \ smaller. 
		// TODO same but with smaller \ bigger
		// TODO empty sets
	}
	
}
