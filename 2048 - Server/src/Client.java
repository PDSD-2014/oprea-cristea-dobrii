import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	private Server server;
	private Socket socket;
	private boolean finished = false;
	private BufferedReader socketReader;
	private PrintWriter socketWriter;
	private boolean authenticated;
	private GamesThread sendGames = null;
	private PlayersThread sendPlayers = null;
	public Client(Socket socket, Server server) {
		this.server = server;
		this.socket = socket;
		try {
			this.socketReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.socketWriter = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String message;
		
		
		authenticated = false;
		while (finished == false) {
			try {
				message = socketReader.readLine();
				if (message == null)
					break;
				System.out.println(message);
				if (authenticated == false) {
					
					if (message.contains("sign_in?"))
					{
						handleSignIn(message);
						if (authenticated == true)
							startGamesListThread();
						continue;
					}
					else if (message.contains("sign_up?"))
					{
						handleSignUp(message);
						if (authenticated == true)
							startGamesListThread();
						continue;
					}
					sendMessage("0");
					continue;
				}
				if (message.contains("get_statistics?"))
				{
					returnStatistics(message);
					
					continue;
				}
				else if (message.contains("create_game?"))
				{
					createGame(message);
					continue;
				}
				else if (message.contains("exit_game?"))
				{
					closeUnstartedGame(message);
					continue;
				}
				else if (message.contains("start_game?"))
				{
					startGame(message);
					continue;
				}
				else if (message.contains("join_host_game?"))
				{
					joinHostGame(message);
					continue;
				}
				else if (message.contains("sign_out"))
				{
					finished = true;
					socket.close();
					if (sendGames != null)
						endGamesListThread();
					if (sendPlayers != null)
						endPlayerListThread();
					continue;
				}
				else if (message.contains("matrix"))
				{
					//server.sendMatrix(message);
					handleMatrix(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	public void endGamesListThread()
	{
		if (sendGames != null)
			sendGames.end();
		
	}
	public void startGamesListThread()
	{
		sendGames= new GamesThread(server, socketWriter);
		sendGames.start();
		
		
	}
	public void endPlayerListThread()
	{
		if (sendPlayers != null)
			sendPlayers.end();
	}
	public void startPlayerListThread(String username)
	{
		sendPlayers= new PlayersThread(server, socketWriter, username);
		sendPlayers.start();
	}
	public String getMatrix(String message)
	{
		int count = message.indexOf("matrix=\"");
		int strLength = new String("matrix=\"").length();
		String matrix = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return matrix;
	}
	public	String getUserName(String message) {
		int count = message.indexOf("user=\"");
		int strLength = new String("user=\"").length();
		String userName = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return userName;
	}
	public	String getHostName(String message) {
		int count = message.indexOf("host=\"");
		int strLength = new String("host=\"").length();
		String userName = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return userName;
	}
	String getPassword(String message) {
		int count = message.indexOf("pass=\"");
		int strLength = new String("pass=\"").length();
		String password = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return password;
	}
	public void startGame(String message)
	{
		String username = getUserName(message);
		
		if (server.startGame(username))
		{
			if (sendPlayers != null)
			{
				System.out.println("send players ended");
				sendPlayers.end();
			}
			sendMessage("1");
			return ;
		}
		sendMessage("0");
	}
	public void closeUnstartedGame(String message)
	{
		String username = getUserName(message);
		sendPlayers.end();
		server.closeUnstartedGame(username);
		
		
		
	}
	public void joinHostGame(String message)
	{
		String username = getUserName(message);
		String host = getHostName(message);
		if (server.joinHostGame(username, host, socketReader, socketWriter, this))
		{
			if (sendGames != null )
				sendGames.end();
			sendMessage("1");
			
			startPlayerListThread(username);
			return ;
		}
		sendMessage("0");
	}
	public void createGame(String message)
	{
		String username = getUserName(message);
		if (server.createGame(username, socketReader, socketWriter, this))
		{
		
			if (sendGames != null )
				endGamesListThread();
			sendMessage("1");
			
			startPlayerListThread(username);
			
			return ;
		}
		sendMessage("0");
	}
	public String getScore(String message)
	{
		int count = message.indexOf("pers=");
		int strLength = new String("pers=").length();
		String score = message.substring(count+strLength,message.indexOf('@', count+strLength+1));
		return score;
	}
	public void handleMatrix(String message)
	{
		String username = getUserName(message);
		String matrix = getMatrix(message);
		
		String updatedMatrix ="matrix@"+matrix;
		String score = getScore(updatedMatrix);
		server.updateScore(username,score);
		
		server.sendMatrix(username, updatedMatrix);
	}
	public void handleSignIn(String message)
	{
		String username = getUserName(message);
		String password = getPassword(message);
		if (server.clientExists(username,password))
		{
			sendMessage("1");
			
			System.out.println("sent 1");
			authenticated = true;
			return ;
		}
		sendMessage("0");
		
		System.out.println("sent 0");
	}
	public void handleSignUp(String message)
	{
		String username = getUserName(message);
		String password = getPassword(message);
		if (server.clientExists(username))
		{
			sendMessage("0");
			return ;
		}
		sendMessage("1");
		server.registerUser(username,password);
		
		authenticated = true;
	}
	public void returnStatistics(String message)
	{
		String username = getUserName(message);
		String result = server.returnStatistics(username);
		sendMessage(result);
		
		System.out.println("Sent statistics"+result);
	}
	public synchronized void sendMessage(String message)
	{
		message = message + (char)13;
		
		//this.socketWriter.write(message);
		
		this.socketWriter.println(message);
		//this.socketWriter.flush();
		
		
	}
	
}
