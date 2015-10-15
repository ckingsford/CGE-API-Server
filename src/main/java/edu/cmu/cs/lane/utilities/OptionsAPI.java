package edu.cmu.cs.lane.utilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.settings.AbstractOptions;
import edu.cmu.cs.lane.settings.OptionsGeneral;


public class OptionsAPI extends AbstractOptions{
	
	private String APIServerURL = "http://localhost:9000/cge/";
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "APIserver";
	}
	
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config)
			throws ConfigurationException {
		if (config.containsKey("APIServerURL")) {
			this.setAPIServerURL(config.getProperty("APIServerURL").toString());
		}
		
	}



	public String getAPIServerURL() {
		return APIServerURL;
	}

	public void setAPIServerURL(String APIServerURL) {
		this.APIServerURL = APIServerURL;
	}

	
}