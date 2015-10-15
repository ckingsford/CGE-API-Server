package edu.cmu.cs.lane.beans;

import javax.xml.bind.annotation.XmlRootElement;
 

@XmlRootElement
public class PatientBean {
	public int id;
    public String name;
    public String info; //extended info in JSON format
 
    public PatientBean() {} // JAXB needs this
 
    public PatientBean(int id, String name) {
    	this.id = id;
    	this.name = name;
    }
    
    public void setInfo(String info) {
    	this.info = info;
    }
    public String getInfo() {
    	return this.info;
    }
}

