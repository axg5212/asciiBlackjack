import java.util.*;

public class Main {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		//Creates a new deck of cards to use and shuffle
		Deck deck = new Deck();
		deck.create();
		deck.shuffle();
		
		//Checks the number of players (max 4)
		System.out.print("Hello There! Enter Number of Players: \n");
		int nPlayers = in.nextInt();
		while(nPlayers > 4) {
				System.out.print("Sorry, we only allow 4 players in our casino.\nPlease kick out the people you least like and enter the new number of players:\n");
				nPlayers = in.nextInt();
		}
		//this catches the new line from before
		in.nextLine();
		
		//Creates the player array that holds every player with a name for each player
		Player players[] = new Player[nPlayers];
		for(int i = 0; i < nPlayers; i++){
		    System.out.print("Please input Player " +(i+1)+ "'s name: \n");
		    String tName = in.nextLine();
		    players[i] = new Player(tName,1000, false);
		}

		//Creates the dealer
		Player dealer = new Player("Dealer", 0, false);
		int choice = 1;
		
		//main game loop
		endgame:
		while(choice == 1) {
			
			//Betting -------------------------------------------------------------------
			for(Player player : players){
			    if(player.balance == 0) {
			    	System.out.println("Out of money. :(\n"); //checks for money in the balance
			    	System.out.println("Player: " +player.name+ " loses! \n");
			    	break endgame; //breaks the enitre loop, ending the game
			    }
		    	System.out.printf(player.name + "'s Balance: $%.2f ", player.balance);
		    	System.out.print("Enter bet amount: \n");
		    	int bet = in.nextInt();
		    	while(bet > player.balance || bet <= 0) {
		    		System.out.print("Not enough money. Enter bet amount: \n");
		    		bet = in.nextInt();
		    	}
		    	player.bet = bet;
		    	player.balance -= bet; //substracts the bet from the balance to not double it up later
			}
			
			//Deal cards -------------------------------------------------------------------------
			dealer.hit(deck.getDeck(), dealer.hand);
			dealer.hit(deck.getDeck(), dealer.hand);
			for(Player player : players){
			    player.hit(deck.getDeck(), player.hand);
			    player.hit(deck.getDeck(), player.hand);
			}
			
			//Game Loop ---------------------------------------------------------------------------
			for(Player player : players){
			    System.out.println("");
			    dealer.printHandHidden(); //prints dealer hand with player hand for convinience 
			    player.printHand();
			    choice = 2;
		    	player.splitPlay = false; //resets split play so value doesn't carry over
			
		    	//Check split
			    if(player.returnSplit() && (player.bet * 2) <= player.balance) {
	    			System.out.print("1: Yes\n2: No\nSplit hand?\nEnter choice: \n");
		    		choice = in.nextInt();
			    	if(choice == 1) {
				    	player.splitPlay = true;
    					player.balance -= player.bet;
	    				player.split.add(player.hand.get(1));
		    			player.hand.remove(1);
		    			//Main hand play
		    			player.printHand();
		    			while(player.returnHandValue() <= 21 && choice !=2) {
		    				System.out.print("1: Hit\n2: Stand\nEnter choice: \n");
		    				choice = in.nextInt();
		    				if(choice == 1) {
		    					player.hit(deck.getDeck(), player.hand);
			    				player.printHand();
			    			}
			    		}
			    		choice = 1;
			    		//Split hand play
			    		player.printSplit();
			    		while(player.returnSplitValue() <= 21 && choice !=2) {
			    			System.out.print("1: Hit\n2: Stand\nEnter choice: \n");
			    			choice = in.nextInt();
			    			if(choice == 1) {
			    				player.hit(deck.getDeck(), player.split);
			    				player.printSplit();
			    			}
			    		}
			    		choice = 1;
			    	}
			    }
			
	    		//Check double down
	    		if(player.returnDoubleDown()  && (player.bet * 2) <= player.balance && choice == 2) {
    				System.out.print("1: Yes\n2: No\nDouble down?\nEnter choice: \n");
    				choice = in.nextInt();
    				//Double down play
    				if(choice == 1) {
    					player.hit(deck.getDeck(), player.hand);
    					player.printHand();
    					player.balance -= player.bet;
    					player.bet *= 2;
    				}
    			}
    			//Regular play
	    		if(choice == 2) {
	    			do {
	    				System.out.print("1: Hit\n2: Stand\nEnter choice: \n");
	    				choice = in.nextInt();
	    				if(choice == 1) {
	    					player.hit(deck.getDeck(), player.hand);
	    				}
	    				player.printHand();
	    			} while(choice != 2 && player.returnHandValue() < 21); //bust condition
	    		}
			}
			
			//The Dealers Play
			dealer.printHand();
			while(dealer.returnHandValue() < 17) {
				dealer.hit(deck.getDeck(), dealer.hand);
				dealer.printHand();
			}
			
			//Results ------------------------------------------------------------------------------
			for(Player player : players){ //each player plays against the dealer
			    if(player.returnHandValue() > 21) {
	    			System.out.println(player.name+" BUST\n");
	    		}
	    		else if(player.returnHandValue() > dealer.returnHandValue() || dealer.returnHandValue() > 21) {
    				System.out.println(player.name+" WIN\n");
    				player.balance += player.bet * 2;
    			}
    			else if(player.returnHandValue() == dealer.returnHandValue()) {
    				System.out.println(player.name+" TIE\n");
    				player.balance += player.bet;
    			}
    			else {
    				System.out.println(player.name+" LOSS\n");
    			}
	    		if(player.returnSplitValue() > 21) {
	    			System.out.println(player.name+" SPLIT BUST\n");
	    		}
	    		else if(player.splitPlay && (player.returnHandValue() > dealer.returnHandValue() || dealer.returnHandValue() > 21)) {
	    			System.out.println(player.name+" SPLIT WIN\n");
	    			player.balance += player.bet * 2;
	    		}
	    		else if(player.splitPlay && player.returnHandValue() == dealer.returnHandValue()) {
	    			System.out.println(player.name+" SPLIT TIE\n");
	    			player.balance += player.bet;
	    		}
	    		else if(player.splitPlay){
	    			System.out.println(player.name+" SPLIT LOSS\n");
    			}
			}
			
			//Reset player and dealer
			deck.shuffle();
			dealer.hand.clear();
			for(Player player : players){
			    player.hand.clear();
			    player.split.clear();
			    player.bet = 0;
			}
			System.out.println("Play another round?\n1: Yes\n2: No\n");
			choice = in.nextInt();
			
		}
		
