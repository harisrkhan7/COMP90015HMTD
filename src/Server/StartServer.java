package Server;

public class StartServer {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerTCP server = new ServerTCP(7899);
		server.start();
	}


}
