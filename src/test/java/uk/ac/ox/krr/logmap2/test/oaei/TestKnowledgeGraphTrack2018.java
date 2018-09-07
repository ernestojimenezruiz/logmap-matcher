/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.ac.ox.krr.logmap2.test.oaei;


/**
 *
 * @author ernesto
 * Created on 6 Sep 2018
 *
 */
public class TestKnowledgeGraphTrack2018 extends TestOAEITrack{

	
	TestKnowledgeGraphTrack2018(){
		super();
	}
	
	
	
	protected void setUp(){
	
		/*tasks.add(
					new OAEITask(
							"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/darkscape-oldschoolrunescape/component/target/",
							"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/darkscape-oldschoolrunescape/component/target/",
							"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/reference.xml",
							"test-memoryalpha-memoryalpha"							
					));
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/source/",
						"",
						"test-oldschoolrunescape-oldschoolrunescape"
						
				));
		
		*/
		
			
			
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/darkscape-oldschoolrunescape/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/darkscape-oldschoolrunescape/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/darkscape-oldschoolrunescape/component/reference.xml",
						"darkscape-oldschoolrunescape"
				));
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-darkscape/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-darkscape/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-darkscape/component/reference.xml",
						"runescape-darkscape"
				));
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-oldschoolrunescape/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-oldschoolrunescape/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/runescape-oldschoolrunescape/component/reference.xml",
						"runescape-oldschoolrunescape"
				));
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/heykidscomics-dc/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/heykidscomics-dc/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/heykidscomics-dc/component/reference.xml",
						"heykidscomics-dc"
				));
		
	
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-dc/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-dc/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-dc/component/reference.xml",
						"marvel-dc"
				));
		
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-heykidscomics/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-heykidscomics/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/marvel-heykidscomics/component/reference.xml",
						"marvel-heykidscomics"
				));
		
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-memory-beta/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-memory-beta/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-memory-beta/component/reference.xml",
						"memory-alpha-memory-beta"
				));
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-alpha-stexpanded/component/reference.xml",
						"memory-alpha-stexpanded"
				));
		
		
		
		tasks.add(
				new OAEITask(
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-beta-stexpanded/component/source/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-beta-stexpanded/component/target/",
						"http://oaei.webdatacommons.org/tdrs/testdata/persistent/knowledgegraph/v1/suite/memory-beta-stexpanded/component/reference.xml",
						"memory-beta-stexpanded"
				));
		
	}
	
	
	public static void main(String[] args){
		
		TestKnowledgeGraphTrack2018 test = new TestKnowledgeGraphTrack2018();
		
		try {
			test.evaluateTasks();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
}
