package edu.cmu.cs.lane.services;

import java.util.ArrayList;
import java.util.HashSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cs.lane.annotations.AnnotatorsFactory;
import edu.cmu.cs.lane.annotations.SnpEffFeaturesAnnotator;
import edu.cmu.cs.lane.beans.PatientBean;
import edu.cmu.cs.lane.beans.PatientFeatureMatrixBean;
import edu.cmu.cs.lane.beans.PatientList;
import edu.cmu.cs.lane.beans.PatientModelsBean;
import edu.cmu.cs.lane.brokers.CGEModelBroker;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;
import edu.cmu.cs.lane.datatypes.dataset.SampleLabelBean;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.models.FeaturesModel;
import edu.cmu.cs.lane.models.PatientModel;
import edu.cmu.cs.lane.models.PatientModelsModel;
import edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer;
import edu.cmu.cs.lane.pipeline.dataanalyzer.DataAnalyzersFactory;
import edu.cmu.cs.lane.pipeline.dataanalyzer.ExampleAnalyzer;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.LookUpDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.VCFReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsMySQL;

@Path("/patients")
public class PatientsResource {
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list() {
    	PatientList patientList = new PatientList();
    	patientList.setPatients(PatientModel.getPatients());
    	
    	return Response.ok(patientList)
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response patient(@PathParam("id") int id) {
    	PatientBean patient = null;
    	if (PatientModel.isExist(id)) patient = PatientModel.getPatient(id);
    	if (PatientModel.getPatient(id).getInfo() == null || PatientModel.getPatient(id).getInfo() == ""){
    		PatientModel.getPatient(id).setInfo(PatientModel.mockPatientInfo());
    	}
    	return Response.ok(patient)
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
    }
    
    @GET
    @Path("/{id}/models")
    @Produces("application/json")
    public Response patientModels(@PathParam("id") int id) {
    	PatientModelsBean patientModels = null;
    	if (PatientModelsModel.isExist(id)) patientModels = PatientModelsModel.getPatientModels(id);

    	return Response.ok(patientModels)
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();

    }
    
    
    @GET
    @Path("/{id}/models/{model_id}")
    @Produces("application/json")
    //@Produces(MediaType.TEXT_PLAIN)
   // public PatientFeatureMatrixBean testModel(@PathParam("id") int id) {
    public Response applyModel(@PathParam("id") String patient_id_s, @PathParam("model_id") String model_id_s) {

       	//get a model (CGEModel) from the current database based on the requested analyzer and specific model id run    	
    	ArrayList<CGEModel> modelList = CGEModelBroker.LoadModelMySQL(Integer.parseInt(model_id_s), true);

        //get patientInfo (filtered by model)
    	SamplesDataset samplesDataset = new SamplesDataset();
    	samplesDataset.setGeneticData(PatientModel.loadPatientInfo(patient_id_s, modelList));
        
        //create annotated object
        PatientFeatureMatrixBean patientFeatureMatrix =  new PatientFeatureMatrixBean(samplesDataset.getGeneticData().getFeaturesCount());   
        for (int i=0; i<samplesDataset.getGeneticData().getFeaturesCount();i++){        	
        	if (samplesDataset.getGeneticData().getData(0, i) == 1 || samplesDataset.getGeneticData().getData(0, i) == 2){
        		SampleGeneticFeatureBean feature = new SampleGeneticFeatureBean();
        		feature.id = samplesDataset.getGeneticData().getFeatureName(i);
        		feature.rsId = samplesDataset.getGeneticData().getExtendedFeatureInfo(i);
        		feature.value = (Byte) (byte) samplesDataset.getGeneticData().getData(0,i);

        		patientFeatureMatrix.add(feature);
        	}
        }  
		AnnotatorsFactory.initialize(null);
		SnpEffFeaturesAnnotator annotator = (SnpEffFeaturesAnnotator) AnnotatorsFactory.getAnnotator("snpEff");
		patientFeatureMatrix.setFeatures(annotator.annotate(patientFeatureMatrix.getFeatures()));
        
		CGEPrediction predictionOutput = new CGEPrediction();
        //apply model
        //=================
		for(int i=0; i< modelList.size(); i++){
	    	String usedAnalyzer = modelList.get(i).details.algorithmName;
	    	AbstractAnalyzer analyzer = DataAnalyzersFactory.create(usedAnalyzer);
			ArrayList<CGEPrediction> predictions = analyzer.applyModel(modelList.get(i), samplesDataset);
			predictionOutput = predictions.get(0);//only one patient was submitted
		}
		predictionOutput.setFeatures(patientFeatureMatrix.getFeatures());
		
		//heuristic - add feature weight based on the specific variants
		double sumWeights = 0;
		for (int f=0; f<patientFeatureMatrix.size();f++){
			for (int m=0; m<modelList.size(); m++){
				if (modelList.get(m).hasFeature(patientFeatureMatrix.get(f).id)){
					for (int j = 0; j < modelList.get(m).get(patientFeatureMatrix.get(f).id).size(); j++)
						sumWeights += Math.abs(modelList.get(m).get(patientFeatureMatrix.get(f).id).get(j).val);
				}
			}
		}
		for (int f=0; f<patientFeatureMatrix.size();f++){
			for (int m=0; m<modelList.size(); m++){
				if (modelList.get(m).hasFeature(patientFeatureMatrix.get(f).id)){
					double w = 0;
					for (int j = 0; j < modelList.get(m).get(patientFeatureMatrix.get(f).id).size(); j++){
						w+=Math.abs(modelList.get(m).get(patientFeatureMatrix.get(f).id).get(j).val);
					}
					patientFeatureMatrix.get(f).weight = w / sumWeights;
				}
			}
		}
        return Response.ok(predictionOutput)
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
  		  
    	
    }
    
   
    
    
    /*
     * Construct a JsonObject:
     * JsonObject myObject = Json.createObjectBuilder()
        .add("name", "Agamemnon")
        .add("age", 32)
        .build();
     */
    
    
    
    /*
     * 
     *  @Path("/{order}")
   @PUT
   @Produces("text/html")
   public String create(@PathParam("order") String order, @QueryParam("customer_name") String customerName)
   {
      orders.put(order, customerName);
      return "Added order #" + order;
   }
   */
   
    /* - attempts to solve the JAXB problems of inheritance
    @GET
    @Path("test")
    @Produces("application/json")   
    public Response test(){
    	House house = new House();
    	FeatureA feature = new FeatureA();
    	feature.fieldA = "AAA";
    	feature.abstractField = "abstract";
    	house.feature = feature;
    	
    	return Response.ok(house)
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();

    }
   */

    
}
