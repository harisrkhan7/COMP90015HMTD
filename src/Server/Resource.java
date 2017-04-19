package Server;

public class Resource {
	private String Name;
	private String Description;
	private String[] Tags;
	private String Channel;
	private String Owner;
	private String EzServer;
	
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
	 * @return the tags
	 */
	public String[] getTags() {
		return Tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String[] tags) {
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
	public String getEzServer() {
		return EzServer;
	}

	/**
	 * @param ezServer the ezServer to set
	 */
	public void setEzServer(String ezServer) {
		EzServer = ezServer;
	}
	

}
