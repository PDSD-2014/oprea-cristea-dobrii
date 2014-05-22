import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Game {
	private boolean started;
	private String host;
	public int turn = -1;
	public ArrayList<Player> players;
	public int[][] matrix = new int[4][4];
	public Server server;
	public Game(Server server, String host, BufferedReader hostSocketReader,
			PrintWriter hostSocketWriter, Client hostClient) {
		started = false;
		this.server = server;
		this.host = host;
		players = new ArrayList<Player>();
		Player player = new Player(host, hostSocketReader, hostSocketWriter,
				hostClient);

		players.add(player);

	}

	public void updateScore(String username, String score)
	{
		for (int i = 0; i < players.size(); i++)
		{
			if (players.get(i).getUsername().compareTo(username) == 0)
			{
				players.get(i).score = new String(score);
				break;
			}
		}
	}
	public void matrixSent(String username, String matrix)
	{
		turn++;
		if (turn == players.size())
			turn = 0;
		for (int i = 0; i < players.size(); i++) {
			if (turn == i)
			{
				players.get(i).getSocketWriter().println("Your turn");
			}
			if (players.get(i).getUsername().compareTo(username) !=0)
			{
				players.get(i).getSocketWriter().println(matrix);
				System.out.println("sent matrix to player "+i);
				
			}
		}
		
		System.out.println(matrix);
		if (matrix.contains("Game over"))
		{
			System.out.println("Game removed");
			server.removeGame(this);
		}
			
	}
	public void start() {
		started = true;
		/*String matrix = null;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getUsername().compareTo(host) == 0) {

				try {
					// destringifyMatrix(players.get(i).getSocketReader().readLine());
					matrix = players.get(i).getSocketReader().readLine();
					System.out.println("matricea este:"+matrix);
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (int i = 1; i < players.size(); i++) {
			players.get(i).getSocketWriter().println(matrix);
		}
		*/
		/*boolean over = false;
		while (over == false) {
			for (int i = 0; i < players.size(); i++) {
				try {
					players.get(i).getSocketWriter().println("Your turn");
					matrix = players.get(i).getSocketReader().readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int j = 0; j < players.size(); j++) {
					if (i != j)
						players.get(i).getSocketWriter().println(matrix);
				}
			}

		}*/
		
	}

	public void destringifyMatrix(String message) {
		String[] res = message.split("@");
		int count = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				matrix[i][j] = Integer.parseInt(res[count]);
				count++;
			}

	}

	public void addPlayer(String username, BufferedReader socketReader,
			PrintWriter socketWriter, Client client) {
		Player player = new Player(username, socketReader, socketWriter, client);
		players.add(player);
	}

	public boolean hasStarted() {
		return started;
	}

	public String getHost() {
		return host;
	}
}
