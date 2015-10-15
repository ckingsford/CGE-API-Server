package edu.cmu.cs.lane.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cs.lane.beans.ModelBean;
import edu.cmu.cs.lane.datatypes.model.AnalysisBean;
import edu.cmu.cs.lane.models.ModelsModel;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsMySQL;
import edu.cmu.cs.lane.utilities.OptionsAPI;


@Path("/models")
public class ModelsResource {
    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list() {
    	return Response.ok(ModelsModel.getModels(true))
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
 
    }
    
    @GET
    @Path("/{modelId}")
    @Produces("application/json")
    public Response getModel(@PathParam("modelId") int modelId, @DefaultValue("false") @QueryParam("full") Boolean full) {
    	return Response.ok(ModelsModel.getModel(modelId,full))
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
 
    }
    
    @GET
    @Path("/remove/{modelId}")
    @Produces("application/json")
    public Response removeModel(@PathParam("modelId") int modelId) {
    	ModelsModel.removeModel(modelId);
    	return Response.ok(Integer.toString(modelId))
        		.header("Access-Control-Allow-Origin", "*")
  		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
  		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
 
    }
    
    @GET
    @Path("/run")
    @Produces("application/json")
    public Response run(@DefaultValue("none") @QueryParam("type") String type, @QueryParam("override") List<String> overrides) {
    	System.out.println(type);
    	type = type.toLowerCase();

    	if (type.equalsIgnoreCase("none")){
    		return Response.ok("No algorithm was provided")
    				.header("Access-Control-Allow-Origin", "*")
    		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
    		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();	
    	}
    	
    	JSONObject json = new JSONObject();
    	
    	Runtime r = Runtime.getRuntime();
    	Process p;
    	StringBuilder sb = new StringBuilder();

    	//String command = "uname -a"; //to get the system information automatically if needed
    	String command = "java -cp genetic-pipeline-1.0-jar-with-dependencies.jar -Xmx4048M edu.cmu.cs.lane.runner.RunPipeline ";
    	if (type.equalsIgnoreCase("gflasso"))
    		command += "./runSamples/regression-sample.properties ";
    	if (type.equalsIgnoreCase("lasso"))
    		command += "./runSamples/classification-sample.properties ";
    	if (type.equalsIgnoreCase("logreg"))
    		command += "./runSamples/classification-sample.properties analysis-algorithms logreg ";
    	if (overrides != null){
	    	for (int i =0; i<overrides.size(); i++){
	    		command += overrides.get(i);
	    		if (overrides.get(i).contains("mysql")){ //we need to override the mysql definitions with those of the API server settings
	    			command += " db-host " + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBHost() +" ";
	    			command += "db-port " + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBPort() +" ";
	    			command += "db-name " + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBName() +" ";
	    			command += "db-username " + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBUsername() +" ";
	    			command += "db-password " + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBPassword() +" ";
	    		}
	    	}
    	}
		try {
			System.out.println(command);
			p = r.exec(command); 
			p.waitFor();
			String line = "";
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));	    	
	    	while ((line = b.readLine()) != null) {
	    	    System.out.println(line);
	    		sb.append(line);
	    	}
			json.put("output", sb.toString());
			
			b = new BufferedReader(new InputStreamReader(p.getErrorStream()));	    	
	    	while ((line = b.readLine()) != null) {
	    	  System.out.println(line);
	    		sb.append(line);
	    	}

    	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    	return Response.ok((String)json.toString())
				.header("Access-Control-Allow-Origin", "*")
		      .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
		      .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
    
    
}
