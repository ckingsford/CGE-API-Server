package edu.cmu.cs.lane;

import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import edu.cmu.cs.lane.brokers.InitializeBrokers;
import edu.cmu.cs.lane.models.ModelsModel;
import edu.cmu.cs.lane.models.PatientModel;
import edu.cmu.cs.lane.models.PatientModelsModel;
import edu.cmu.cs.lane.pipeline.dataanalyzer.DataAnalyzersFactory;
import edu.cmu.cs.lane.pipeline.util.FactoryUtils;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.AbstractOptions;
import edu.cmu.cs.lane.settings.OptionsMySQL;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.utilities.OptionsAPI;

/*
 * 1. ModelRun (or model version)
 * 2. 
 * 3. Properties file should 'know' about data location (and available analyzers?) (relate to DataAnalysisPhaseController.properties and ShotgunAnalyzer.properties)
 * 4. Change Analyzers object to loadData() (different for multiple / one patient?) AnalyzeData() ApplyModel()
 * 5. Based on analyzer - the previous phases need to be run? (or alternatively how this need to be done)
 * 6. Need to think about Update mechanisms for code / models
 * 
 */

// package project as a 'fat jar': mvn -f genetic-pipeline/pom.xml clean compile assembly:single

/*
 * Main class.
 * 	
 * 
 * mvn clean test
 * mvn exec:java -Dexec.args="./properties/APIServer.properties" -Dexec.Xms256M -Dexec.Xmx5120M 
 * 
 * curl -X GET http://localhost:9000/cge/application.wadl
 * curl http://localhost:9000/cge/myresource
 * curl -i http://localhost:9000/cge/myresource
 *
 */


//@WebService - attempts to solve the JAXB problems of inheritance 
//@XmlSeeAlso({FeatureA.class, AbstractFeature.class})
public class Main { 
    // Base URI the Grizzly HTTP server will listen on
    public static String BASE_URI = "http://localhost:9000/cge/"; //temporary for testing purposes only

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("edu.cmu.cs.lane");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     * @throws ConfigurationException 
     */
    public static void main(String[] args) throws IOException, ConfigurationException {
    	if (args.length > 0){
    		Hashtable<String,String> externalClasses = new Hashtable<String, String>();
    		AbstractOptions optionsAPI;
			try {
				optionsAPI = FactoryUtils.classInstantiator(OptionsAPI.class.getCanonicalName());
				externalClasses.put(optionsAPI.getName(), OptionsAPI.class.getCanonicalName());
				
				Hashtable<String, String> propertiesOverrides = new Hashtable<String, String>();
				if (args.length > 1){
					for (int i = 1; i< args.length; i+=2){
						propertiesOverrides.put(args[i], args[i+1]);
					}
				}
				OptionsFactory.initialize(args[0], propertiesOverrides,externalClasses);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		InitializeBrokers.initializeStaticObjects();
    		DataAnalyzersFactory.initialize();
    		
    		
    		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()){
    			System.out.println("Loaded and initialized with " + args[0]);    			
    		}
    	}
    	/* attempts to solve the JAXB problems of inheritance 
    	try {
    		ClientConfig cc = new ClientConfig();
			JAXBContext.newInstance(FeatureA.class, AbstractFeature.class);
		} catch (JAXBException e) {
		}
		*/
    	BASE_URI = ((OptionsAPI) OptionsFactory.getOptions("APIserver")).getAPIServerURL();
    	PatientModel.init();
    	ModelsModel.init();
    	PatientModelsModel.init();
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl", BASE_URI));
       while(true){
    	   
       }
        //To stop on input
        //System.in.read();
        //server.stop();
    }
}

