package Server;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Response {
	private String response;
	private String errorMessage;
	private ArrayList<Resource> responseList;
	public Response(boolean response, String errorMessage)
	{
		if(response == true)
			this.response = "success";
		else
			this.response = "error";
		this.errorMessage = errorMessage;
	}
	public Response()
	{
		this.response = null;
		this.errorMessage = null;
		this.responseList = null;
	}
	public ArrayList<Resource> getResourceList(){
	      return new ArrayList<Resource>(responseList); // new copy
	}
	
	public void setResourceList(ArrayList<Resource> responseList){
	      this.responseList = responseList;
	}
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public JSONObject toJSON(){
	    JSONObject reply = new JSONObject();
	    JSONObject resourceObject = null;
	    switch(response)
	    {
	    case "success": 
	    	reply.put("response", "success");
	    	if(responseList.isEmpty() == false)
	    	{
	    		int iterator = 0;
	    		for(Resource s:responseList)
	    		{
	    			iterator++;
	    			JSONObject tempObject = s.toJSON();
	    			reply.put(Integer.toString(iterator), tempObject);
	    		
	    		}
	    		reply.put("resultSize", Integer.toString(iterator));
	    		
	    	}
	    	break;
	    case "error":
	    	reply.put("response", this.errorMessage);
	    break;
	    case "default":
	    	break;
	    }
		return reply;
	}
	
}
