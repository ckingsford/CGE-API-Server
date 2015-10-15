package edu.cmu.cs.lane.beans;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer;


@XmlRootElement
public class ModelBean { 
	public int id;
	public String name; //TODO: temporary
    public AnalysisSetDetails model;
 
    public ModelBean() {} // JAXB needs this
 
    public ModelBean(int id, String name) {
    	this.id = id;
    	this.name = name;
    }
    
}

