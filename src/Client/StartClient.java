package Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class StartClient {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArgumentParser commandParser = new ArgumentParser(args);
		try {
		commandParser.parseInput();
		int port = commandParser.getPort();
		InetAddress address;
		address = commandParser.getHostName();
		boolean debug = commandParser.getDebug();
		ClientTCP client = new ClientTCP(commandParser.toJSON(), port, address, debug);
		client.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Server Not Found!");
		} catch (NumberFormatException e){
			System.out.println("Invalid Input");
		}
	}

}