		//once loop ends we check for who has the most money in the bank
		in.close();
		float maxVal = 0;
		int holdCount = 0;
		int winCount = 0;
		if(nPlayers < 1){
		    for(Player player : players){
		    System.out.println(player.name+": "+player.balance);
		    if(player.balance > maxVal){
		        maxVal = player.balance;
		        winCount = holdCount;
		    }
		        holdCount++;
		    }
		    System.out.println(players[winCount].name+" Wins!\n");
		}
	}

}

//Player class holds all values for each player
class Player extends Card{

	String name;
	float balance; 
	float bet = 0;
	ArrayList<Integer> hand = new ArrayList<Integer>();
	ArrayList<Integer> split = new ArrayList<Integer>();
	boolean splitPlay = false;
	
	public Player(String name, float balance, boolean sp) {
		
		this.name = name;
		this.balance = balance;
		this.splitPlay = sp;
		
	}
	
	int returnHandValue() {
		
		int handValue = 0, softHandValue = 0;
		for(int card : hand) {
			card %= 13;
			if(card == 0) {
				handValue += 11;
				softHandValue += 1;
			}
			else if(card > 0 && card < 10) {
				handValue += card + 1;
				softHandValue += card + 1;
			}
			else if(card == 0 || card > 10){
				handValue += 10;
				softHandValue += 10;
			}
		}
		if(handValue <= 21) {
			return handValue;
		}
		else {
			return softHandValue;
		}
		
	}
	
