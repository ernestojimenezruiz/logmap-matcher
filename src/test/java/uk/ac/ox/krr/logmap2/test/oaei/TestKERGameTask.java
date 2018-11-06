package uk.ac.ox.krr.logmap2.test.oaei;

public class TestKERGameTask extends TestOAEITrack{

	@Override
	protected void setUp() {
		
		SAVE_MAPPINGS = true;
		OUTPUT_FILE_TEMPLATE = "/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/KER-game-im/logmap-game-alignment";
		
		String uri_path = "file:/home/ejimenez-ruiz/Documents/OAEI_2018/Tracks/KER-game-im/";
		
		tasks.add(
				new OAEITask(
						uri_path + "game_source.n3",
						uri_path + "game_target.n3",
						uri_path + "alignment.rdf",
						"game"
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
