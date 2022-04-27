package uk.ac.ox.krr.logmap2.test.oaei;


/**
 *
 * @author ernesto
 * Created on 10 Oct 2021
 *
 */
public class TestKnowledgeGraphTrack2021 extends TestOAEITrack{

	
	TestKnowledgeGraphTrack2021(){
		super();
	}
	
	
	 
	protected void setUp(){
	
		
		
		/*tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v3/suite/marvelcinematicuniverse-marvel/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v3/suite/marvelcinematicuniverse-marvel/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v3/suite/marvelcinematicuniverse-marvel/component/reference.xml",
						"marvelcinematicuniverse-marvel"
				));
		*/
		
		
		
		/*tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/marvelcinematicuniverse-marvel/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/marvelcinematicuniverse-marvel/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/marvelcinematicuniverse-marvel/component/reference.xml",
						"marvelcinematicuniverse-marvel"
				));
		*/
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/memoryalpha-memorybeta/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/memoryalpha-memorybeta/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v4/suite/memoryalpha-memorybeta/component/reference.xml",
						"memory-alpha-memory-beta"
				));
		
		
	}
	
	
	public static void main(String[] args){
		
		TestKnowledgeGraphTrack2021 test = new TestKnowledgeGraphTrack2021();
		
		
		
		try {
			System.out.println("KG 2021 z"); 
			test.evaluateTasks(); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
}
