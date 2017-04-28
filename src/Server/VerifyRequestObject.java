package Server;

import org.json.simple.JSONObject;

public class VerifyRequestObject {
	boolean existsCommand(JSONObject command)
	{
		return (command.containsKey("command"));
	}
	Response checkResourceTemplate(JSONObject command){
		Response response = null;
		JSONObject resourceTemplate = (JSONObject) command.get("resourceTemplate");
		if (command.containsKey("resourceTemplate") == false) {
		      response = new Response (false,"Missing resourceTemplate");
		}
		if (checkTemplate(resourceTemplate) == false){
		      // if it gets inside this loop,
		      // then resourceTemplate is wrong
		      response = new Response(false,"invalid resourceTemplate");
		      // use above response to parse into JSON and .. yeah.. 
		}
		return response;
		
	}
	public boolean checkTemplate(JSONObject toCheck){
	      // check for all of the key and see if they are all intact
	      boolean complete = true;
	      String[] checkList = {"name","tags","description","uri","channel","owner","ezserver"};
	      for (int i = 0; i < checkList.length ; ++i){
		    if( !toCheck.containsKey(checkList[i]) ) {
			  complete = false;
		    }
	      }
	      // if complete remains true, it's intact
	      return complete;
	}
	public boolean checkResource(JSONObject inputResource, String cmd)
	{
		boolean convert;
		if(cmd.equals("PUBLISH")|| cmd.equals("SHARE") || cmd.equals("REMOVE")){
			convert = inputResource.containsKey("resource");
			
		}
		else if(cmd.equals("QUERY")|| cmd.equals("FETCH")){
			convert = inputResource.containsKey("resourceTemplate");
		}
		else if(cmd.equals("EXCHANGE"))
		{
			convert = inputResource.containsKey("serverList");
		}
		else
		{
			convert = false;
		}
		return convert;
	}
	Response getMissingResponse(String cmdText)
	{
		Response error;
		if(cmdText.equals("PUBLISH")|| cmdText.equals("SHARE") || cmdText.equals("REMOVE")){
			error = new Response(false, "Missing resource");
			
		}
		else if(cmdText.equals("QUERY")|| cmdText.equals("FETCH")){
			error = new Response(false, "Missing resourceTemplate");
		}
		else if(cmdText.equals("EXCHANGE"))
		{
			error = new Response(false, "missing or invalid server list");
		}
		else
		{
			error = new Response(false, "invalid command");
		}
		return error;
	}


}
