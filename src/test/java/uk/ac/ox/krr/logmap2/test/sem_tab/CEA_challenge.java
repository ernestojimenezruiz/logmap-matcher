package uk.ac.ox.krr.logmap2.test.sem_tab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CEA_challenge {

	
	public CEA_challenge(String folder, String dbpedia_ontology) {
		
		
		try (Stream<Path> paths = Files.walk(Paths.get("/home/you/Desktop"))) {
		    
			paths
		        .filter(Files::isRegularFile)
		        .forEach(printConsumer); //System.out::println
		    
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		
	}
	
	
	Consumer<Path> printConsumer = new Consumer<Path>() {
	    public void accept(Path name) {
	        System.out.println(name.getFileName());
	    };
	};
	
	
	
	
	
	
	
}
