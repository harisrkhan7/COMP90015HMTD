package Server;

import org.json.simple.JSONObject;

public class VerifyRequestObject {
	boolean checkCommand(JSONObject command)
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

}
