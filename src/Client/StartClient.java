package Client;

public class StartClient {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientTCP client = new ClientTCP(7899,args);
		client.start();
	}

}
