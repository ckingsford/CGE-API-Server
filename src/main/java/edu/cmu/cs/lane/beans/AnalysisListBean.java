/**
 * 
 */
package edu.cmu.cs.lane.beans;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;

/**
 * @author zinman
 *
 */
public class AnalysisListBean {
    public ArrayList<AnalysisSetDetails> list;
    public AnalysisListBean() {
    	list = new ArrayList<AnalysisSetDetails>();
    } // JAXB needs this
}
