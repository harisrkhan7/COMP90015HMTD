package Server;
import java.net.*;
import java.io.*;
import java.util.Timer;
public class ServerTCP extends Thread{
	int serverPort; // the server port
	ServerSocket listenSocket;
	Services TCPService;
	PeriodicExchange exchange;
	Timer TimedExchange;

	public ServerTCP(int port)
	{
		serverPort = port;	
		TCPService = new Services();
		exchange = new PeriodicExchange(TCPService);
		TimedExchange = new Timer();
	}
	public void run()
	{
		System.out.println("test");
		TimedExchange.scheduleAtFixedRate(exchange, 10, 60000);
		try
		{
			listenSocket = new ServerSocket(serverPort);
			while(true) {
				System.out.println("Server listening for a connection");
				Socket clientSocket = listenSocket.accept();
				System.out.println("Received connection ");
				Connection c = new Connection(clientSocket, TCPService);
				c.start();
				}
			}
		catch(IOException e)
		{
	       System.out.println("Listen socket:"+e.getMessage());
	     }
}
}