	int returnSplitValue() {
		
		int handValue = 0, softHandValue = 0;
		for(int card : split) {
			card %= 12;
			if(card == 0) {
				handValue += 11;
				softHandValue += 1;
			}
			else if(card > 0 && card < 10) {
				handValue += card;
				softHandValue += card;
			}
			else {
				handValue += 10;
				softHandValue += 10;
			}
		}
		if(handValue <= 21) {
			return handValue;
		}
		else {
			return softHandValue;
		}
		
	}
	
	boolean returnSplit() {
		
		if((hand.get(0) % 13) == (hand.get(1) % 13)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	boolean returnDoubleDown() {
		
		if(this.returnHandValue() >= 9 && this.returnHandValue() <= 11) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	void hit(Stack<Integer> deck, ArrayList<Integer> hand) {
		
		hand.add(deck.lastElement());
		deck.pop();
		
	}
	
	void printHand() {
		
		ArrayList<String> asciiHand = new ArrayList<String>();
		System.out.println(name + ": " + returnHandValue());
		for(int card : hand) {
			String asciiCard[] = returnCard(card).split("\n");
			for(String line : asciiCard) {
				asciiHand.add(line);
			}
		}
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < hand.size(); j++) {
				int k = i + (j * 9);
				System.out.print(asciiHand.get(k));
			}
			System.out.print("\n");
		}
	
	}
	
	void printSplit() {
		
		ArrayList<String> asciiHand = new ArrayList<String>();
		System.out.println("Split:" + returnSplitValue());
		for(int card : split) {
			String asciiCard[] = returnCard(card).split("\n");
			for(String line : asciiCard) {
				asciiHand.add(line);
			}
		}
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < split.size(); j++) {
				int k = i + (j * 9);
				System.out.print(asciiHand.get(k));
			}
			System.out.print("\n");
		}
	
	}
	
	void printHandHidden() {
		
		ArrayList<String> asciiHand = new ArrayList<String>();
		System.out.println(name + ": ");
		for(int i = 0; i < hand.size() - 1; i++) {
			String asciiCard[] = returnCard(hand.get(i)).split("\n");
			for(String line : asciiCard) {
				asciiHand.add(line);
			}
		}
		String asciiCard[] = returnCard(52).split("\n");
		for(String line : asciiCard) {
			asciiHand.add(line);
		}
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < hand.size(); j++) {
				int k = i + (j * 9);
				System.out.print(asciiHand.get(k));
			}
			System.out.print("\n");
		}
	
	}

}

class Deck {
	
	ArrayList<Integer> freshDeck = new ArrayList<Integer>();
	Stack<Integer> shuffledDeck = new Stack<Integer>();
	
	void create() {
		
		for(int card = 1; card <= 52; card++) {
			this.freshDeck.add(card);
		}
		
	}
	
	void shuffle() {
		
		this.shuffledDeck.clear();
		Collections.shuffle(freshDeck);
		for(int card : this.freshDeck) {
			this.shuffledDeck.add(card);
		}
		
	}
	
	Stack<Integer> getDeck() {
		
		return shuffledDeck;
		
	}
	
}

class Card {

	String spade = "\u2660";
	String club = "\u2663";
	String heart = "\u2665";
	String diamond = "\u2666";
	
	String aceOfSpades = ("*----------*\n|A         |\n|    /\\    |\n|   /  \\   |\n|  (    )  |\n|   -/\\-   |\n|    --    |\n|         A|\n*----------*");
	String aceOfClubs = ("*----------*\n|A         |\n|    /\\    |\n|    \\/    |\n|  /\\  /\\  |\n|  \\/  \\/  |\n|    /\\    |\n|         A|\n*----------*");
	String aceOfHearts = ("*----------*\n|A         |\n| /--\\/--\\ |\n| \\      / |\n|  \\    /  |\n|   \\  /   |\n|    \\/    |\n|         A|\n*----------*");
	String aceOfDiamonds = ("*----------*\n|A         |\n|    /\\    |\n|   /  \\   |\n|  (    )  |\n|   \\  /   |\n|    \\/    |\n|         A|\n*----------*");
	
