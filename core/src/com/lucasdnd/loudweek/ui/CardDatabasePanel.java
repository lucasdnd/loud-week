package com.lucasdnd.loudweek.ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.lucasdnd.loudweek.FontUtils;
import com.lucasdnd.loudweek.InputHandler;
import com.lucasdnd.loudweek.LoudWeek;
import com.lucasdnd.loudweek.gameplay.Card;
import com.lucasdnd.loudweek.gameplay.CardDatabase;
import com.lucasdnd.loudweek.gameplay.CardModel;

/**
 * Renders the card database in the DeckBuildingScreen
 * 
 * @author lucasdnd
 *
 */
public class CardDatabasePanel {
	
	// Layout
	private float x, y, width, height;
	private final float marginX = 12f;
	private final float marginY = 12f;
	
	// Rendering
	private FontUtils font;
	private SpriteBatch batch;
	private ShapeRenderer uiShapeRenderer;
	
	// Data
	private final int cardsPerPage = 9;
	private int currentPage;
	private Button nextPageButton, previousPageButton;
	
	private ArrayList<Card> cards;
	private Card pickedUpCard;	// This will keep a reference to the card the Player picked up from here.
								// If the cardOnMouse needs to be put back, we know it came from here
	
	public CardDatabasePanel(float x, float y) {
		
		this.x = x;
		this.y = y;
		width = Card.cardWidth * 3 + marginX * 2;
		height = Card.cardHeight * 3 + marginY * 2;
		
		// Basic objects
		font = new FontUtils();
		batch = new SpriteBatch();
		uiShapeRenderer = new ShapeRenderer();
		nextPageButton = new Button(">");
		previousPageButton = new Button("<");
		
		// Instantiate the Cards so we don't do that in the render loop
		cards = new ArrayList<Card>();
		for (CardModel cm : CardDatabase.get().cardModels) {
			cards.add(new Card(cm, true));
		}
	}
	
	public void update(LoudWeek game) {
		
		InputHandler input = game.getInputHandler();
		
		// Check if we clicked a card
		if (input.leftMouseJustClicked) {
			
			float mouseX = Gdx.input.getX();
			float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
			
			for (int i = 0; i < cardsPerPage; i++) {
				
				float posX = x + Card.cardWidth * (i % 3) + marginX * (i % 3);
				float posY = (y - Card.cardHeight * (i / 3)) - marginY / 2f * (i / 3) - Card.cardHeight;
				
				if (mouseX >= posX && mouseX <= posX + Card.cardWidth &&
					mouseY >= posY && mouseY <= posY + Card.cardHeight) {
					
					CardModel cm = CardDatabase.get().cardModels.get(i + (cardsPerPage * currentPage));
					if (cm.isPlayerHasInCollection() == false) {
						return;	// Pick up only if the player has it in his collection
					}
					Card pickedUpCard = new Card(cm, true);
					this.pickedUpCard = pickedUpCard;
					game.getDeckBuildingScreen().setCardOnMouse(pickedUpCard);
				}
			}
		}
	}
	
	public void render(LoudWeek game) {
		this.update(game);
		
		// Render cards
		batch.begin();
		for (int i = 0; i < cardsPerPage; i++) {
			
			float posX = x + Card.cardWidth * (i % 3) + marginX * (i % 3);
			float posY = (y - Card.cardHeight * (i / 3)) - marginY / 2f * (i / 3) - Card.cardHeight;
			
			CardModel cm = CardDatabase.get().findModelByCardId(cards.get(i).getId());
			if (cm != null) {
				
				Card cardToRender = cards.get(i);
				
				if (cm.isPlayerHasInCollection()) {
					cardToRender.removeTintColor();
					cardToRender.render(posX, posY);
				} else {
					cardToRender.setTintColor(Color.DARK_GRAY);
					cardToRender.render(posX, posY);
				}
			}
		}
		batch.end();
		
		// Render text
		font.drawWhiteFont("Sua coleção", x, y + marginY * 3f, true, Align.center, (int)width);
		
		// Placeholder: card types
		uiShapeRenderer.begin(ShapeType.Filled);
		uiShapeRenderer.setColor(new Color(0.2f, 0.6f, 0.3f, 1));
		float typeRectHeight = 300f;
		uiShapeRenderer.rect(x - marginX * 8f, y / 2f - typeRectHeight / 2f + marginY / 2f, marginX * 3f, typeRectHeight);
		
		// Placeholder: previous page button
		float buttonSize = 40f;
		uiShapeRenderer.rect(x - marginX * 4f, y / 2f - buttonSize / 2f + marginY / 2f, buttonSize, buttonSize);
		uiShapeRenderer.rect(x + width + marginX / 2f + 2f, y / 2f - buttonSize / 2f + marginY / 2f, buttonSize, buttonSize);
		
		uiShapeRenderer.end();
	}

	/**
	 * When the player releases the card on the mouse by right clicking it
	 * @param cardOnMouse
	 */
	public void putCardBack(Card cardOnMouse) {
		if (cardOnMouse == pickedUpCard) {
			
		}
	}
}
