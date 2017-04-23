package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class Services {
	private ArrayList<Server> ServerList;
	private HashMap<String, Resource> ResourceList;
	// We need to use hash maps for storing the data. 
	// I have created the classes for server and resource
	// Publish method and exchange method developers are free
	// to choose the primary key for the Hash maps.
	public Services()
	{
		ServerList = new ArrayList<Server>();
		ResourceList = new HashMap<String, Resource>();
	}
	public Response publish(Resource toPublish)
	{
		return null;
	}
	public Response remove(Resource toRemove)
	{
		String key=toRemove.getOwner()+toRemove.getChannel()+toRemove.getUri();
		Response response;
		//Response response
		if(ResourceList.containsKey(key)){
				ResourceList.remove(key);
				response=new Response("success", null);
			}
		else{
			response = new Response("error","cannot remove resource");
		}
			
		return response;

	}
	public Response share(String secret, Resource toShare)
	{
		return null;
		
	}
	public Response query(Boolean relay, Resource toQuery)
	{
		ArrayList<Resource> matched = getEntry(ResourceList, toQuery);
	      if(relay){
		  // do client side operation 
		  // add the resource returned to the matched
	    }

	    Response queryResponse = new Response("success", null);
	    queryResponse.setResourceList(matched);
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
	                response=new Response("success", null);
	            }
	            else{
	                response=new Response("error","missing or invalid server list");
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
	      String[] tagList1 = res1.getTags();
	      String[] tagList2 = res2.getTags();
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
	public static boolean containString(String check,String[] list){
	      for (String str : list){
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

}
