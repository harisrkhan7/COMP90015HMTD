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
	RequestCheck checkRequest;
	public ServerTCP(int port)
	{
		serverPort = port;	
		TCPService = new Services();
		exchange = new PeriodicExchange(TCPService);
		TimedExchange = new Timer();
		checkRequest = new RequestCheck();
	}
	public void run()
	{
		System.out.println("Server Running");
		TimedExchange.scheduleAtFixedRate(exchange, 10, 60000);
		try
		{
			listenSocket = new ServerSocket(serverPort);
			while(true) {
				System.out.println("Server listening for a connection");
				Socket clientSocket = listenSocket.accept();
				System.out.println("Received connection ");
				System.out.println(clientSocket.getInetAddress().toString());
				if(checkRequest.verifyClient(clientSocket))
				{
					Connection c = new Connection(clientSocket, TCPService);
					c.start();
				}
				else
				{
					System.out.println("Connection Refused");
				}
			}
		}
		catch(IOException e)
		{
	       System.out.println("Listen socket:"+e.getMessage());
	       }
		}
	}
