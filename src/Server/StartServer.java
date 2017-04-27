package Server;
public class StartServer {
	static ServerArgumentParser commandParser;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		commandParser = new ServerArgumentParser(args);
		commandParser.parseInput();
		ServerCommands initialCommands = commandParser.getCommands();
		ServerTCP server = new ServerTCP(initialCommands);
		server.start();
	}


}
