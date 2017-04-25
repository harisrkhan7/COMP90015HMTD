package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class Services {
	private ArrayList<Server> ServerList;
	private HashMap<String, Resource> ResourceList;
	private String secret;
	/**
	 * @return the serverList
	 */
	public ArrayList<Server> getServerList() {
		return ServerList;
	}
	/**
	 * @param serverList the serverList to set
	 */
	public void setServerList(ArrayList<Server> serverList) {
		ServerList = serverList;
	}
	/**
	 * @return the resourceList
	 */
	public HashMap<String, Resource> getResourceList() {
		return ResourceList;
	}
	/**
	 * @param resourceList the resourceList to set
	 */
	public void setResourceList(HashMap<String, Resource> resourceList) {
		ResourceList = resourceList;
	}
	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public Services()
	{
		this.secret ="davisisGood";
		ServerList = new ArrayList<Server>();
		ResourceList = new HashMap<String, Resource>();
		
	}
	public Response publish(Resource toPublish) throws URISyntaxException
	{
		Response response = responseCheck("publish",toPublish,"");		
		return response;
	}
	public Response remove(Resource toRemove)
	{
		String key=toRemove.getOwner()+toRemove.getChannel()+toRemove.getUri();
		Response response;
		//Response response
		if(ResourceList.containsKey(key)){
				ResourceList.remove(key);
				response=new Response(true, null);
			}
		else{
			response = new Response(false,"cannot remove resource");
		}
			
		return response;

	}
	public Response share(String secret, Resource toShare) throws URISyntaxException
	{
		Response response = responseCheck("share",toShare,secret);		
		return response;
		
	}
	
	public Response responseCheck(String command, Resource res, String secretShare) throws URISyntaxException{
		Response response = new Response();
		
		if(!command.equals("share") && res==null){	//1.if no resource and not share command
			return  new Response(false, "missing resource");
		}		
		if(( res.getUri()==null) || (res.getEzServer()==null) || res.getOwner().equals("*") ){	//2.if incorrect resource info
			return  new Response(false, "invalid resource");
		}
		switch (command) {
		case "publish":
			System.out.println("Command Publish!!");
			String OCU = res.getOwner()+res.getChannel()+res.getUri();
			String CU = res.getChannel()+res.getUri();
			List<String> l = new ArrayList<String>(ResourceList.keySet());
			for(String listItem : l){
			   if(listItem.contains(CU)){		//contain channel and uri
			      if(!listItem.contains(OCU)){	//not the same owner, rule broken
					response.setResponse("error");	
					response.setErrorMessage("cannot publish resource");
					return response;
			      }
			   }
			}
		    
			URI uri = new URI(res.getUri());
			boolean uriAbs = uri.isAbsolute();
			if(res.getUri().contains("file") || !uriAbs){		//if URI contains file or relative address, rule broken
				response.setResponse("error");	//other exception situations
				response.setErrorMessage("cannot publish resource");
				return response;
			}else{													//if all correct 
				ResourceList.put(res.getOwner()+res.getChannel()+res.getUri(),res);	
				response.setResponse("success");
				
				for (String name: ResourceList.keySet()){
		            String key =name.toString();
		            String value = ResourceList.get(name).toString();  
		            System.out.println(key + " " + value);  
				} 
				
				return response;				
			}
//			break;
		case "share":		
			System.out.println("Command Share!!");
			String OCUs = res.getOwner()+res.getChannel()+res.getUri();
			String CUs = res.getChannel()+res.getUri();
			List<String> ls = new ArrayList<String>(ResourceList.keySet());
			for(String listItem : ls){
			   if(listItem.contains(CUs)){		//contain channel and uri
			      if(!listItem.contains(OCUs)){	//not the same owner, rule broken
					response.setResponse("error");	
					response.setErrorMessage("cannot share resource");
					return response;
			      }
			   }
			}
			
			if(secret.equals("") || res==null){	//1.if no secret or resource
				response.setResponse("error");
				response.setErrorMessage("missing resource and\\/or secret");
				return response;
			}			
//			if(Arrays.binarySearch(SecretList, secret) == 0){	//if secret is not in the list
			if(!secret.equals(secretShare)){	//if secret is not in the list
				response.setResponse("error");
				response.setErrorMessage("incorrect secret");
				return response;
			}
			if(res.getUri().contains("file")){	//if all correct
				ResourceList.put(res.getOwner()+res.getChannel()+res.getUri(),res);	
				response.setResponse("success");
				return response;
			}else{ 		//if rule broken
				response.setResponse("error");	//other exception situations
				response.setErrorMessage("cannot share resource");
				return response;
			}
//			break;
        case "remove":
            String key=res.getOwner()+res.getChannel()+res.getUri();
            if(!ResourceList.containsKey(key)) { //Resource did not exist
                response.setResponse("error");
                response.setErrorMessage("cannot remove resource");
                return response;
            }
            if(ResourceList.remove(key)!=null){//resource removed
            	response.setResponse("success");
            	return response;
            }
            break;	
		default:
			break;
		}	

		response.setResponse("error");	//other exception situations
		response.setErrorMessage("exception!!");
		return response;
	}
	
	
	public Response query(Boolean relay, Resource toQuery)
	{
		printResourceList();
		toQuery.toJSON().toString();
		ArrayList<Resource> matched = getEntry(ResourceList, toQuery);
	      if(relay){
		  // do client side operation 
		  // add the resource returned to the matched
	    }
	      Response queryResponse;
	      if(matched.isEmpty())
	    {
	    	  queryResponse = new Response(false,"Object not found");
	    } 
	      else
	      {
	    queryResponse = new Response(true, null);
	    queryResponse.setResourceList(matched);
	      }
	    	  return queryResponse;
		
	}
	public Response fetch(Resource toFetch, DataOutputStream out)
	{
	     
	        try {
			 File fileBeingSent = new File(toFetch.getUri());
			 RandomAccessFile byteFile = new RandomAccessFile(fileBeingSent,"r");
			 byte[] sendingBuffer = new byte[1024*1024];
			 int left;
			 while((left = byteFile.read(sendingBuffer)) > 0){
			       System.out.println("file left " + left);
			       out.write(Arrays.copyOf(sendingBuffer, left));
			 }
			 byteFile.close();

		   } catch (FileNotFoundException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		   } catch (IOException e){
			  System.out.println(e.getMessage());   
		   }

		return null;
		
	}
	public Response exchange(ArrayList<Server> incomingServerList)
	{
		
		 Response response;
	            if(incomingServerList!=null){
	                ArrayList<Server> newlist=new ArrayList<Server>();
	                for(Server x:incomingServerList){
	                    ServerList.add(new Server(x.getHostname(),x.getPort()));
	                }
	                response=new Response(true, null);
	            }
	            else{
	                response=new Response(false,"missing or invalid server list");
	            }
	       
	        return response;	
	}
	public void exchange()
	{
		Random random = new Random();
		int listSize = this.ServerList.size();
		int index = 0;
		if(listSize > 0)
		{
		index = random.nextInt(listSize);
		Server serv=ServerList.get(index);
		try{
			//open socket
			Socket socket = new Socket(serv.getHostname(),serv.getPort());
			DataInputStream in = new DataInputStream( socket.getInputStream());
			DataOutputStream out =new DataOutputStream( socket.getOutputStream());
            System.out.println("connection established");
            //String command="";
            //
			
			JSONObject server;
			JSONObject command=new JSONObject();
            JSONArray servers=new JSONArray();
			command.put("command","EXCHANGE");
			for(Server x : ServerList){
                server = new JSONObject();
                server.put("hostname",x.getHostname());
                server.put("port",x.getPort());
				servers.add(server);
			}
			command.put("serverList",servers);
            //
			out.writeUTF(command.toJSONString());//send
			String data = in.readUTF();   // read a line of data from the stream
			socket.close();
		}
		catch (UnknownHostException e){
			ServerList.remove(index);
		}
		catch(IOException e){
			ServerList.remove(index);
		}
		}
		
	}

	// Support method for the Query method


	public static ArrayList<Resource> getEntry(HashMap<String, Resource> ResourceList,
		    Resource search){
	      // initialize matching array to be return
	      ArrayList<Resource> match = new ArrayList<Resource>();

	      // Looping through the ResourceList
	      for (Entry<String, Resource> entry : ResourceList.entrySet()){
		    Resource tempResource = entry.getValue();
		    if(matchChannel(search,tempResource) &&
				matchOwner(search, tempResource) &&
				matchTags(search, tempResource) && 
				matchNameDesc(search,tempResource))
		    {  
			  match.add(entry.getValue());
		    }

	      }
	      return match;
	}

	public static boolean matchChannel(Resource res1, Resource res2){
	      if (res1.getChannel().equals(res2.getChannel())) return true;
	      return false;
	}

	public static boolean matchOwner(Resource res1, Resource res2){
	      if (res1.getOwner().equals(res2.getOwner())) return true;
	      if (res1.getOwner().equals("")) return true;
	      return false;
	}

	// Compare if the tags in resource 2 contains all tags in resource 1

	public static boolean matchTags(Resource res1, Resource res2){
	      ArrayList<String> tagList1 = res1.getTags();
	      ArrayList<String> tagList2 = res2.getTags();
	      boolean abort = false;
	      for (String tag : tagList1){
		    if (!containString(tag,tagList2)) abort = true;
		    if (abort) break;
	      }
	      if (abort) {return false;} else {
		    return true;
	      }
	}

	// supporting method for comparing the resources
	// This method check whether an array "list" contain 
	// string "check" while being case insensitive
	public static boolean containString(String check,ArrayList<String> tagList2){
	      for (String str : tagList2){
		    if (check.equalsIgnoreCase(str)){
			  return true;
		    }
	      }
	      return false;

	}

	// 

	public static boolean matchNameDesc(Resource template, Resource res){
	      boolean match = false;
	      String templateName = template.getName();
	      String resName = res.getName();
	      String templateDesc = template.getDescription();
	      String resDesc = res.getDescription();
	      if (templateName.equals("") || templateDesc.equals("")) match = true;
	      if (resName.toLowerCase().contains(templateName)) match = true;
	      if (resDesc.toLowerCase().contains(templateDesc)) match = true;
	      return match;
	}
	void printResourceList()
	{
		for (Map.Entry <String,Resource> entry : ResourceList.entrySet()){
			String key = entry.getKey();
			Resource r = entry.getValue();
			System.out.println("Key is " + key);
			System.out.println("Resource is " + r.toJSON().toString());
		}
				
	}

}
