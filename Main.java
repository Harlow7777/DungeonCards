import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {
	
	final static String path = "C:\\Users\\AB04191\\workspace\\DungeonCards\\bin\\Cards\\";	
	public static Deck deck = new Deck();
	public static String PLAYER_NAME = "";
	public static String PLAYER_CLASS = "";
	public static int PLAYER_LIVES = 10;
	public int bossLives = 0;
	public int bossValue = 0;
	public static int PLAYER_SPEED = 0;
	public static int PLAYER_LEVEL = 1;
	public final static JTextArea level = new JTextArea();
	public final static JTextArea lives = new JTextArea();
	public final static JTextArea speed = new JTextArea();
	public final static JTextArea coord = new JTextArea();
	
	//public gui components
	public static JFrame frame = new JFrame("Dungeon Cards");
	public static int currRow;
	public static int currCol;
	public static JPanel[][] gridArray = new JPanel[20][20];
	public static int cellWidth = 0;
	public static int cellHeight = 0;
	public static JPanel PIT = new JPanel();
	public static JPanel EXIT = new JPanel();
	public static JPanel gridPanel = new JPanel();
	public static GridBagLayout layout = new GridBagLayout();
    public TestPane board = new TestPane();
    final static JPanel cardPanel = new JPanel(); 
    final static JPanel cardDisplay = new JPanel();
    final static JPanel character = new JPanel();
    final static JTextPane notifications = new JTextPane();
    public boolean drawListener = true;
    public boolean drawCard = false;
    
    public static void main(String[] args) {
    	deck.populate();
    	deck.shuffle();
        new Main();
        setupCharacter();
    }

    public Main() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                //Setup board
                gridPanel.add(board);
                gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                gridPanel.setBorder(BorderFactory
                        .createTitledBorder("Board"));
                
                //Add Card view
                cardDisplay.setToolTipText("Draws a card from the deck.");
                cardDisplay.addMouseListener(new MouseListener() {
                	@Override
					public void mouseClicked(MouseEvent e) {
                		if(drawListener == true){
							Card currentCard = deck.drawCard();
							String currentSuit = currentCard.getSuit();
							String currentValue = currentCard.getValue();
							//Show currentCard on GUI
							cardDisplay.removeAll();
							cardDisplay.repaint();
							JLabel picLabel = displayCard(currentSuit, currentValue);
							cardDisplay.add(picLabel);
							cardDisplay.revalidate();
							cardDisplay.repaint();
							System.out.println(currentSuit + " " + currentValue);
							//If drawn card is boss, enter battle phase
							if(currentValue.length() > 2){
								notifications.setText("Battle: " + currentValue);
								//remove face card from deck once a boss battle occurs
								drawListener = false;
								deck.dispose(currentCard);
								battlePhase(currentValue);
							//else move character
							} else {
								moveCharacter(currentSuit, currentValue);
							}
                		} else {
                			return;
                		}
                	}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}
                });
                
                //Setup Frame
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(character, BorderLayout.NORTH);
                frame.add(cardPanel, BorderLayout.EAST);
                frame.add(gridPanel, BorderLayout.WEST);
                frame.add(coord, BorderLayout.SOUTH);
                
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                cardPanel.setVisible(false);
            }
        });
    }
    
    public static void setupCharacter(){
    	notifications.setText("Type Name");
        notifications.setForeground(Color.red);
    	notifications.setOpaque(false);
    	final JTextArea name = new JTextArea();
    	final JTextArea CLASS = new JTextArea();
    	final JButton confirm = new JButton();
    	
    	name.setEditable(true);
    	name.setText("Name Here");
    	
    	confirm.setText("Confirm");
    	confirm.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				PLAYER_NAME = name.getText();
				name.setEditable(false);
				name.setOpaque(false);
				confirm.setVisible(false);
				notifications.setText("Place Start");
			}
    	});
    	level.setOpaque(false);
    	level.setEditable(false);
    	level.setText("LVL: " + PLAYER_LEVEL);
    	lives.setOpaque(false);
    	lives.setEditable(false);
    	lives.setText("Lives: " + PLAYER_LIVES);
    	speed.setOpaque(false);
    	speed.setEditable(false);
    	speed.setText("Speed: " + PLAYER_SPEED);
    	//TODO: find proper place for notifications
    	character.add(notifications);
    	character.add(name);
    	character.add(confirm);
    	character.add(level);
    	character.add(lives);
    	character.add(speed);
    	character.setSize(50, 200);
    	character.setBorder(BorderFactory
                .createTitledBorder("Character"));
    	
    	//TODO: Add class popup select list, selection stored in class global
    	CLASS.setEditable(false);
    	CLASS.setOpaque(false);
    	CLASS.setText(PLAYER_CLASS);
    	
    	//wait for end of setup to show cardPanel
    	notifications.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				changed();
			}
				public void changed() {
		   		     if (notifications.getText().equals("Select Class")){
		   		    	 cardPanel.setVisible(true);
		   		        BufferedImage image = null;
		   				try {
		   					image = ImageIO.read(new File(path + "cardback.png"));//182x300
		   				} catch (IOException e) {
		   					e.printStackTrace();
		   				}
		   		        final JLabel picLabel = new JLabel(new ImageIcon(scale(image, BufferedImage.TYPE_INT_RGB, 182, 300, 0.061, 0.072)));
		   		        cardDisplay.add(picLabel);
		   		        cardPanel.add(cardDisplay);
		   		        cardPanel.repaint();
		   		        frame.pack();
		   		     } else {
		   		       
		   		     }
	   		   }
    	});
    }
    
    public static void moveCharacter(String suit, String currValue){
    	String direction = "";
    	int value = Integer.parseInt(currValue);
    	//erase current position color
    	JPanel currentPosition = gridArray[currRow][currCol];
    	currentPosition.setOpaque(false);
    	currentPosition.setBackground(null);
    	System.out.println("Current Position: (" + currCol + ", " + currRow + ")");
    	
    	//determine direction of movement based on suit
    	switch(suit) {
    		case "S": 	direction = "NORTH";
    					currRow = currRow - value;
    					break;
    		case "D":	direction = "SOUTH";
    					currRow = currRow + value;
    					break;
    		case "C":	direction = "LEFT";
    					currCol = currCol - value;
    					break;
    		case "H":	direction = "RIGHT";
    					currCol = currCol + value;
    					break;
    	}
    	
    	//stop at the edge of the board, unless Ninja class
    	if(PLAYER_CLASS.equals("Ninja")){
    		if(currCol < 0){
    			currCol = currCol*(-1);
    		}
    		if(currRow < 0){
    			currRow = currRow*(-1);
    		}
    		if(currCol > 285){
    			currCol = 285 - (currCol - 285);
    		}
    		if(currRow > 285){
    			currRow = 285 - (currRow - 285);
    		}
    	} else {
	    	if(currCol < 0){
	    		currCol = 0;
	    	}
	    	if(currRow < 0){
	    		currRow = 0;
	    	}
	    	if(currCol > 285){
	    		currCol = 285;
	    	}
	    	if(currRow > 285){
	    		currRow = 285;
	    	}
    	}
    	
    	//move to new position
    	JPanel newPosition = new JPanel();
    	newPosition = gridArray[currRow][currCol];
    	newPosition.setBackground(Color.PINK);
    	newPosition.repaint();
    	newPosition.revalidate();

    	System.out.println("New Position: (" + currCol + ", " + currRow + ")");
    	currentPosition = (JPanel) newPosition;
    	//check if pit/exit
    	//if()
    }
    
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if(sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);
        }
        return dbi;
    }
    
    public JLabel displayCard(String suit, String value){
    	BufferedImage image = null;
    	try {
			image = ImageIO.read(new File(path + suit + value + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	JLabel picLabel = new JLabel(new ImageIcon(scale(image, BufferedImage.TYPE_INT_RGB, 182, 300, 0.061, 0.072)));
    	return picLabel;
    }
    
    public void battlePhase(final String bossType){
    	System.out.println("Entering battle phase");
    	cardPanel.setEnabled(false);
    	cardPanel.revalidate();
    	final JFrame battle = new JFrame("BATTLE!");
        final JPanel bossZone = new JPanel();
        final JPanel playerZone = new JPanel();
        final JPanel controls = new JPanel();
        
        //figure bossLives based on bossType
        //TODO: add boss specials
        if(bossType.equals("Jack")){
        	bossLives = 3;
        } else if(bossType.equals("Queen")){
        	bossLives = 4;
        } else if(bossType.equals("King")){
        	bossLives = 5;
        }
        
        //setup battleZones
        final JLabel picLabel = displayCard("cardback", "");
        final JLabel picLabel2 = displayCard("cardback", "");
        final JButton fight = new JButton();
        final JTextArea guide = new JTextArea();
        fight.setText("Fight!");
        guide.setForeground(Color.red);
        guide.setText("Click fight to begin");
        guide.setOpaque(false);
        
        //boss draws first on fight click
        fight.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bossValue = battleDraw(bossZone);
				fight.setEnabled(false);
				fight.revalidate();
				guide.setText("Draw card");
				guide.revalidate();
				drawCard = true;
			}
        });
        playerZone.add(picLabel);
        bossZone.add(picLabel2);
        playerZone.revalidate();
		playerZone.repaint();
		bossZone.revalidate();
		bossZone.repaint();
		
		playerZone.setBorder(BorderFactory.createLineBorder(Color.black));
		playerZone.setBorder(BorderFactory
                .createTitledBorder(PLAYER_NAME + "'s HP: " + PLAYER_LIVES));
		bossZone.setBorder(BorderFactory.createLineBorder(Color.black));
		bossZone.setBorder(BorderFactory
                .createTitledBorder(bossType + "'s HP: " + bossLives));
        
        //initialize battleZones and controls
        battle.setLayout(new BorderLayout());
        battle.add(bossZone, BorderLayout.WEST);
        battle.add(controls, BorderLayout.CENTER);
        battle.add(playerZone, BorderLayout.EAST);
        controls.setLayout(new BorderLayout());
        controls.add(fight, BorderLayout.NORTH);
        controls.add(guide, BorderLayout.SOUTH);
        battle.pack();
        battle.setLocationRelativeTo(null);
        battle.setVisible(true);        
        
        //on player draw figure who does damage
        playerZone.addMouseListener(new MouseListener() { 
			@Override
			public void mouseClicked(MouseEvent me) {
				if(drawCard == true){
					int playerValue = battleDraw(playerZone);
					System.out.println(bossType + ": " + bossValue + ", " + PLAYER_NAME + ":" + playerValue);
					//deal damage
					if(bossValue < playerValue){
						guide.setText("You win");
						guide.revalidate();
						System.out.println("Player wins, 1 damage to Boss");
						bossLives--;
						bossZone.setBorder(BorderFactory
				                .createTitledBorder(bossType + "'s HP: " + bossLives));
						bossZone.revalidate();
						if(bossLives <= 0){
							System.out.println("Boss is dead");
					    	PLAYER_LEVEL++;
					    	//TODO: Add exp. popup(+1 to speed/lives)
					    	level.setText("LVL: " + PLAYER_LEVEL);
					    	level.repaint();
					    	drawListener = true;
					    	final JFrame victory = new JFrame("You Win");
					    	victory.setVisible(true);
					    	victory.setLocationRelativeTo(null);
					    	JTextArea win = new JTextArea();
					    	win.setText("You Win!");
					    	win.setOpaque(false);
					    	win.setForeground(Color.green);
					    	
					    	JButton proceed = new JButton();
					    	proceed.addActionListener(new ActionListener() { 
								@Override
								public void actionPerformed(ActionEvent e) {
									victory.dispose();
									battle.dispose();
								}
					    	});
					    	proceed.setText("Continue");
					    	proceed.setVisible(true);
					    	
					    	victory.setLayout(new BorderLayout());
					    	victory.add(win, BorderLayout.NORTH);
					    	victory.add(proceed, BorderLayout.SOUTH);
					    	victory.pack();
					    	drawCard = false;
						}
					} else if(bossValue == playerValue){
						guide.setText("You tied");
						guide.revalidate();
						System.out.println("Player tied, no damage");
						//TODO: add class special
					} else {
						guide.setText("You lose");
						guide.revalidate();
						System.out.println("Player loses, 1 damage to Player");
						PLAYER_LIVES--;
						playerZone.setBorder(BorderFactory
				                .createTitledBorder(PLAYER_NAME + "'s HP: " + PLAYER_LIVES));
						playerZone.revalidate();
						lives.setText("Lives: " + PLAYER_LIVES);
						lives.repaint();
						if(PLAYER_LIVES <= 0){
							System.out.println("Player is dead");
							//game over popup
							playerZone.setEnabled(false);
							JFrame gameOver = new JFrame("Game Over");
							gameOver.setLocationRelativeTo(null);
							gameOver.setVisible(true);
							JTextArea end = new JTextArea();
							end.setText("Game Over");
							end.setOpaque(false);
							end.setForeground(Color.red);;
							gameOver.add(end);
							gameOver.pack();
							drawCard = false;
							//TODO: add to hall of fame
							gameOver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						}
					}

					//boss draws again, playerzone resets
					secondDraw(guide, bossZone);

				} else {
					return;
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
        });
    }
    
    private void secondDraw(final JTextArea guide, final JPanel bossZone) {
    	 SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    	    	guide.setText("Draw again");
				bossValue = battleDraw(bossZone);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    	    }
    	 });
    }
    
    public int battleDraw(JPanel display){
    	Card currentCard = deck.drawCard();
		String currentSuit = currentCard.getSuit();
		String currentValue = currentCard.getValue();
		//draw until you get a non face card
		while(currentValue.length() > 2){
			currentCard = deck.drawCard();
			currentSuit = currentCard.getSuit();
			currentValue = currentCard.getValue();
		}
		//Show currentCard on GUI
		display.removeAll();
		display.repaint();
		JLabel picLabel3 = displayCard(currentSuit, currentValue);
		display.add(picLabel3);
		display.revalidate();
		display.repaint();
		return Integer.parseInt(currentValue);
    }
    
    public class TestPane extends JPanel {
        public TestPane() {
        	setLayout(layout);
        	GridBagConstraints gbc = new GridBagConstraints();
        	for (int row = 0; row < 20; row++) {
	            for (int col = 0; col < 20; col++) {
	                gbc.gridx = col;
	                gbc.gridy = row;
	
	                CellPane cellPane = new CellPane(col, row);
	                Border border = null;
	                if (row < 4) {
	                    if (col < 4) {
	                        border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
	                    } else {
	                        border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
	                    }
	                } else {
	                    if (col < 4) {
	                        border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
	                    } else {
	                        border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
	                    }
	                }
	                cellPane.setBorder(border);
	                add(cellPane, gbc);
	                gridArray[row][col] = cellPane;
	            }
	        }
        }
    }

    public class CellPane extends JPanel {

        public CellPane(final int x, final int y) {
            addMouseListener(new MouseAdapter() {
            	//add listener for draw button to move character
                @Override
                public void mouseClicked(MouseEvent e) {
                	if(notifications == null){
                		System.out.println("Notifications was null");
                		notifications.setText("Place Start");
                	}
                    if(notifications.getText().equals("Place Start")){
                    	//store component to remember start for win condition
                    	setBackground(Color.BLUE);
                    	currRow = y;
                    	currCol = x;
                    	notifications.setText("Place Exit");
                    } else if(notifications.getText().equals("Place Exit")){
                    	setBackground(Color.GREEN);
                    	EXIT = CellPane.this;
                    	notifications.setText("Place Pit");
                    } else if(notifications.getText().equals("Place Pit")){
                    	setBackground(Color.BLACK);
                    	PIT = CellPane.this;
                    	notifications.setText("Select Class");
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    coord.setText(CellPane.this.getX() + ":" + CellPane.this.getY());
                }
//
//                @Override
//                public void mouseExited(MouseEvent e) {
//                    setBackground(defaultBackground);
//                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(15, 15);
        }
    } 
}