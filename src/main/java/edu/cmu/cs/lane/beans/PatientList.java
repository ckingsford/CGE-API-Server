/**
 * 
 */
package edu.cmu.cs.lane.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author zinman
 *
 */
@XmlRootElement
public class PatientList {
	public ArrayList<PatientBean> patients;


	/**
	 * 
	 */
	public PatientList() {
		patients = new ArrayList<PatientBean>();
	}
	
	/**
	 * @return the patients
	 */
	public ArrayList<PatientBean> getPatients() {
		return patients;
	}

	/**
	 * @param patients the patients to set
	 */
	public void setPatients(ArrayList<PatientBean> patients) {
		this.patients = patients;
	}

	public void setPatients(Collection<PatientBean> patients) {
		this.patients.addAll(patients);
	}
	
}
