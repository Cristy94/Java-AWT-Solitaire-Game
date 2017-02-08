package solitaire;

public class Game {

	Engine game;
	GUI gui;
	
	public Game() {
		game = new Engine();
		gui = new GUI(game);
	}
	
	public static void main(String[] args) {
		Game Solitaire = new Game();
	}
}
