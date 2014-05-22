import java.io.BufferedReader;
import java.io.PrintWriter;


public class Player {
	private String username;
	private BufferedReader socketReader;
	private PrintWriter socketWriter;
	private Client client;
	public String score;
	public Player(String username, BufferedReader socketReader, PrintWriter socketWriter, Client client)
	{
		this.username = username;
		this.socketReader = socketReader;
		this.socketWriter = socketWriter;
		this.client = client;
	}
	
	public String getUsername()
	{
		return username;
	}
	public BufferedReader getSocketReader()
	{
		return socketReader;
	}
	public PrintWriter getSocketWriter()
	{
		return socketWriter;
	}
	public Client getClient()
	{
		return client;
	}
	
}
