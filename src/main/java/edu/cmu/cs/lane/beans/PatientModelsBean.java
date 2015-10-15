package edu.cmu.cs.lane.beans;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

 

@XmlRootElement
public class PatientModelsBean {
	public int patientId;
	public ArrayList<Integer> modelIds;

    public PatientModelsBean() {} // JAXB needs this
 
    public PatientModelsBean(int patientId, ArrayList<Integer>modelIds) {
    	this.patientId = patientId;
    	this.modelIds = new ArrayList<Integer>();
    	this.modelIds.addAll(modelIds);
    }
}
