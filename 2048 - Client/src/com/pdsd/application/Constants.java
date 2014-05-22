package com.pdsd.application;

public abstract class Constants {

	public static final String OK = "1"; 
	public static final String NOK = "0"; 
	
	public static final String LOGIN_ERROR = "Could not login into the application";
	public static final String SIGNUP_ERROR = "Could not sign up for 2048";
	public static final String UNKNOWN_ERROR = "The server is not responding";
	
	public static final String GAMES_LIST_REQUEST = "games_list";
	public static final String PLAYERS_LIST_REQUEST = "players_list";
	public static final String GAME_CLOSED_BY_HOST = "game_closed_by_host";
	public static final String GAME_STARTED = "game_started";
	
	public static final String DESTINATION_ADDRESS = "192.168.0.116";
	public static final int DESTINATION_PORT = 25000;
	public static final int SLEEP = 500;
	
	public static String signInRequest = "sign_in?user=\"*\"&pass=\"%\"";
	public static String signUpRequest = "sign_up?user=\"*\"&pass=\"%\"";
	public static String createGameRequest = "create_game?user=\"*\"";
	public static String joinGameRequest = "join_host_game?user=\"*\"&host=\"%\"";
	public static String exitGameRequest = "exit_game?user=\"*\"";
	public static String getStatisticsRequest = "get_statistics?user=\"*\"";
	public static String signOutRequest = "sign_out?user=\"*\"";
	public static String startGameRequest = "start_game?user=\"*\"";
	
	
}