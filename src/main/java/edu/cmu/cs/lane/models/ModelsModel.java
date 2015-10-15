package edu.cmu.cs.lane.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import edu.cmu.cs.lane.brokers.AnalysisSetBroker;
import edu.cmu.cs.lane.brokers.CGEModelBroker;
import edu.cmu.cs.lane.beans.AnalysisListBean;
import edu.cmu.cs.lane.beans.ModelBean;
import edu.cmu.cs.lane.datatypes.model.AnalysisBean;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;

public class ModelsModel { 

	static private AnalysisListBean analysisListBean = new AnalysisListBean();
	static private Hashtable<Integer,Integer> analysisHash = new Hashtable<Integer, Integer>();  //map between id to index
	static private long checksum = -1;
	
	static public void init(){ 
    	checksum = loadModelsFromDatabase(true);  
	}
	
	static public AnalysisListBean getModels(boolean getDetails) {
		long newChecksum = AnalysisSetBroker.getAnalysesListChecksum();
		if (checksum != newChecksum){
			checksum = loadModelsFromDatabase(getDetails);	
		}
		return analysisListBean;
	}
	
	static public int getModelsCount() {
		return analysisHash.size();
	}
	
	static public long loadModelsFromDatabase(boolean loadDetails){
		long newChecksum = AnalysisSetBroker.getAnalysesListChecksum();
		if (checksum != newChecksum){
			analysisListBean.list.clear();
			analysisListBean.list.addAll(AnalysisSetBroker.getAnalysesList(null));
			
			
			
			analysisHash.clear();
			for (int i=0; i<analysisListBean.list.size(); i++){
				//getting the details of the first model in the set
				analysisListBean.list.get(i).analyses = new ArrayList<AnalysisBean>();
				if (loadDetails){
					AnalysisBean firstModelDetails = getModel(analysisListBean.list.get(i).id, false);
					analysisListBean.list.get(i).analyses.add(firstModelDetails);
				}
				analysisHash.put(analysisListBean.list.get(i).id, i);
			}
		}
		return newChecksum;
	}

	/**
	 * @param modelId
	 * @return
	 */
	public static AnalysisBean getModel(int analysisSetId, boolean retrieveFull) {
		AnalysisBean analysisBean = new AnalysisBean();
		//analysisBean.name = analysisListBean.list.get(analysisHash.get(new Integer(analysisSetId))).name; //analysisListBean - not necessarily filled
		ArrayList<CGEModel> models = CGEModelBroker.LoadModelMySQL(analysisSetId,retrieveFull);
		if (models!=null && models.size() > 0){
			analysisBean.details = models.get(0).details; //TODO: currently getting only the first in the list
	
			if (retrieveFull){ //add features
				for(int i=0; i< models.size(); i++){
					analysisBean.model.addAll(models.get(i).getFeatures());
				}
			}
		}
		return analysisBean;
	}

	/**
	 * @param modelId
	 * @return
	 */
	public static Boolean removeModel(int modelId) {
		CGEModelBroker.RemoveModel(modelId);
		return true;
	}
	
}
