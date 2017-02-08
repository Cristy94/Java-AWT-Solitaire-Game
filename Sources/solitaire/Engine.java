package solitaire;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import solitaire.Card.Suit;
import solitaire.Pile.PileType;

/**
 * Core class of the application.
 * Contains all objects and states of the game 
 */
public class Engine {
	
	ArrayList<Pile> piles;
	ArrayList<Pile> finalPiles;
	Pile drawPile, getPile;
	ArrayList<Pile> allPiles;
	public final int pileNumber = 7;
	public Deck deck;
	
	/**
	 * Class constructor
	 */
	public Engine() {
		resetCards();
	}
	
	/**
	 * Reset all game piles and the deck
	 */
	public void resetCards() {
		deck = new Deck();
		deck.shuffle();
		
		drawPile = new Pile(120);
		drawPile.setOffset(0);
		
		getPile = new Pile(180);
		getPile.setOffset(0);
		
		finalPiles = new ArrayList<Pile>();
		piles = new ArrayList<Pile>();
		
		allPiles = new ArrayList<Pile>();
		allPiles.add(drawPile);
		allPiles.add(getPile);
	}
	
	/**
	 * Setup the initial game state
	 */
	public void setupGame() {
		// Generate piles
		drawPile.type = PileType.Draw;
		getPile.type = PileType.Get;

		for(int i = 1; i <= pileNumber; ++i) {
			Pile p = new Pile(120);
			
			// Add i cards to the current pile
			for(int j = 1; j <= i; ++j) { 
				Card card = deck.drawCard();  
				p.addCard(card);
				
				if(j!=i)
					card.hide();
				else 
					card.show();
			}
			
			piles.add(p);
			allPiles.add(p);
		}
		
		for(Suit suit : Suit.values()) {
			Pile p = new Pile(100);
			p.setOffset(0);
			p.type = PileType.Final;
			finalPiles.add(p);	
			allPiles.add(p);
		}
		
		while(deck.size() > 0) {
			Card card = deck.drawCard();
			card.hide();
			drawPile.addCard(card);
		}
	}
	
	/**
	 * Draw a card from the draw pile and place it into the get pile
	 */
	public void drawCard() {
		if(!drawPile.cards.isEmpty()) {
			Card drew = drawPile.drawCard();
			drew.isReversed = false;
			getPile.addCard(drew);			
		}
	}
	
	/**
	 * When a normal pile is clicked, if the top card is reversed show it
	 * @param {Pile} p
	 */
	public void clickPile(Pile p) {
		if(!p.cards.isEmpty()) {
			Card c = p.cards.get(p.cards.size() - 1);
			if(c.isReversed) {
				c.isReversed = false;
			}
		}
	}
	
	/**
	 * Reverse the Get pile and place it again for Draw
	 */
	public void turnGetPile() {
		if(!drawPile.cards.isEmpty()) return;
		
		while(!getPile.cards.isEmpty()) {
			Card c = getPile.drawCard();
			c.isReversed = true;
			
			drawPile.addCard(c);
		}
	}

	/**
	 * Tests wheter all the cards have been placed in the correct pile
	 * @return {Boolean}
	 */
	public boolean checkWin() {
		for(Pile p : finalPiles) {
			if(p.cards.size() != 13)
				return false;
		}
		return true;
	}

	/**
	 * Save the game state to save.xml file
	 */
	public void save() {
		
		String saveString = "";
		
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			Document doc = docBuilder.newDocument();
			
			String newLine = System.getProperty( "line.separator" );

			Element game =  doc.createElement("game");
			doc.appendChild(game);
			
			// This is from previous implementation, save each pile in a new line
			for(Pile p : piles)
				saveString += p.toString() + newLine;
			for(Pile p: finalPiles)
				saveString += p.toString() + newLine;
			saveString += drawPile.toString() + newLine;
			saveString += getPile.toString() + newLine;

			String[] lines = saveString.split(newLine);
			
			for(String pile : lines) {
				Element p = doc.createElement("pile");
				
				String cardStrings[] = pile.split("-");
				for(String c: cardStrings) {
					String parts[] = c.split(" of ");
					
					Element cardE = doc.createElement("card");
					cardE.setAttribute("value", parts[0]);
					cardE.setAttribute("suit", parts[1]);
					cardE.setAttribute("isReversed", parts[2]);
					
					p.appendChild(cardE);
				}
				
				game.appendChild(p);
			}
				
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource src = new DOMSource(doc);
			StreamResult res = new StreamResult(new File("save.xml"));
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(src, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
       
	}

	/**
	 * Load the game state from save.xml file
	 */
	public void load() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse("save.xml");
			Element docEle = dom.getDocumentElement();
			NodeList nl = docEle.getChildNodes();
			int currentPileCount = 0;
			if (nl != null) {
				// Iterate through all piles
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element el = (Element) nl.item(i);
					if (el.getNodeName().contains("pile")) {

						NodeList cardList = el.getChildNodes();
						Pile tempPile = new Pile(100);

						if (cardList != null) {
							// Iterate through all cards
							for (int j = 0; j < cardList.getLength(); j++) {
								if (cardList.item(j).getNodeType() != Node.ELEMENT_NODE)
									continue;
								
								Element cardNode = (Element) cardList.item(j);

								String suitName = cardNode.getAttribute("suit");
								boolean isReversed = cardNode.getAttribute("isReversed").equals("true");
								int value = Card.valueInt(cardNode.getAttribute("value"));

								// Skip the base card
								if (value == 100)
									continue;

								// Search for the card in all piles
								Card card = null;
								Pile foundPile = null;

								for (Pile p : allPiles) {
									if ((card = p.searchCard(value, suitName)) != null) {
										foundPile = p;
										break;
									}
								}

								tempPile.addCard(card);
								foundPile.removeCard(card);

								// Face-up or face-down card
								if (isReversed) {
									card.hide();
								} else {
									card.show();
								}
							}

							// Add the cards to the correct pile
							if (currentPileCount < pileNumber) {
								piles.get(currentPileCount).merge(tempPile);
							} else if (currentPileCount < pileNumber + 4) {
								finalPiles.get(currentPileCount - pileNumber)
										.merge(tempPile);

								if (!tempPile.isEmpty()) {
									// Set the pile filter for final piles
									Card c = tempPile.peekTopCard();
									finalPiles.get(currentPileCount
											- pileNumber).suitFilter = c.suit;
								}
							} else if (currentPileCount == pileNumber + 4) {
								drawPile.merge(tempPile);
							} else {
								getPile.merge(tempPile);
							}
						}
						currentPileCount++;
					}
				}
			}
						
			// Draw and add the cards again so the offsets are re-calculated
			for(Pile p: allPiles) {
				ArrayList<Card> cards = new ArrayList<Card>();
				
				while(!p.isEmpty()) cards.add(p.drawCard());
				
				for(Card card: cards)
					p.addCard(card);
			}
			
		} catch(Exception e ) {
			e.printStackTrace();
		}
	}	
}
