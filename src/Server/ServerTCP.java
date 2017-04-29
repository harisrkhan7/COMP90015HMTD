package Server;
import java.net.*;
import java.io.*;
import java.util.Timer;
public class ServerTCP {
	int serverPort; // the server port
	ServerSocket listenSocket;
	Services TCPService;
	PeriodicExchange exchange;
	PeriodicRemove remove;
	Timer TimedExchange;
	RequestCheck checkRequest;
	int exchangeInterval;
	InetAddress hostname;
	boolean debug;
	public ServerTCP(ServerCommands command)
	{
		debug = command.debug;
		exchangeInterval = command.getExchangeInterval();
		int connectionInterval = command.getConnectionInterval();
		String secret = command.getSecret();
		hostname = command.getAdvertisedHostName();
		serverPort = command.getPort();
		TCPService = new Services(secret);
		exchange = new PeriodicExchange(TCPService);
		TimedExchange = new Timer();
		checkRequest = new RequestCheck(connectionInterval);
		remove = new PeriodicRemove(checkRequest);
	}
	public void start()
	{
		System.out.println("Server Running");
		TimedExchange.scheduleAtFixedRate(remove, Parameters.PERIODIC_REMOVE_START_DELAY, Parameters.PERIODIC_REMOVE_INTERVAL);
		TimedExchange.scheduleAtFixedRate(exchange, Parameters.EXCHANGE_START_DELAY, Parameters.EXCHANGE_INTERVAL);
		try
		{
			listenSocket = new ServerSocket(serverPort, 0 , hostname);
			BroadcastServer temp = 
			new BroadcastServer(listenSocket.getInetAddress().toString(),listenSocket.getLocalPort());
			System.out.println(temp.getBroadcastServer());
			while(true) {
				System.out.println("Server listening for a connection");
				Socket clientSocket = listenSocket.accept();
				System.out.println("Received connection ");
				System.out.println(clientSocket.getInetAddress().toString());
				if(checkRequest.verifyClient(clientSocket))
				{
					Connection c = new Connection(clientSocket, TCPService, debug);
					c.start();
				}
			}
		}
		catch(IOException e)
		{
	       System.out.println("Listen socket:"+e.getMessage());
	       }
		}
	}
