package uk.ac.ox.krr.logmap2.test.oaei;

public class TestKERGameTask extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true;
		OUTPUT_FILE_TEMPLATE = "/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/KER-game-im/v2/logmap-game-v2-alignment";
		
		String uri_path = "file:/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/KER-game-im/";
		
		tasks.add(
				new OAEITask(
						uri_path + "v1/game_source.n3",
						uri_path + "v1/game_target.n3",
						uri_path + "v1/alignment.rdf",
						"game-v1"
				));
		
		
		tasks.add(
				new OAEITask(
						uri_path + "v2/game_source.n3",
						uri_path + "v2/game_target.n3",
						uri_path + "v2/alignment.rdf",
						"game-v2"
				));
		
		
	}
	
	
public static void main(String[] args){
		
		TestKERGameTask test = new TestKERGameTask();
		
		try {
			test.evaluateTasks();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
