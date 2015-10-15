package edu.cmu.cs.lane.models;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.cmu.cs.lane.beans.AnalysisListBean;
import edu.cmu.cs.lane.beans.PatientModelsBean;

public class PatientModelsModel { 
	//static private Hashtable<Integer,PatientModelsBean> patientModels = new Hashtable<Integer, PatientModelsBean>(); //patientId->list of models

	static public boolean init(){ 
		/*
		Integer[] patientIds = PatientModel.getPatientIds();
		for (int i = 0; i<patientIds.length; i++) {
			ArrayList<Integer> models = new ArrayList<Integer>();
			models.add(1); 
			models.add(2);
			models.add(3);
			patientModels.put(i, new PatientModelsBean(i, models)); 
		}
		*/
		return true;
	}
	
	static public boolean isExist(int id){
		//return patientModels.containsKey(id);
		return true;
	}
	
	static public PatientModelsBean getPatientModels(int id){
		AnalysisListBean modelList = ModelsModel.getModels(true);
		PatientModelsBean patientModels = new PatientModelsBean(); 
		patientModels.modelIds = new ArrayList<Integer>();
		patientModels.patientId = id;
		for (int i=0; i< modelList.list.size(); i++){
			if (modelList.list.get(i).additionalInfo == null || modelList.list.get(i).additionalInfo.equalsIgnoreCase("") ){ //TODO: TEMP: just to filter out the regression analyses for the example
				patientModels.modelIds.add(modelList.list.get(i).id);
			}
		}
		return patientModels;
		
	}

}
