/**
 * 
 */
package edu.cmu.cs.lane;


import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.lane.annotations.AnnotatorsFactory;
import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;
import edu.cmu.cs.lane.annotations.SnpEffFeaturesAnnotator;
import edu.cmu.cs.lane.beans.PatientFeatureMatrixBean;
import edu.cmu.cs.lane.brokers.CGEModelBroker;
import edu.cmu.cs.lane.brokers.InitializeBrokers;
import edu.cmu.cs.lane.brokers.MySQLConnector;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.models.FeaturesModel;
import edu.cmu.cs.lane.models.ModelsModel;
import edu.cmu.cs.lane.models.PatientModel;
import edu.cmu.cs.lane.models.PatientModelsModel;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;
import edu.cmu.cs.lane.settings.OptionsMySQL;
import edu.cmu.cs.lane.utilities.OptionsAPI;

/**
 * @author zinman
 *
 */
public class PatientResourceTest {

    private HttpServer server;
    private WebTarget target;
    
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        //c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());
        OptionsAPI options = new OptionsAPI();
        String BASE_URI = options.getAPIServerURL();

/*		OptionsMySQL optionsMysql = new OptionsMySQL();
		OptionsFactory.initializeEmpty();
		OptionsFactory.addOptions("mysql", optionsMysql);
		OptionsFactory.addOptions("APIserver", options);
		MySQLConnector.initialize();
        target = c.target(BASE_URI);
        
        PatientModel.init();
    	ModelsModel.init();
    	PatientModelsModel.init();
*/	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void test() {
		
		//putting everything in comment just for clean purposes - checked and verified to be working
		/* 
		OptionsFactory.initializeEmpty();
		
		OptionsMySQL mockOptions  = new OptionsMySQL();
		OptionsFactory.addOptions("mysql", mockOptions);
		MySQLConnector.initialize();
				
		OptionsLoadGenetic optGenetics = new OptionsLoadGenetic();
		optGenetics.setGeneticInputFilePattern("chr([\\d]+)\\.tab.txt");
		OptionsFactory.addOptions("loadGenetic", optGenetics);
		
		OptionsGeneral optGeneral = new OptionsGeneral();
		optGeneral.setWorkingFolder("./patientsFolder/");
		optGeneral.setLoadGeneticSource("file");
		optGeneral.setGeneticInputOrientation("columns-as-patients");
		OptionsFactory.addOptions("general", optGeneral); //override previous general	
		
		InitializeBrokers.initializeStaticObjects();
		
		//load model
		String model_id_s = "24";
		System.out.println("Attempting to load model:");
		ArrayList<CGEModel> model = CGEModelBroker.LoadModelMySQL(Integer.parseInt(model_id_s), true);
		System.out.println(model.get(0).getDetails().toString());
		
	    //load patients
		String patient_id_s = "0";
        SamplesGeneticData geneticData = PatientModel.loadPatientInfo(patient_id_s, model);
    	System.out.println("patients count: " +geneticData.getSamplesCount() + "\t features count: " + geneticData.getFeaturesCount());

        //create annotated object
        PatientFeatureMatrixBean patientFeatureMatrixOutput =  new PatientFeatureMatrixBean(geneticData.getFeaturesCount());   
 
        for (int i=0; i<geneticData.getFeaturesCount();i++){        	
        	if (geneticData.data.get(i) == 1 || geneticData.data.get(i) == 2){
        		SampleGeneticFeatureBean feature = new SampleGeneticFeatureBean();
        		feature.id = geneticData.labels.getColumnLabel(i);
        		feature.rsId = geneticData.labels.getColumnExtLabel(i);
        		feature.value = (Byte) (byte) geneticData.data.get(i);
        		patientFeatureMatrixOutput.add(feature);
        	}
        }  
        
        for (int i=0; i<patientFeatureMatrixOutput.getFeatures().size(); i++){
        	System.out.println(patientFeatureMatrixOutput.getFeatures().get(i).toString());
        }
        
		AnnotatorsFactory.initialize(null);
		SnpEffFeaturesAnnotator annotator = (SnpEffFeaturesAnnotator) AnnotatorsFactory.getAnnotator("snpEff");
		patientFeatureMatrixOutput.setFeatures(annotator.annotate(patientFeatureMatrixOutput.getFeatures()));

		
		int a = 3;
		a++;
		*/
		
        //Response responseMsg = target.path("patients/list").request().get();
        //String responseMsg = target.path("patients/list").request().get(String.class);
		//String responseMsg = target.path("patients/0/models/1").request().get(String.class);
        //System.out.println(responseMsg);
		

	}

}
