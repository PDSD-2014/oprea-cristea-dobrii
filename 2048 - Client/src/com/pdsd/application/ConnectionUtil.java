package com.pdsd.application;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class ConnectionUtil {
	public Socket connection;
	private static ConnectionUtil connectionUtil = null;
	public OutputStreamWriter osw;
	public BufferedReader responseReader;

	protected ConnectionUtil(String destinationAddress, int destinationPort)
			throws IOException {
		InetAddress address = InetAddress.getByName(destinationAddress);
		connection = new Socket(address, destinationPort);

		BufferedOutputStream bos = null;

		bos = new BufferedOutputStream(connection.getOutputStream());

		osw = new OutputStreamWriter(bos);

		InputStreamReader inputStreamReader = null;

		inputStreamReader = new InputStreamReader(connection.getInputStream());

		responseReader = new BufferedReader(inputStreamReader);

	}

	public static ConnectionUtil getConnection() {
		if (connectionUtil == null) {

			try {
				connectionUtil = new ConnectionUtil(
						Constants.DESTINATION_ADDRESS,
						Constants.DESTINATION_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return connectionUtil;
	}

	public void sendData(String data) {

		/*try {
			osw.write(data + (char) 13);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			osw.write(data+(char)13);
			osw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		Log.d("LOGIN", "send data" + data);
	}

	public String receiveData() {
		/*
		 * InputStreamReader inputStreamReader = null;
		 * 
		 * try { if (connection != null) inputStreamReader = new
		 * InputStreamReader(connection.getInputStream()); else return null;
		 * BufferedReader responseReader = new
		 * BufferedReader(inputStreamReader);
		 * 
		 * return responseReader.readLine(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 * return null;
		 */
		try {
			return responseReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public void closeConnection() {
		try {
			connection.close();
			connection = null;
			connectionUtil = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}