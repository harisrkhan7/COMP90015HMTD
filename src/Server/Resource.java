package Server;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Resource {
	private String Name;
	private String Description;
	private ArrayList<String> Tags;
	private String Channel;
	private String Owner;
	private Server Server;
	private String uri;
	private JSONObject toConvert;
	public Resource(JSONObject objConvert, String cmd)
	{
		updateToConvert(objConvert, cmd);
		this.Name = getParameter("name");
		this.Description = getParameter("description");
		this.Tags = getArrayList("tags");
		this.Channel = getParameter("channel");
		this.Owner = getParameter("owner");
		this.Server = new Server("localhost",123);
		this.uri = getParameter("uri");
		removeWhiteSpaces();
		
	}
	public Resource(String owner, String channel, String uri)
	{
		this.Name = null;
		this.Description = null;
		this.Tags = null;
		this.Server = null;
		this.uri = uri;
		if(channel == null)
			this.Channel = " ";
		if(owner == null)
			this.Owner = " ";
		
	}
	public Resource(String name, ArrayList<String> tags,
		    String description,
		    String uri, String channel, 
		    String owner, Server ezserver){
	      this.Name = name;
	      this.Tags = tags;
	      this.Description = description;
	      this.uri = uri;
	      this.Channel = channel;
	      this.Owner = owner;
	      this.Server = ezserver;
	}
	void updateToConvert(JSONObject objConvert, String cmd)
	{
		if(cmd.equals("PUBLISH")|| cmd.equals("SHARE") || cmd.equals("REMOVE")){
			System.out.println(objConvert.toJSONString()+ "before get update to convert");
			toConvert = (JSONObject) objConvert.get("resource");
			System.out.println(toConvert.toString()+ "update to convertIf after update");
		}
		else{
			System.out.println(objConvert.toString()+"argument");
			toConvert = (JSONObject) objConvert.get("resourceTemplate");
			System.out.println(toConvert.toString()+"update to convert else");
		}
		
	}

	public String getParameter(String cmd)
	{
		try{
		return (toConvert.get(cmd).toString());
		}
		catch(NullPointerException e)
		{
			return "";
		}
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the tags
	 */
	public ArrayList<String> getTags() {
		return Tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(ArrayList<String> tags) {
		Tags = tags;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return Channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		Channel = channel;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return Owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		Owner = owner;
	}

	/**
	 * @return the ezServer
	 */
	public Server getEzServer() {
		return Server;
	}

	/**
	 * @param ezServer the ezServer to set
	 */
	public void setEzServer(Server ezServer) {
		Server = ezServer;
	}
	public JSONObject toJSON(){
	    JSONObject resource = new JSONObject();
	    JSONArray tags = tagsToArrayNode();
        resource.put("name", this.Name);
        resource.put("tags", tags);
        resource.put("description", this.Description);
        resource.put("uri", this.uri);
        resource.put("channel", this.Channel);
        resource.put("owner", this.Owner);
        resource.put("ezserver", this.Server.toString());
		return resource;
	}
	private ArrayList<String> getArrayList(String label)
	{
		ArrayList<String> returnList = new ArrayList<String>();
			if(toConvert.containsKey(label))
			{
				JSONArray list =  (JSONArray) toConvert.get(label);
				Object testlist[] = list.toArray();
				for(int i = 0;i<testlist.length;i++)
				{
					returnList.add(testlist[i].toString());
				
				}
			}
			return returnList;
	}
	private JSONArray tagsToArrayNode(){
		JSONArray returnList = new JSONArray();
		if(Tags.isEmpty()==false)
		{
		for(String x:Tags){
        	returnList.add(x);
        }
		}
		System.out.println(returnList.toString()+" in tagstoArrayNode ");
		return returnList;
	}
	public void removeWhiteSpaces(){
		this.Channel.trim();
		this.Channel.replaceAll("\0", "");
		this.Description.trim();
		this.Description.replaceAll("\0", "");
		this.Server.setHostname(this.Server.getHostname().trim());
		this.Server.setHostname(this.Server.getHostname().replaceAll("\0", ""));
		this.Name.trim();
		this.Name.replaceAll("\0", "");
		this.Owner.trim();
		this.Owner.replaceAll("\0", "");
		this.uri.trim();
		this.uri.replaceAll("\0", "");
		for(String s:this.Tags)
		{
			s.trim();
			s.replaceAll("\0", "");
		}
//		System.out.println("white space cleaned");
	}
}
