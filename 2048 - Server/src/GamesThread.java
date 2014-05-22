import java.io.PrintWriter;


public class GamesThread extends Thread{
	public boolean finished = false;
	private Server server;
	private PrintWriter socketWriter;
	public GamesThread(Server server, PrintWriter writer)
	{
		this.server = server;
		this.socketWriter = writer;
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
			sendMessage(server.listOpenGames());
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
