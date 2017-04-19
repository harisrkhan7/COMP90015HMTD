package Client;
import java.net.*;
import java.io.*;
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
	    ClientSocket = new Socket("localhost", serverPort);
	    System.out.println("Connection Established");
	    DataInputStream in = new DataInputStream( ClientSocket.getInputStream());
	    DataOutputStream out =new DataOutputStream( ClientSocket.getOutputStream());
	    System.out.println("Sending data");
	    out.writeUTF(message);    
	    String data = in.readUTF();   // read a line of data from the stream
	    System.out.println("Received: "+ data) ;
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
