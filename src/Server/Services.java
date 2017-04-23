package Server;
import java.util.ArrayList;
import java.util.HashMap;
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
	public Response remove(String toRemove)
	{
		return null;
	}
	public Response share(String secret, Resource toShare)
	{
		return null;
		
	}
	public void query(Boolean relay, Resource toQuery)
	{
		
	}
	public Resource fetch(Resource toFetch)
	{
		return null;
		
	}
	public Response exchange(ArrayList<Server> incomingServerList)
	{
		
		return null;
		
	}
	public void exchange()
	{
		
	}
	
}
