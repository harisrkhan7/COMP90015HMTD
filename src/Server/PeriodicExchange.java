package Server;
import java.util.TimerTask;
public class PeriodicExchange extends TimerTask{
	Services TCPService;
	public PeriodicExchange(Services s)
	{
		TCPService = s;
	}
	public void run()
	{
		//initialise a timer
		System.out.println("Timed Message");
		TCPService.exchange();
	}
}
