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
public class OAEITask {
	
	private String uri_source;
	private String uri_target;
	private String reference;
	private String task_name;

	
	public OAEITask(String source, String target, String ref, String task_name){
		setSource(source);
		setTarget(target);
		setReference(ref);
		setTaskName(task_name);
		
	}


	/**
	 * @return the uri_source
	 */
	public String getSource() {
		return uri_source;
	}


	/**
	 * @param uri_source the uri_source to set
	 */
	public void setSource(String uri_source) {
		this.uri_source = uri_source;
	}


	/**
	 * @return the uri_target
	 */
	public String getTarget() {
		return uri_target;
	}


	/**
	 * @param uri_target the uri_target to set
	 */
	public void setTarget(String uri_target) {
		this.uri_target = uri_target;
	}


	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}


	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}


	/**
	 * @return the task_name
	 */
	public String getTaskName() {
		return task_name;
	}


	/**
	 * @param task_name the task_name to set
	 */
	public void setTaskName(String task_name) {
		this.task_name = task_name;
	}
	
	
	
}
