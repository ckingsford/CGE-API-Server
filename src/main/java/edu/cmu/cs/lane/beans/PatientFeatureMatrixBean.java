/**
 * 
 */
package edu.cmu.cs.lane.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;
import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;



/**
 * @author zinman
 *
 */
@XmlRootElement
public class PatientFeatureMatrixBean {

	ArrayList<SampleGeneticFeatureBean> features;
	/**
	 * 
	 */
	public PatientFeatureMatrixBean() { //JAXB needs this
		features = new ArrayList<SampleGeneticFeatureBean>();
	}
	
	/**
	 * 
	 */
	public PatientFeatureMatrixBean(int featureCount) { 
		features = new ArrayList<SampleGeneticFeatureBean>(featureCount);
	}
	
	public void add(SampleGeneticFeatureBean feature) {
		features.add(feature);
	}

	/**
	 * @return the features
	 */
	public ArrayList<SampleGeneticFeatureBean> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(ArrayList<SampleGeneticFeatureBean> features) {
		this.features = features;
	}
	
	public void addAll(Collection<? extends SampleGeneticFeatureBean> features){
		this.features.addAll(features);
	}
	public SampleGeneticFeatureBean get(int index){
		return features.get(index);
	}
	
	public int size(){
		return features.size();
	}
}
