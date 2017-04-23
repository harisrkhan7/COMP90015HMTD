package Server;

public class Server {
	private String hostName;
	private int port;
	public Server(String hostname, int port)
	{
		this.hostName = hostname;
		this.port =  port;
	}
	/**
	 * @return the hostName
	 */
	public String getHostname() {
		return hostName;
	}
	/**
	 * @param hostname the hostName to set
	 */
	public void setHostname(String hostname) {
		this.hostName = hostname;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
}
