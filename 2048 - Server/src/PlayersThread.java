import java.io.PrintWriter;


public class PlayersThread extends Thread{
	public boolean finished = false;
	private Server server;
	private PrintWriter socketWriter;
	private String host = null;
	public PlayersThread(Server server, PrintWriter writer, String host)
	{
		this.server = server;
		this.socketWriter = writer;
		this.host = host;
	}
	public void run()
	{
		finished = false;
		while (finished == false)
		{
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendMessage(server.listPlayers(host));
		}
	}
	public void end()
	{
		finished = true;
	}
	public void sendMessage(String message)
	{
		message = message + (char)13;
		//this.socketWriter.write(message);
		//this.socketWriter.flush();
		this.socketWriter.println(message);
	}
}