	String spadeTwo =("*----------*\n|2         |\n|          |\n|     \u2660    |\n|          |\n|     \u2660    |\n|          |\n|         2|\n*----------*");
	String clubTwo =("*----------*\n|2         |\n|          |\n|     \u2663    |\n|          |\n|     \u2663    |\n|          |\n|         2|\n*----------*");
	String heartTwo =("*----------*\n|2         |\n|          |\n|     \u2665    |\n|          |\n|     \u2665    |\n|          |\n|         2|\n*----------*");
	String diamondTwo =("*----------*\n|2         |\n|          |\n|     \u2666    |\n|          |\n|     \u2666    |\n|          |\n|         2|\n*----------*");
	
	String spadeThree = ("*----------*\n|3         |\n|     \u2660    |\n|          |\n|     \u2660    |\n|          |\n|     \u2660    |\n|         3|\n*----------*");
	String clubThree = ("*----------*\n|3         |\n|     \u2663    |\n|          |\n|     \u2663    |\n|          |\n|     \u2663    |\n|         3|\n*----------*");
	String heartThree = ("*----------*\n|3         |\n|     \u2665    |\n|          |\n|     \u2665    |\n|          |\n|     \u2665    |\n|         3|\n*----------*");  
	String diamondThree = ("*----------*\n|3         |\n|     \u2666    |\n|          |\n|     \u2666    |\n|          |\n|     \u2666    |\n|         3|\n*----------*");

	String spadeFour = ("*----------*\n|4         |\n|          |\n|  \u2660    \u2660  |\n|          |\n|  \u2660    \u2660  |\n|          |\n|         4|\n*----------*");
	String clubFour = ("*----------*\n|4         |\n|          |\n|  \u2663    \u2663  |\n|          |\n|  \u2663    \u2663  |\n|          |\n|         4|\n*----------*");
	String heartFour = ("*----------*\n|4         |\n|          |\n|  \u2665    \u2665  |\n|          |\n|  \u2665    \u2665  |\n|          |\n|         4|\n*----------*");
	String diamondFour = ("*----------*\n|4         |\n|          |\n|  \u2666    \u2666  |\n|          |\n|  \u2666    \u2666  |\n|          |\n|         4|\n*----------*");

	String spadeFive = ("*----------*\n|5         |\n|          |\n|  \u2660    \u2660  |\n|     \u2660    |\n|  \u2660    \u2660  |\n|          |\n|         5|\n*----------*");
	String clubFive = ("*----------*\n|5         |\n|          |\n|  \u2663    \u2663  |\n|     \u2663    |\n|  \u2663    \u2663  |\n|          |\n|         5|\n*----------*");
	String heartFive = ("*----------*\n|5         |\n|          |\n|  \u2665    \u2665  |\n|     \u2665    |\n|  \u2665    \u2665  |\n|          |\n|         5|\n*----------*");
	String diamondFive = ("*----------*\n|5         |\n|          |\n|  \u2666    \u2666  |\n|     \u2666    |\n|  \u2666    \u2666  |\n|          |\n|         5|\n*----------*");

	String spadeSix = ("*----------*\n|6         |\n|  \u2660    \u2660  |\n|          |\n|  \u2660    \u2660  |\n|          |\n|  \u2660    \u2660  |\n|         6|\n*----------*");
	String clubSix = ("*----------*\n|6         |\n|  \u2663    \u2663  |\n|          |\n|  \u2663    \u2663  |\n|          |\n|  \u2663    \u2663  |\n|         6|\n*----------*");
	String heartSix = ("*----------*\n|6         |\n|  \u2665    \u2665  |\n|          |\n|  \u2665    \u2665  |\n|          |\n|  \u2665    \u2665  |\n|         6|\n*----------*");
	String diamondSix = ("*----------*\n|6         |\n|  \u2666    \u2666  |\n|          |\n|  \u2666    \u2666  |\n|          |\n|  \u2666    \u2666  |\n|         6|\n*----------*");

