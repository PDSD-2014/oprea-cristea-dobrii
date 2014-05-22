import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Server {
	private static ServerSocket mSocketServer;
	private boolean finished = false;
	private File database;
	private Document doc;
	private DocumentBuilderFactory factory;
	private DocumentBuilder parser;
	private ArrayList<Game> games;

	public Server(int port) {
		try {
			games = new ArrayList<Game>();
			mSocketServer = new ServerSocket(port);
			database = new File("database.xml");
			factory = DocumentBuilderFactory.newInstance();
			try {
				parser = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!database.exists()) {
				try {
					doc = parser.newDocument();
					Element root = doc.createElement("users");
					doc.appendChild(root);
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer;

					transformer = transformerFactory.newTransformer();
					transformer.transform(new DOMSource(doc), new StreamResult(
							database));
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (finished == false) {
			try {
				Socket socket = mSocketServer.accept();
				System.out.println("client connected "
						+ socket.getInetAddress());
				Client client = new Client(socket, this);
				client.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public String returnStatistics(String username) {
		NodeList existingClients = doc.getElementsByTagName("user");
		String result = new String();
		for (int i = 0; i < existingClients.getLength(); i++) {
			Element client = (Element) existingClients.item(i);
			if (client.getElementsByTagName("username").item(0)
					.getTextContent().equals(username)) {
				result += "result=1&total_games=\""
						+ client.getElementsByTagName("totalGames").item(0)
								.getTextContent()
						+ "\"&high_score=\""
						+ client.getElementsByTagName("highScore").item(0)
								.getTextContent()
						+ "\"&total_score=\""
						+ client.getElementsByTagName("totalScore").item(0)
								.getTextContent() + "\"";
				return result;
			}
		}
		return new String("0");
	}

	public void registerUser(String usernameStr, String passwordStr) {
		try {
			doc = parser.parse(database);
			Element root = doc.getDocumentElement();
			Element user = doc.createElement("user");
			root.appendChild(user);
			Element username = doc.createElement("username");
			user.appendChild(username);
			Element password = doc.createElement("password");
			user.appendChild(password);
			Element totalGames = doc.createElement("totalGames");
			user.appendChild(totalGames);
			Element highScore = doc.createElement("highScore");
			user.appendChild(highScore);
			Element totalScore = doc.createElement("totalScore");
			user.appendChild(totalScore);

			username.appendChild(doc.createTextNode(usernameStr));
			password.appendChild(doc.createTextNode(passwordStr));
			totalGames.appendChild(doc.createTextNode("0"));
			highScore.appendChild(doc.createTextNode("0"));
			totalScore.appendChild(doc.createTextNode("0"));
			DOMSource source = new DOMSource(doc);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();

				StreamResult result = new StreamResult("database.xml");

				transformer.transform(source, result);

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean clientExists(String username, String password) {
		try {
			doc = parser.parse(database);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList existingClients = doc.getElementsByTagName("user");
		for (int i = 0; i < existingClients.getLength(); i++) {
			Element client = (Element) existingClients.item(i);
			if (client.getElementsByTagName("username").item(0)
					.getTextContent().equals(username)
					&& client.getElementsByTagName("password").item(0)
							.getTextContent().equals(password)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * public void sendMatrix(String username, String matrix) {
	 * 
	 * }
	 */
	public boolean startGame(String username) {
		System.out.println("Game started");
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getHost().compareTo(username) == 0) {
				for (int q = 0; q < games.get(i).players.size(); q++) {
					// games.get(i).players.get(q).getSocketWriter().write("game_started");
					// games.get(i).players.get(q).getSocketWriter().flush();
					games.get(i).players.get(q).getSocketWriter()
							.println("game_started");
					games.get(i).players.get(q).getClient()
							.endPlayerListThread();

				}
				games.get(i).start();

				return true;
			}
		}
		return false;
	}

	public boolean clientExists(String username) {
		try {
			doc = parser.parse(database);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList existingClients = doc.getElementsByTagName("user");
		for (int i = 0; i < existingClients.getLength(); i++) {
			Element client = (Element) existingClients.item(i);
			if (client.getElementsByTagName("username").item(0)
					.getTextContent().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public void sendMatrix(String username, String matrix) {
		for (int i = 0; i < games.size(); i++) {
			for (int q = 0; q < games.get(i).players.size(); q++)
				if (games.get(i).players.get(q).getUsername()
						.compareTo(username) == 0) {
					games.get(i).matrixSent(username, matrix);
					break;
				}
		}
	}

	public void updateScore(String username, String score) {
		for (int i = 0; i < games.size(); i++) {
			for (int q = 0; q < games.get(i).players.size(); q++)
				if (games.get(i).players.get(q).getUsername()
						.compareTo(username) == 0) {
					games.get(i).updateScore(username, score);
					break;
				}
		}
	}

	public synchronized String listPlayers(String username) {
		String result = new String("players_list=");

		for (int i = 0; i < games.size(); i++) {

			for (int q = 0; q < games.get(i).players.size(); q++)
				if (games.get(i).players.get(q).getUsername()
						.compareTo(username) == 0) {

					for (int j = 0; j < games.get(i).players.size(); j++) {
						result += "\""
								+ games.get(i).players.get(j).getUsername()
								+ "\"&";
					}
					if (result.indexOf('&') != -1) {
						System.out.println(result);
						return result.substring(0, result.length() - 1);
					}
					System.out.println(result);
					return result;
				}
		}
		if (result.indexOf('&') != -1) {
			return result.substring(0, result.length() - 1);
		}
		return result;
	}

	public synchronized String listOpenGames() {
		String result = new String("games_list=");
		for (int i = 0; i < games.size(); i++) {
			if (!games.get(i).hasStarted()) {
				result += "\"" + games.get(i).getHost() + "\"&";
			}
		}
		if (result.indexOf('&') != -1) {
			return result.substring(0, result.length() - 1);
		}
		return result;
	}

	public boolean createGame(String username, BufferedReader reader,
			PrintWriter writer, Client client) {
		Game newGame = new Game(this, username, reader, writer, client);
		games.add(newGame);
		return true;
	}

	public boolean joinHostGame(String username, String host,
			BufferedReader reader, PrintWriter writer, Client client) {
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getHost().compareTo(host) == 0) {
				games.get(i).addPlayer(username, reader, writer, client);
				return true;
			}
		}
		return false;
	}

	public void removeGame(Game game) {
		for (int u = 0; u < game.players.size(); u++) {
			NodeList existingClients = doc.getElementsByTagName("user");
			
			for (int i = 0; i < existingClients.getLength(); i++) {
				Element client = (Element) existingClients.item(i);
				if (client.getElementsByTagName("username").item(0)
						.getTextContent()
						.equals(game.players.get(u).getUsername())) {
					
					String totalGames = client.getElementsByTagName("totalGames").item(0)
							.getTextContent();
					
					int totalGamesInt = Integer.parseInt(totalGames);
					totalGamesInt++;
					
					client.getElementsByTagName("totalGames").item(0)
					.setTextContent(Integer.valueOf(totalGamesInt).toString());
					
					String highScore = client.getElementsByTagName("highScore").item(0)
							.getTextContent();
					
					int highScoreInt = Integer.parseInt(highScore);
					
					if (highScoreInt < Integer.parseInt(game.players.get(u).score))
						highScoreInt = Integer.parseInt(game.players.get(u).score);
					
					client.getElementsByTagName("highScore").item(0)
					.setTextContent(Integer.valueOf(highScoreInt).toString());
					
					
					String totalScore = client.getElementsByTagName("totalScore").item(0)
							.getTextContent();
					
					int totalScoreInt = Integer.parseInt(totalScore);
					
					totalScoreInt+=Integer.parseInt(game.players.get(u).score);
					
					
					client.getElementsByTagName("totalScore").item(0)
					.setTextContent(Integer.valueOf(totalScoreInt).toString());
					
					
					
					/*result += "result=1&total_games=\""
							+ client.getElementsByTagName("totalGames").item(0)
									.getTextContent()
							+ "\"&high_score=\""
							+ client.getElementsByTagName("highScore").item(0)
									.getTextContent()
							+ "\"&total_score=\""
							+ client.getElementsByTagName("totalScore").item(0)
									.getTextContent() + "\"";*/
				}
			}
		}
		
		
		
		DOMSource source = new DOMSource(doc);

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();

			StreamResult result = new StreamResult("database.xml");

			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		for (int u = 0; u < game.players.size(); u++)
			game.players.get(u).getClient().startGamesListThread();
		games.remove(game);
	}

	public synchronized boolean closeUnstartedGame(String username) {
		for (int i = 0; i < games.size(); i++) {
			for (int j = 0; j < games.get(i).players.size(); j++) {
				String player = games.get(i).players.get(j).getUsername();
				if (player.compareTo(username) == 0) {
					if (games.get(i).getHost().compareTo(username) == 0) {
						for (int q = 0; q < games.get(i).players.size(); q++) {
							// games.get(i).players.get(q).getSocketWriter().write("game_closed_by_host");
							// games.get(i).players.get(q).getSocketWriter().flush();
							games.get(i).players.get(q).getSocketWriter()
									.println("game_closed_by_host");

							games.get(i).players.get(q).getClient()
									.endPlayerListThread();
							games.get(i).players.get(q).getClient()
									.startGamesListThread();
						}
						games.remove(i);
						return true;
					} else {

						for (int u = 0; u < games.get(i).players.size(); u++)
							if (games.get(i).players.get(u).getUsername()
									.compareTo(username) == 0) {
								games.get(i).players.get(u).getClient()
										.endPlayerListThread();
								games.get(i).players.get(u).getClient()
										.startGamesListThread();
								games.get(i).players.remove(u);
								return true;
							}

					}
				}
			}
		}
		return false;
	}

	public static void main(String args[]) {
		new Server(25000);
	}
}
