package Client;
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
public class ClientTCP extends Thread {
	int serverPort;
	Socket ClientSocket;
	String message;
	ClientTCP(int port, String inpmessage)
	{
		serverPort = port;
		this.message = inpmessage;
		
	}
	public void run()
	{
	  try{
	    Resource resource =  new Resource(null, null, "test");
		ClientSocket = new Socket("localhost", serverPort);
	    System.out.println("Connection Established");
	    DataInputStream in = new DataInputStream( ClientSocket.getInputStream());
	    DataOutputStream out =new DataOutputStream( ClientSocket.getOutputStream());
	    JSONObject newCommand = new JSONObject();
        newCommand.put("command", "QUERY");
        newCommand.put("relay", "false");
        newCommand.put("resource template", resource.toJSON());

		System.out.println(newCommand.toJSONString());
		
		// Send RMI to Server
		out.writeUTF(newCommand.toString());
		out.flush();
		
	    ClientSocket.close();
	  }catch (UnknownHostException e) {
	     System.out.println("Socket:"+e.getMessage());
	  }catch (EOFException e){
	     System.out.println("EOF:"+e.getMessage());
	  }catch (IOException e){
	     System.out.println("readline:"+e.getMessage());
	  }finally {
	     if(ClientSocket!=null) try {
	       ClientSocket.close();
	     }catch (IOException e){
	       System.out.println("close:"+e.getMessage());
	     }
	  }
}
}
