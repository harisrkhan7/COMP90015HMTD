package Client;

public class StartClient {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArgumentParser commandParser = new ArgumentParser(args);
		commandParser.parseInput();
		ClientTCP client = new ClientTCP(commandParser.toJSON());
		client.start();
	}

}
