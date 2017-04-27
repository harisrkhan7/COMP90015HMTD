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
import org.json.simple.parser.ParseException;
public class Services {
	private ArrayList<Server> ServerList;
	private HashMap<String, Resource> ResourceList;
	private String secretServer;
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
		return secretServer;
	}
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secretServer = secret;
	}
	public Services(String secret)
	{
		if(secret != null)
		this.secretServer =secret;
		ServerList = new ArrayList<Server>();
		ResourceList = new HashMap<String, Resource>();
		
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
	
	public Response publish(Resource res) throws URISyntaxException
	{	
		return new Response(true,null);
	}
	public Response share(String secret, Resource res) throws URISyntaxException
	{	
		Response response;		
		String command = "share";
		response = missingResource(command,res,secret);
		
		// THESE CHECKS contains bugs......
		
		System.out.println("At missing resource check");
		if(!(response.getResponse() == null)){
			return response;
		}	

		System.out.println("At incorrect resource check");
		response = incorrectResource(res);
		if(!(response.getResponse() == null)){
			return response;
		}


		System.out.println("At channelOwner check");
		response = sameChannelDiffOwner(command,res);
		if(!(response.getResponse() == null)){
			return response;
		}
		
		// bug here at secret check
		System.out.println("At secret check");
		response = secretCheck(secret);
		if((response.getResponse().equals("error"))){
			return response;
		}

		System.out.println("sequence 1 ");
		
		response = uriCheck(command,res);
		if(!(response.getResponse() == null)){
			return response;
		}

		System.out.println("URI CHECKEDOUT");	
//		return responseCheck("share",res,secret);	
		System.out.println("HERE");
		printResponse(new Response(false, "exception!!"));
		return new Response(false, "exception!!"); //other exception situations

	}

	public Response missingResource(String command, Resource res, String secret){
		if(!command.equals("share") && res==null){	//1.if no resource and not share command
		      System.out.println("no resource found");
			printResponse(new Response(false, "missing resource"));
			return new Response(false, "missing resource");
		}	
		if(command.equals("share")){	
			if(secret.equals("") || res==null){	//1.if no secret or resource
			      System.out.println("no response and blank secret");
				printResponse(new Response(false, "missing resource and\\/or secret"));
				return new Response(false, "missing resource and\\/or secret");
			}
		}	
//		return null;
		return new Response();
	}
	
	public Response incorrectResource(Resource res){
//		System.out.println("Uri:"+res.getUri()+" EzServer: "+res.getEzServer()+" Owner: "+res.getOwner());
		if(( res.getUri().equals("")) || (res.getEzServer()==null) || res.getOwner().equals("*") ){	//2.if incorrect resource info
//			System.out.println("Invalid, Uri:"+res.getUri()+" EzServer: "+res.getEzServer()+" Owner: "+res.getOwner());
			printResponse(new Response(false, "invalid resource"));
			return new Response(false, "invalid resource");
		}	
		return new Response();
	}

	public Response sameChannelDiffOwner(String command, Resource res){
		String OCU = res.getOwner()+res.getChannel()+res.getUri();
		String CU = res.getChannel()+res.getUri();
		List<String> l = new ArrayList<String>(ResourceList.keySet());
		for(String listItem : l){
		   if(listItem.contains(CU)){		//contain channel and uri
			 
			 System.out.println("WHOOPSIES");
			 
		      if(!listItem.contains(OCU)){	//not the same owner, rule broken
			    
			    System.out.println("WOAHHHH");
			    
		    	 if(command.equals("publish")){
		    		 System.out.println("SameChannel, Uri:"+res.getUri()+" EzServer: "+res.getEzServer()+" Owner: "+res.getOwner());		 			
		    		 printResponse(new Response(false, "cannot publish resource"));
		    		 return new Response(false, "cannot publish resource");
		    	 }else{
		    		 printResponse(new Response(false, "cannot share resource"));
		    		 return new Response(false, "cannot share resource");
		    	 }
		      }
		   }
		}
		return new Response();
	}

	public Response secretCheck(String secret){
//		if(Arrays.binarySearch(SecretList, secret) == 0){	//if secret is not in the list
		if(!secret.equals(secretServer)){	//if secret is not in the list
			printResponse(new Response(false, "incorrect secret"));
			return new Response(false, "incorrect secret");
		}
		return new Response(true,"");
	}
	
	public Response uriCheck(String command, Resource res) throws URISyntaxException{
		URI uri = new URI(res.getUri());
		System.out.println("I'm at URICHECK NOW");
//		Response response;
		boolean uriAbs = uri.isAbsolute();
		if(!uriAbs){		//if URI contains file or relative address, rule broken
			 if(command.equals("publish") || res.getUri().contains("file")){
				 System.out.println("uriCheck, Uri:"+res.getUri()+" EzServer: "+res.getEzServer()+" Owner: "+res.getOwner());					
				 printResponse(new Response(false, "cannot publish resource"));
				 return new Response(false, "cannot publish resource");
			 }else{
				 printResponse(new Response(false, "cannot share resource"));
				 return new Response(false, "cannot share resource");
			 }					 	 
		}else{		
		      //if all correct 
			ResourceList.put(res.getOwner()+res.getChannel()+res.getUri(),res);	
			System.out.println("Input Success!!!!!!!!!");
			for (String name: ResourceList.keySet()){
	            String key =name.toString();
	            String value = ResourceList.get(name).toString();  
	            System.out.println("key:" + key + " | value:" + value);  
			} 
			printResponse(new Response(true,""));
			return new Response(true,"");	//True should not have error message?		
		}
	}
	
	public void printResponse(Response response){
		System.out.println("Response: "+response.toJSON().toJSONString());
		System.out.println("Response: "+response.toJSON().toString());
		System.out.println("Response: "+response.toString());
		response.toJSON().toString();
	}

	public Response query(Boolean relay, Resource toQuery) throws 
	UnknownHostException,
	IOException, 
	ParseException
	{
	      	printResourceList();
		System.out.println("The query (resourceTemplate) to check is " + toQuery.toJSON().toString());
		ArrayList<Resource> matched = getEntry(ResourceList, toQuery);
		
		//relay to be implemented properly
		
//		if(relay){
//		   // make connection
//		      JSONObject relayQuery = toQuery.toJSON();
//		      JSONParser tempParser = new JSONParser();
//
//
//		      for(Server sv : ServerList){
//			    // send query
//			    Socket clientSocket;
//			    DataInputStream in;
//			    DataOutputStream out;
//
//			    String tempHost = sv.getHostname();
//			    int tempPort = sv.getPort();
//			    clientSocket = new Socket(tempHost, tempPort);
//			    in = new DataInputStream( clientSocket.getInputStream());
//			    out = new DataOutputStream( clientSocket.getOutputStream());
//			    JSONObject forRelay = new JSONObject();
//			    forRelay.put("command","QUERY");
//			    forRelay.put("relay",false);
//			    forRelay.put("resourceTemplate",relayQuery);
//			    out.writeUTF(forRelay.toJSONString());
//			    JSONObject received = (JSONObject) tempParser.parse(in.readUTF());
//			    // once received
//			    if (received.get("response").equals("success")){
//				  System.out.println("adding in the relayed resources");
//				  received.remove("response");
//				  received.remove("resultSize");
//				  matched.addAll(received.values());
//			    }
//			    System.out.println("added all of the relayed resource \n matched is now ");
//			    System.out.println(matched.toString());
//				
//			  }
//			    
//		      }
	      Response queryResponse;
	      if(matched.isEmpty())
	    {
	    	  queryResponse = new Response(false,"Object not found");
	    } 
	      else
	      {
		    queryResponse = new Response(true, null);
		    queryResponse.setResourceList(matched);
		    System.out.println("Resources to be sent is "+ queryResponse.toJSON().toString());
	      }
	    	  return queryResponse;
		
	}
	
	
	public Response transferOperation(Resource toFetch, DataOutputStream out)
	{
	     Response toReturn;
	     double fileSize = (double) 0.0;
	        try {	
	              	URI uri = new URI(toFetch.getUri());
	              	System.out.println("URI path is "+ uri.getPath());
	              	File fileBeingSent = new File(uri.getPath());
	              	fileSize = fileBeingSent.length();
	              	RandomAccessFile byteFile = new RandomAccessFile(fileBeingSent,"r");
	              	byte[] sendingBuffer = new byte[1024*1024];
	              	int left;
	              	System.out.println("Ready to send the buffer " + sendingBuffer);
	              	while((left = byteFile.read(sendingBuffer)) > 0){
	              	      System.out.println("file left " + left);
	              	      out.write(Arrays.copyOf(sendingBuffer, left));
			 }
			 byteFile.close();

		   } catch (FileNotFoundException e) {

			 e.printStackTrace();
			 Response res = new Response(false,"invalid resourceTemplate");
			 return res;
		   } catch (IOException e){
			  System.out.println(e.getMessage());   
		   } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response res = new Response(false, "invalid URI");
			return res;
		  }

		toReturn = new Response(true,null);
//		toReturn.setExtra((int) fileSize);
		return toReturn;
		
		// extra is now something 
		
	}

	
		
	public Response exchange(JSONArray list)
	{
		//Setting up and initialising variables
		Response response;
        String host=null;
        int port=0;
        Server serv_to_add=null;

        for(int i=0;i<list.size();i++){
            JSONObject x=(JSONObject) list.get(i);//Getting the server object as a JSON
            host=x.get("hostname").toString();//Extracting the host name
            port=Integer.parseInt(x.get("port").toString());//Extracting the port
            serv_to_add=new Server(host,port);//Converting to a Server object
            if(!(checkifduplicate(ServerList,serv_to_add))){//if server is not already on the list
                System.out.println(ServerList.indexOf(serv_to_add));//Debug
                 ServerList.add(serv_to_add);
            }
        }
        response=new Response(true, null);
	    return response;
	}
	 public boolean checkifduplicate(ArrayList<Server> list, Server toCheck){
	        for(Server x:list){
	            if(x.getHostname().equals(toCheck.getHostname())&& x.getPort()==toCheck.getPort()){
	                return true;
	            }
	        }
	        return false;
	    }
	 public void exchange()
		{
	        //Setting up the variables
			Random random = new Random();
			int listSize = this.ServerList.size();
			int index = 0;
			if(listSize > 0)//if the serverlist is not empty
			{
	            //Selecting a random server
	            index = random.nextInt(listSize);
			    Server serv=ServerList.get(index);
	            System.out.println("Selected server: "+serv.getHostname()+":"+serv.getPort());
	            try{
				    //opening a socket
	                Socket socket = new Socket(serv.getHostname(),serv.getPort());
	                int timeout=10000;
	                socket.setSoTimeout(timeout);
	                DataInputStream in = new DataInputStream( socket.getInputStream());
	                DataOutputStream out =new DataOutputStream( socket.getOutputStream());
	                System.out.println("connection established");

	                //Setting up loop variables and JSON objects to be sent
	                JSONObject server;
	                JSONObject command=new JSONObject();
	                JSONArray servers=new JSONArray();

	                //Creating the JSON to send
	                command.put("command","EXCHANGE");
	                for(Server x : ServerList){
	                    server = new JSONObject();
	                    server.put("hostname",x.getHostname());
	                    server.put("port",x.getPort());
	                    servers.add(server);
	                }
	                command.put("serverList",servers);

	                //Sending the JSON
	                System.out.println("Sending to: "+serv.getHostname()+serv.getPort());//Debug
	                out.writeUTF(command.toJSONString());//send
	                //String data = in.readUTF();   // read a line of data from the stream
	                socket.close();
	                System.out.println("Exchange socket closed");//Debug
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
		  Resource templateResource){
	    // initialize matching array to be return
	    ArrayList<Resource> match = new ArrayList<Resource>();
	    // Looping through the ResourceList
	    for (Entry<String, Resource> entry : ResourceList.entrySet()){
		  Resource resourcefromList = entry.getValue();
		  if (matchChannel(templateResource,resourcefromList) &&
			      matchOwner(templateResource, resourcefromList) &&
			      matchTags(templateResource, resourcefromList) && 
			      matchURI(templateResource, resourcefromList) &&
			      matchNameDesc(templateResource,resourcefromList))
		  {  
			match.add(entry.getValue());
			System.out.println("MATCHED, adding...");
		  }

	    }
	    return match;
    }
    
    
	// Support method for the Query method
	
	
	public static boolean matchChannel(Resource res1, Resource res2){
	      if (res1.getChannel().equals(res2.getChannel())) return true;
	      return false;
	}
	
	public static boolean matchOwner(Resource res1, Resource res2){
	      if (res1.getOwner().equals(res2.getOwner())) return true;
	      if (res1.getOwner().equals("")) return true;
	      return false;
	}
	
	// have to check that res1 tags are all in res2 tags
	// or res2 have all the tags res1 have
	public static boolean matchTags(Resource res1, Resource res2){
	      ArrayList<String> tagList1 = res1.getTags();
	      
	      if(tagList1.isEmpty()) return true;
	      
	      ArrayList<String> tagList2 = res2.getTags();
	      System.out.println("resource 1 is (the templateResource)" + res1.getTags().toString());
	      System.out.println("resource 2 is (the list resource)" + res2.getTags().toString());
	      
	      
	      
	      boolean abort = false;
	      for (String tag : tagList1){
		    System.out.println("checking the resource 1 tag "+tag);
		    if (containString(tag,tagList2) == false) abort =  true;
		    if(abort) return false;
	      }
	      return true;
	}
	
	
	public static boolean matchURI(Resource template, Resource res){
	      String uriTemplate = template.getUri();
	      String resTemplate = res.getUri();
	      
	      if (uriTemplate.equals(resTemplate)) return true;
	      return false;
	}
	
	public static boolean containString(String check,ArrayList<String> list){
		    for (String str : list){
		        if (check.equalsIgnoreCase(str)){
		              System.out.println("matched returning TRUE");
		            return true;
		         }
		     }
		    return false;

		}

	
	public static boolean matchNameDesc(Resource template, Resource res){
	      String templateName = template.getName();
	      String resName = res.getName();
	      String templateDesc = template.getDescription();
	      String resDesc = res.getDescription();
	      
	      if(templateName.equals("") && templateDesc.equals("")) return true;
	      if (!templateName.equals("") && resName.toLowerCase().contains(templateName)) return true;
	      if (!templateDesc.equals("") && resDesc.toLowerCase().contains(templateDesc)) return true;
	      return false;
	}
	

	
	
void printResourceList()
{
    System.out.println("Below is the current resource list");
    
	for (Map.Entry <String,Resource> entry : ResourceList.entrySet()){
		String key = entry.getKey();
		Resource r = entry.getValue();
		System.out.println("Key is " + key);
		System.out.println("Resource is " + r.toJSON().toString());
	}
			
}



}