	String spadeSeven = ("*----------*\n|7         |\n|  \u2660    \u2660  |\n|          |\n|  \u2660  \u2660 \u2660  |\n|          |\n|  \u2660    \u2660  |\n|         7|\n*----------*");
	String clubSeven = ("*----------*\n|7         |\n|  \u2663    \u2663  |\n|          |\n|  \u2663  \u2663 \u2663  |\n|          |\n|  \u2663    \u2663  |\n|         7|\n*----------*");
	String heartSeven = ("*----------*\n|7         |\n|  \u2665    \u2665  |\n|          |\n|  \u2665  \u2665 \u2665  |\n|          |\n|  \u2665    \u2665  |\n|         7|\n*----------*");
	String diamondSeven = ("*----------*\n|7         |\n|  \u2666    \u2666  |\n|          |\n|  \u2666  \u2666 \u2666  |\n|          |\n|  \u2666    \u2666  |\n|         7|\n*----------*");

	String spadeEight = ("*----------*\n|8         |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|          |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|         8|\n*----------*");
	String clubEight = ("*----------*\n|8         |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|          |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|         8|\n*----------*");
	String heartEight = ("*----------*\n|8         |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|          |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|         8|\n*----------*");
	String diamondEight = ("*----------*\n|8         |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|          |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|         8|\n*----------*");

	String spadeNine = ("*----------*\n|9         |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|     \u2660    |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|         9|\n*----------*");
	String clubNine = ("*----------*\n|9         |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|     \u2663    |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|         9|\n*----------*");
	String heartNine = ("*----------*\n|9         |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|     \u2665    |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|         9|\n*----------*");
	String diamondNine = ("*----------*\n|9         |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|     \u2666    |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|         9|\n*----------*");

	String spadeTen = ("*----------*\n|10        |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|  \u2660    \u2660  |\n|        10|\n*----------*");
	String clubTen = ("*----------*\n|10        |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|  \u2663    \u2663  |\n|        10|\n*----------*");
	String heartTen = ("*----------*\n|10        |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|  \u2665    \u2665  |\n|        10|\n*----------*");
	String diamondTen = ("*----------*\n|10        |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|  \u2666    \u2666  |\n|        10|\n*----------*");

	String spadeJack = ("*----------*\n|J         |\n|    /\\    |\n|   /__\\   |\n|   0  0   |\n|   \\__/   |\n|   /\u2660\u2660\\   |\n|         J|\n*----------*");
	String clubJack = ("*----------*\n|J         |\n|    /\\    |\n|   /__\\   |\n|   0  0   |\n|   \\__/   |\n|   /\u2663\u2663\\   |\n|         J|\n*----------*");
	String heartJack = ("*----------*\n|J         |\n|    /\\    |\n|   /__\\   |\n|   0  0   |\n|   \\__/   |\n|   /\u2665\u2665\\   |\n|         J|\n*----------*");
	String diamondJack = ("*----------*\n|J         |\n|    /\\    |\n|   /__\\   |\n|   0  0   |\n|   \\__/   |\n|   /\u2666\u2666\\   |\n|         J|\n*----------*");

	String spadeQueen = ("*----------*\n|Q         |\n|  /-\\/-\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2660\u2660\\   |\n|         Q|\n*----------*");
	String clubQueen = ("*----------*\n|Q         |\n|  /-\\/-\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2663\u2663\\   |\n|         Q|\n*----------*");
	String heartQueen = ("*----------*\n|Q         |\n|  /-\\/-\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2665\u2665\\   |\n|         Q|\n*----------*");
	String diamondQueen = ("*----------*\n|Q         |\n|  /-\\/-\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2666\u2666\\   |\n|         Q|\n*----------*");

	String spadeKing = ("*----------*\n|K         |\n|  /\\/\\/\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2660\u2660\\   |\n|         K|\n*----------*");
	String clubKing = ("*----------*\n|K         |\n|  /\\/\\/\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2663\u2663\\   |\n|         K|\n*----------*");
	String heartKing = ("*----------*\n|K         |\n|  /\\/\\/\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2665\u2665\\   |\n|         K|\n*----------*");
	String diamondKing = ("*----------*\n|K         |\n|  /\\/\\/\\  |\n|  |____|  |\n|   0  0   |\n|   \\__/   |\n|   /\u2666\u2666\\   |\n|         K|\n*----------*");

