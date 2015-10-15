package edu.cmu.cs.lane.models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cs.lane.beans.PatientBean;
import edu.cmu.cs.lane.beans.PatientFeatureMatrixBean;
import edu.cmu.cs.lane.brokers.load.CGELoadGeneticCenter;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.dataset.AbstractMatrixSampleDataType;
import edu.cmu.cs.lane.datatypes.dataset.BatchInfo;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.LookUpDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.VCFReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.utilities.OptionsAPI;

public class PatientModel {

	static private Hashtable<Integer,PatientBean> patients = new Hashtable<Integer, PatientBean>();  
	static private boolean initialized = false;
	
	static public boolean init(){ 
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader("./patientsFolder/patients.txt"));//TODO: take from a database through the CGELoadClinicalCenter
	        String line = br.readLine();
	        line = br.readLine(); //skipping header
	        int i = 0;
	        while (line != null) {
	        	//System.out.println(line);
	            String[] patientRow = line.split("\t");
	            if (patientRow.length > 1){
		            String name = patientRow[0] + " " + patientRow[1];
			    	patients.put(i, new PatientBean(i,name));
			    	i++;
	            }
	            line = br.readLine();
	        }
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
	    initialized = true;
		return true;
	}
	
	static public SamplesGeneticData loadPatientInfo(String patient_id_s, ArrayList<CGEModel> model) {

		ArrayList<AbstractDataFilter> dataFilters = new ArrayList<AbstractDataFilter>(1);
    	LookUpDataFilter lookupDataFilter = new LookUpDataFilter(); //Data filter -  based on the features in the model
    	for (int m = 0; m < model.size(); m++){
	    	ArrayList<String> ids = new ArrayList<String>(model.get(m).size());
	    	for (int i=0; i<model.get(m).getFeatures().size();i++){
	    		ids.add(model.get(m).getFeatures().get(i).id);
	    	}
	    	lookupDataFilter.populateHashByPosition(ids);
	    	//lookupDataFilter.populateHashByRsID(ids);
    	}
    	dataFilters.add(lookupDataFilter);
		
    	ArrayList<ArrayList<BatchInfo>> batches = CGELoadGeneticCenter.getBatchIDs();
    	ArrayList<BatchInfo> groupIds = new ArrayList<BatchInfo>(); 
    	groupIds.add(batches.get(0).get(0));//TODO: there is no background info and the current assumption the genetic information of this patient is not split between multiple batches
    	ArrayList<SamplesGeneticData> patientGeneticInfo = CGELoadGeneticCenter.loadBatch(groupIds, dataFilters); //return value should potentially be an array with only one element corresponding to one patient
    	

        
    	
       
        int[] patientIndex = new int[1]; 
        patientIndex[0] = Integer.parseInt(patient_id_s);//TODO: change to get from hash based on other identifiers
        AbstractMatrixSampleDataType outData = patientGeneticInfo.get(0).sliceBySamples(patientIndex);
        return new SamplesGeneticData(outData);
        
    	/*
        ArrayList<String> samplesIds = new ArrayList<String>();         
        for(String sample:patientGeneticInfo.get(0).getSamplesNames()){ 
        	samplesIds.add(sample);
        }
        SamplesGeneticData patientGeneticData = new SamplesGeneticData(); 
        patientGeneticData.data = patientGeneticInfo.get(0).data.getRows(patientIndex);
        patientGeneticData.labels.setColumnLabels(patientGeneticInfo.get(0).labels.getColumnLabels());
        patientGeneticData.labels.setColumnExtendedInfo(patientGeneticInfo.get(0).labels.getColumnExtendedInfo());
        String[] rowLabel = new String[1];
        rowLabel[0] = patientGeneticInfo.get(0).labels.getRowLabel(patientIndex[0]);
        patientGeneticData.labels.setRowLabels(rowLabel);
        return patientGeneticData;
        */
	}
	
	static public boolean isInitialized(){
		return initialized;
	}
	
	static public Collection<PatientBean> getPatients() {
		return patients.values();
	}
	
	static public Integer[] getPatientIds() {
		return patients.keySet().toArray(new Integer[patients.size()]);
	}
	
	static public int getPatientCount() {
		return patients.size();
	}
	
	static public boolean isExist(int id){
		return patients.containsKey(id);
	}
	
	static public PatientBean getPatient(int id){
		return patients.get(id);
	}
	
	static public String mockPatientInfo(){
		JSONObject patientInfo = new JSONObject();
		Random rand = new Random();

		try {
			//mock birthdate
			int day = rand.nextInt(31);
			int month = rand.nextInt(12);
			int year = 1960 + rand.nextInt(33);
			patientInfo.put("Birthdate", month+"/"+day+"/"+year);
			
			//mock gender
			int genderProbability = rand.nextInt(100);
			String gender = genderProbability >30 ? "Male" : "Female";
			patientInfo.put("Gender", gender);
			
			//mock MR
			int mr = rand.nextInt(999999);
			patientInfo.put("MR", mr);
			
			//mock Acct No.
			int acctNo = rand.nextInt(99999);
			patientInfo.put("acctNo", acctNo);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return patientInfo.toString();
	}
	
	 
}
