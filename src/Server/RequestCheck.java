package Server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class RequestCheck {
	private long connectionInterval;
	private HashMap<InetAddress,Long> connectedClients;

	RequestCheck()
	{
		connectedClients = new HashMap<InetAddress,Long>();
		connectionInterval = 10000;
	}
	public boolean verifyClient(Socket clientSocket)
	{
		boolean verification;
		InetAddress tempIP = clientSocket.getInetAddress();
		
		if(connectedClients.containsKey(tempIP))
		{
			long lastConnectionTime = connectedClients.get(tempIP);
			verification = verifyDelay(lastConnectionTime, tempIP);
		}
		else
		{
			connectedClients.put(tempIP, System.currentTimeMillis());
			verification = true;
		}
		return verification;
	}
	boolean verifyDelay(long lastConnectionTime, InetAddress tempIP)
	{
		long delay = (System.currentTimeMillis() - lastConnectionTime);
		if(delay >= connectionInterval)
		{
			connectedClients.replace(tempIP, System.currentTimeMillis());
			return true;
		}
		else
		{
			return false;
		}
	}



}
