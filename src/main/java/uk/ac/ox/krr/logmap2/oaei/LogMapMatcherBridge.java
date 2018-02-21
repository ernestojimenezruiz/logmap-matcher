///*******************************************************************************
// * Copyright 2012 by the Department of Computer Science (University of Oxford)
// * 
// *    This file is part of LogMap.
// * 
// *    LogMap is free software: you can redistribute it and/or modify
// *    it under the terms of the GNU Lesser General Public License as published by
// *    the Free Software Foundation, either version 3 of the License, or
// *    (at your option) any later version.
// * 
// *    LogMap is distributed in the hope that it will be useful,
// *    but WITHOUT ANY WARRANTY; without even the implied warranty of
// *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *    GNU Lesser General Public License for more details.
// * 
// *    You should have received a copy of the GNU Lesser General Public License
// *    along with LogMap.  If not, see <http://www.gnu.org/licenses/>.
// ******************************************************************************/
//package uk.ac.ox.krr.logmap2.oaei;
//
//
//import java.io.IOException;
//import java.net.URL;
//
//
//import eu.sealsproject.platform.res.domain.omt.IOntologyMatchingToolBridge;
//import eu.sealsproject.platform.res.tool.api.ToolBridgeException;
//import eu.sealsproject.platform.res.tool.api.ToolException;
//import eu.sealsproject.platform.res.tool.api.ToolType;
//import eu.sealsproject.platform.res.tool.impl.AbstractPlugin;
//
//import uk.ac.ox.krr.logmap2.*;
//
//
//public class LogMapMatcherBridge extends AbstractPlugin implements IOntologyMatchingToolBridge {
//
//	/**
//	* LogMap 2 aligns two ontologies specified via their URL and returns the 
//	* URL of the resulting alignment, which should be stored locally.
//	* 
//	*/
//	public URL align(URL source, URL target) throws ToolBridgeException, ToolException {
//		
//		URL url_alignment;
//		
//		try {
//			//We read parameters
//			Parameters.readParameters();
//					
//			if (Parameters.allow_bioportal){
//				
//				LogMap2_OAEI_BioPortal logmap = new LogMap2_OAEI_BioPortal();
//				
//				logmap.align(source, target);
//				
//				url_alignment = logmap.returnAlignmentFile(); //Local-temporary file
//				
//			}
//			else{
//				LogMap2_OAEI logmap = new LogMap2_OAEI();
//			
//				logmap.align(source, target);
//				
//				url_alignment = logmap.returnAlignmentFile(); //Local-temporary file				
//			}
//			
//			return url_alignment;
//			
//		}
//		
//		catch (IOException e) {
//			throw new ToolBridgeException("Cannot create file for resulting alignment", e);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			throw new ToolException("Error extracting/cleaning/storing mappings with LogMap: ");
//			
//		}
//			
//		
//	}
//
//	/**
//	* Not considered in LogMap
//	*/
//	public URL align(URL source, URL target, URL inputAlignment) throws ToolBridgeException, ToolException {
//		throw new ToolException("functionality of called method is not supported");
//	}
//
//	/**
//	* No pre-resiquites in LogMap
//	*/
//	public boolean canExecute() {
//		return true;
//	}
//
//	/**
//	* The LogMap is an ontology matching tool. 
//	*/
//	public ToolType getType() {
//		return ToolType.OntologyMatchingTool;
//	}
//	
//	
//
//	
//
//}