	String hiddenCard = ("*----------*\n|##########|\n|##########|\n|##########|\n|##########|\n|##########|\n|##########|\n|##########|\n*----------*");
	
	String returnCard(int card) {
		
		String asciiCard = null;
		switch(card) {
		case 0:
			asciiCard = this.aceOfSpades;
			break;
		case 1:
			asciiCard = this.spadeTwo;
			break;
		case 2:
			asciiCard = this.spadeThree;
			break;
		case 3:
			asciiCard = this.spadeFour;
			break;
		case 4:
			asciiCard = this.spadeFive;
			break;
		case 5:
			asciiCard = this.spadeSix;
			break;
		case 6:
			asciiCard = this.spadeSeven;
			break;
		case 7:
			asciiCard = this.spadeEight;
			break;
		case 8:
			asciiCard = this.spadeNine;
			break;
		case 9:
			asciiCard = this.spadeTen;
			break;
		case 10:
			asciiCard = this.spadeJack;
			break;
		case 11:
			asciiCard = this.spadeQueen;
			break;
		case 12:
			asciiCard = this.spadeKing;
			break;
		case 13:
			asciiCard = this.aceOfHearts;
			break;
		case 14:
			asciiCard = this.heartThree;
			break;
		case 15:
			asciiCard = this.heartTwo;
			break;
		case 16:
			asciiCard = this.heartFour;
			break;
		case 17:
			asciiCard = this.heartFive;
			break;
		case 18:
			asciiCard = this.heartSix;
			break;
		case 19:
			asciiCard = this.heartSeven;
			break;
		case 20:
			asciiCard = this.heartEight;
			break;
		case 21:
			asciiCard = this.heartNine;
			break;
		case 22:
			asciiCard = this.heartTen;
			break;
		case 23:
			asciiCard = this.heartJack;
			break;
		case 24:
			asciiCard = this.heartQueen;
			break;
		case 25:
			asciiCard = this.heartKing;
			break;
		case 26:
			asciiCard = this.aceOfClubs;
			break;
		case 27:
			asciiCard = this.clubTwo;
			break;
		case 28:
			asciiCard = this.clubThree;
			break;
		case 29:
			asciiCard = this.clubFour;
			break;
		case 30:
			asciiCard = this.clubFive;
			break;
		case 31:
			asciiCard = this.clubSix;
			break;
		case 32:
			asciiCard = this.clubSeven;
			break;
		case 33:
			asciiCard = this.clubEight;
			break;
		case 34:
			asciiCard = this.clubNine;
			break;
		case 35:
			asciiCard = this.clubTen;
			break;
		case 36:
			asciiCard = this.clubJack;
			break;
		case 37:
			asciiCard = this.clubQueen;
			break;
		case 38:
			asciiCard = this.clubKing;
			break;
		case 39:
			asciiCard = this.aceOfDiamonds;
			break;
		case 40:
			asciiCard = this.diamondTwo;
			break;
		case 41:
			asciiCard = this.diamondThree;
			break;
		case 42:
			asciiCard = this.diamondFour;
			break;
		case 43:
			asciiCard = this.diamondFive;
			break;
		case 44:
			asciiCard = this.diamondSix;
			break;
		case 45:
			asciiCard = this.diamondSeven;
			break;
		case 46:
			asciiCard = this.diamondEight;
			break;
		case 47:
			asciiCard = this.diamondNine;
			break;
		case 48:
			asciiCard = this.diamondTen;
			break;
		case 49:		
			asciiCard = this.diamondJack;
			break;
		case 50:
			asciiCard = this.diamondQueen;
			break;
		case 51:
			asciiCard = this.diamondKing;
			break;
		case 52:
			asciiCard = this.hiddenCard;
			break;
		}
		return asciiCard;
			
	}

}