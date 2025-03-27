package uk.ac.ox.krr.logmap2.test.oaei;

public class TestAlignmentModule2025 extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true; 
		//OUTPUT_FILE_TEMPLATE: will generate files logmap-alignment-food.owl, logmap-alignment-food.txt
		//PATH = "/home/ernesto/Documents/Datasets/Food/logmap-alignment"; +  + "-"+task.getTaskName();
		PATH = "C:/Users/Ernes/OneDrive/Documents/City-module/alignment-2025/logmap/";
		
			 
		String path_students = "C:/Users/Ernes/OneDrive/Documents/City-module/alignment-2025/students/";
		String path_targets = "C:/Users/Ernes/OneDrive/Documents/City-module/alignment-2025/targets/";
		String uri_path_students = "file:/" + path_students;
		String uri_path_targets = "file:/" + path_targets;
		
		
		/*
		
		tasks.add(
				new OAEITask(
						uri_path_students + "ridwan-online-sales.owl",  //source
						uri_path_targets + "product-onto-chatgpt.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"ridwan-product-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "roquain-movies.owl",  //source
						uri_path_targets + "movieontology.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"roquain-movies-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "rs-fitness.owl",  //source
						uri_path_targets + "helifit.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"rs-helifit-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "shaha-movies.owl",  //source
						uri_path_targets + "movieontology.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"shaha-mo-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "shuai-nba.owl",  //source
						uri_path_targets + "basketball.ttl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"shuai-basketball-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "siddique_formula1.rdf",  //source
						uri_path_targets + "formula1.ttl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"siddique-f1-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "stellaie-fraud.rdf",  //source
						uri_path_targets + "ontofic.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"stellaie-ontofic-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "yusuf-movies.owl",  //source
						uri_path_targets + "movieontology.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"yusuf-mo-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "abdalaziz-music.owl",  //source
						uri_path_targets + "musicontology.rdfs",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"abdalaziz-music-reference"
				));
		
		*/
		
		tasks.add(
				new OAEITask(
						uri_path_students + "ahmad-landslides.rdf",  //source
						uri_path_targets + "landslides-onto-chatgpt.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"ahmad-landslides-reference"
				));
		
		
		/*
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "aziz-movies.owl",  //source
						uri_path_targets + "movieontology.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"aziz-mo-reference"
				));
		
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "city-watch-traffic.rdf",  //source
						uri_path_targets + "safeon_roadacci.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"city-watch-safeon-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "harrison-airbnb.owl",  //source
						uri_path_targets + "airbnb-chatgpt.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"harrison-airbnb-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "ninad-movies.owl",  //source
						uri_path_targets + "movieontology.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"ninad-mo-reference"
				));
		
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "laura-thyroid-cancer.owl",  //source
						uri_path_targets + "doid.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"laura-doid-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "navid_malware.rdf",  //source
						uri_path_targets + "malont.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"navid-malont-reference"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path_students + "ontogeeks-heart.rdf",  //source
						uri_path_targets + "doid.owl",  //target
						//uri_path + "reference.rdf",   //reference mappings if any
						"", //no reference
						"ontogeeks-doid-reference"
				));
		
		*/
		
	}
	
	
	public static void main(String[] args){
		
		TestAlignmentModule2025 test = new TestAlignmentModule2025();
		
		
		try {
			test.evaluateTasks();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}
