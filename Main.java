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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {
	
	final static String path = "C:\\Users\\AB04191\\workspace\\DungeonCards\\bin\\Cards\\";	
	final static String helpTxt = "";
	public static Deck deck = new Deck();
	public static String PLAYER_NAME = "";
	public static String PLAYER_CLASS = "";
	public static String direction = "";
	
	public static int oldRow = 0, oldCol = 0, turns = 0, bossLives = 0, bossValue = 0, strongWill = 0, exp = 0, secondWind = 0;
	public static int PLAYER_LIVES = 10;
	public static int PLAYER_SPEED = 0;
	public static int PLAYER_LEVEL = 1;
	public final static JTextArea level = new JTextArea();
	public final static JTextArea lives = new JTextArea();
	public final static JButton livesUp = new JButton();
	public final static JTextArea speed = new JTextArea();
	public final static JButton speedUp = new JButton();
	public final static JTextArea coord = new JTextArea();
	public final static JTextArea CLASS = new JTextArea();
    public static JTextArea guide = new JTextArea();
    public static JPanel bossZone = new JPanel();
    public static JPanel playerZone = new JPanel();
	
	//public gui components
	public static JFrame frame = new JFrame("Dungeon Cards");
	public static int currRow;
	public static int currCol;
	public static int gridRows = 20, gridCols = 20;
	public static JPanel[][] gridArray = new JPanel[gridRows][gridCols];
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
	final static JFrame battle = new JFrame("BATTLE!");
	public static JPanel newPosition = new JPanel();
	public static JPanel currentPosition = new JPanel();
    public static boolean drawListener = true;
    public boolean drawCard = false;
    public boolean testing = false;
    
    public static void main(String[] args) {
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
                		turns++;
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
								if(testing == true){
									System.out.println("Testing mode, no battles");
									notifications.setText("Testing mode, no battles");
								} else {
									notifications.setText("Battle: " + currentValue);
									//remove face card from deck once a boss battle occurs
									drawListener = false;
									battlePhase(currentValue);
								}
								deck.dispose(currentCard);
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
                frame.add(notifications, BorderLayout.SOUTH);
                
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                cardPanel.setVisible(false);
            }
        });
    }
    
    public static void switchLvlButtons(Boolean b){
    	livesUp.setEnabled(b);
		livesUp.setVisible(b);
		speedUp.setEnabled(b);
		speedUp.setVisible(b);
    }
    
    public static void levelUp(String ATTR){
    	if(ATTR.equals("speed")){
    		PLAYER_SPEED++;
    		speed.setText("Speed: " + PLAYER_SPEED);
			speed.revalidate();
    	} else if(ATTR.equals("lives")){
    		PLAYER_LIVES+=5;
    		lives.setText("Lives: " + PLAYER_LIVES);
			lives.revalidate();
    	}
		exp--;
		if(exp == 0){
			switchLvlButtons(false);
		}
    }
    
    public static void setupLvlButtons(){
    	switchLvlButtons(false);
    	livesUp.setText("+5");
    	livesUp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				levelUp("lives");
			}
    	});
    	speedUp.setText("+");
    	speedUp.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			levelUp("speed");
    		}
    	});
    }
    
    public static void setupCharacter(){
    	notifications.setText("Type Name");
        notifications.setForeground(Color.red);
    	notifications.setOpaque(false);
    	final JTextArea name = new JTextArea();
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

    	setupLvlButtons();
    	character.add(name);
    	character.add(confirm);
    	character.add(level);
    	character.add(lives);
    	character.add(livesUp);
    	character.add(speed);
    	character.add(speedUp);
    	character.setSize(50, 200);
    	character.setBorder(BorderFactory
                .createTitledBorder("Character"));
    	
    	CLASS.setEditable(false);
    	CLASS.setOpaque(false);
    	CLASS.setText("Class: " + PLAYER_CLASS);
    	character.add(CLASS);
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
		   		    	selectClass();
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
    	int value = Integer.parseInt(currValue);
    	//erase current position color
    	currentPosition = gridArray[currRow][currCol];
    	System.out.println("Current Position: (" + currCol + ", " + currRow + ")");
    	oldCol = currCol;
    	oldRow = currRow;
    	//determine direction of movement based on suit
    	switch(suit) {
    		case "S": 	direction = "NORTH";
    					currRow = currRow - (value + PLAYER_SPEED);
    					break;
    		case "D":	direction = "SOUTH";
    					currRow = currRow + (value + PLAYER_SPEED);
    					break;
    		case "C":	direction = "EAST";
    					currCol = currCol + (value + PLAYER_SPEED);
    					break;
    		case "H":	direction = "WEST";
    					currCol = currCol - (value + PLAYER_SPEED);
    					break;
    	}
    	
    	//stop at the edge of the board, unless Ninja class
    	//ClassSp: Ninja - Active Reflex
    	if(PLAYER_CLASS.equals("Ninja")){
    		if(currCol < 0){
    			currCol = currCol*(-1);
    		}
    		if(currRow < 0){
    			currRow = currRow*(-1);
    		}
    		if(currCol >= gridCols){
    			currCol = (gridCols-1) - (currCol - (gridCols-1));
    		}
    		if(currRow >= gridRows){
    			currRow = (gridRows-1) - (currRow - (gridRows-1));
    		}
    	} else {
	    	if(currCol < 0){
	    		currCol = 0;
	    	}
	    	if(currRow < 0){
	    		currRow = 0;
	    	}
	    	if(currCol >= gridCols){
	    		currCol = gridCols-1;
	    	}
	    	if(currRow >= gridRows){
	    		currRow = gridRows-1;
	    	}
    	}
    	
    	//move to new position with steps
    	//TODO: fix steps
    	System.out.println("New Position: (" + currCol + ", " + currRow + ")"); 
    	newPosition = gridArray[currRow][currCol];
    	
    	//vertical movement
    	if(oldRow != currRow){
    		final Timer timer = new Timer(100, null);
			timer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
    		    	currentPosition.setBackground(Color.LIGHT_GRAY);
	    	    	currentPosition.repaint();
	    	    	currentPosition.revalidate();
	    	    	System.out.println("Cleared background for row: " + oldRow);
	    	    	
	    	    	oneStep();
	    	    	ninjaStep();
	    	    	
	    	    	//check if newPosition as currentPosition is pit/exit
	    	    	currentPosition = gridArray[oldRow][currCol];
	    	    	if(currentPosition.getBackground() == Color.GREEN){
	    	    		youWin("end");
	    	    		timer.stop();
	    	    		//TODO: add to hall of fame
	    	    	} else if(currentPosition.getBackground() == Color.BLACK) {
	    	    		gameOver();
	    	    		timer.stop();
	    	    		//TODO: add to hall of fame
	    	    	}
	    	    		
	    	    	//color newPosition as currentPosition
	    			currentPosition.setBackground(Color.BLUE);
	    	    	currentPosition.repaint();
	    	    	currentPosition.revalidate();
	    	    	System.out.println("Set newpostion background to red for row: " + oldRow);
	    	    	
	    	    	if (oldRow == currRow) {
    		            timer.stop();
    		        }
				}
			});
			timer.start();
    		System.out.println("Old Row: " + oldRow + ", Curr Row: " + currRow);
    	} else if(oldCol != currCol){
    		final Timer timer = new Timer(100, null);
    		timer.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent evt) {
    		    	currentPosition.setBackground(Color.LIGHT_GRAY);
	    	    	currentPosition.repaint();
	    	    	currentPosition.revalidate();
	    	    	System.out.println("Cleared background for col: " + oldCol);
	    	    	
	    	    	oneStep();	    	    	
	    	    	ninjaStep();
	    	    	
	    	    	currentPosition = gridArray[currRow][oldCol];
	    	    	//check if newPosition as currentPosition is pit/exit
	    	    	if(currentPosition.getBackground() == Color.GREEN){
	    	    		youWin("end");
	    	    		timer.stop();
	    	    		//TODO: add to hall of fame
	    	    	} else if(currentPosition.getBackground() == Color.BLACK) {
	    	    		gameOver();
	    	    		timer.stop();
	    	    		//TODO: add to hall of fame
	    	    	}
	    	    	//color newPosition as currentPosition
	    			currentPosition.setBackground(Color.BLUE);
	    	    	currentPosition.repaint();
	    	    	currentPosition.revalidate();
	    	    	System.out.println("Set newpostion background to red for col: " + oldCol);
	    	    	
   		            if (oldCol == currCol) {
    		            timer.stop();
    		        }
    		    }    
    		});
    	    timer.start();
    		System.out.println("Old Col: " + oldCol + ", Curr Col: " + currCol);
    	} else {
    		do{
	    		oneStep();
		    	ninjaStep();
    		} while(oldCol != currCol || oldRow != currRow);	
    	}
    }
    
    public static void oneStep(){
    	//take one step
    	if(direction.equals("EAST")){
    		oldCol++;
    	} else if(direction.equals("WEST")){
    		oldCol--;
    	} else if(direction.equals("SOUTH")){
    		oldRow++;
    	} else if(direction.equals("NORTH")){
    		oldRow--;
    	}
    }
    
    public static void ninjaStep(){
    	if(oldCol < 0){
    		direction = "EAST";
    		oldCol = 1;
    	} else if(oldCol >= gridCols){
    		direction = "WEST";
    		oldCol = gridCols-1;
    	} else if(oldRow < 0){
    		direction = "SOUTH";
    		oldRow = 1;
    	} else if(oldRow >= gridRows){
    		direction = "NORTH";
    		oldRow = gridRows-1;
    	}
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
    
    public static void selectClass(){
    	final JFrame classFrame = new JFrame("Select Class");
    	classFrame.setLocationRelativeTo(null);
    	final JPanel description = new JPanel();
    	String[] data = {"Ninja", "Athlete", "Soldier", "Tank", "Rogue", "Swordsman"};
    	JList<String> myList = new JList<String>(data);
    	final JButton confirm = new JButton();
    	ListSelectionModel listSelectionModel = myList.getSelectionModel();

    	listSelectionModel.addListSelectionListener( new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
	            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	            if (!lsm.isSelectionEmpty()) {
	                // Find out which indexes are selected.
	                int minIndex = lsm.getMinSelectionIndex();
	                int maxIndex = lsm.getMaxSelectionIndex();
	                for (int i = minIndex; i <= maxIndex; i++) {
	                    if (lsm.isSelectedIndex(i)) {
	                    	String desc = null, className = null;
	                        switch(i){
	                        	case 0: desc = "Active Reflex: " + '\n' + "If you encounter a wall, you turn around and finish your movement " + '\n' + "in the opposite direction you were initially moving";
	                        			className = "Ninja";
	                        			break;
	                        	case 1: desc = "Controlled Dash: " + '\n' + "After defeating a boss, draw a card, " + '\n' + "move that number of squares in any direction of your choice, discard that card";
	                        			className = "Athlete";
	                        			break;
	                        	case 2: desc = "Strong Will: " + '\n' + "If you are defeated by a boss, " + '\n' + "you can make a saving draw which replaces the last draw you made in combat";
		                    			className = "Soldier";
		                    			break;
	                        	case 3: desc = "Damage Sponge: " + '\n' + "You start with 5 extra lives";
		                    			className = "Tank";
		                    			break;
	                        	case 4: desc = "Critical hit: " + '\n' + "you may draw 3 cards during combat instead of 1 once per boss, use the highest of the 3";
		                    			className = "Rogue";
		                    			break;
	                        	case 5: desc = "Guard Break: " + '\n' + "On a tie, you do 1 damage to the Boss";
	                        			className = "Swordsman";
	                        			break;
	                        }
	                        JTextArea classDes = new JTextArea();
	            			classDes.setText(desc);
	            			description.removeAll();
	            			description.revalidate();
	            			description.add(classDes);
	            			confirm.setEnabled(true);
	            			classFrame.pack();
	            			PLAYER_CLASS = className;
	                    }
	                }
	            }
    		}
        });
        confirm.setText("Confirm");
        confirm.setToolTipText("Confirm Class Selection");
        confirm.setVisible(true);
    	classFrame.setLayout(new BorderLayout());
    	classFrame.add(myList, BorderLayout.WEST);
    	classFrame.add(description, BorderLayout.EAST);
    	classFrame.add(confirm, BorderLayout.SOUTH);
    	confirm.setEnabled(false);
    	confirm.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				classFrame.dispose();
				CLASS.setText("Class: " + PLAYER_CLASS);
				CLASS.revalidate();
				//ClassSp: Tank - Damage Sponge
				if(PLAYER_CLASS.equals("Tank")){
					System.out.println("Damage Sponge, setting lives to 15");
					PLAYER_LIVES = 15;
					lives.setText("Lives: " + PLAYER_LIVES);
					lives.revalidate();
				}
				notifications.setText("Draw Card");
				notifications.revalidate();
			}
    	});
    	classFrame.setVisible(true);
    	classFrame.pack();
    }
    
    public void battlePhase(final String bossType){
    	playerZone.removeAll();
    	bossZone.removeAll();
    	
    	System.out.println("Entering battle phase");
    	strongWill = 0;
    	
    	//disable draw
	    cardPanel.setEnabled(false);
	    cardPanel.revalidate();
    	
        final JPanel controls = new JPanel();
        
        //figure bossLives based on bossType
        //TODO: add boss specials(to OP?)
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
        final JTextArea lastMatch = new JTextArea();
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
        controls.add(lastMatch, BorderLayout.CENTER);
        controls.add(guide, BorderLayout.SOUTH);
        battle.pack();
        battle.setLocationRelativeTo(null);
        battle.setVisible(true);  
        battle.revalidate();
        battle.repaint();
        battle.pack();
        
        //on player draw figure who does damage
        //TODO: fix switch bossType on draw
        playerZone.addMouseListener(new MouseListener() { 
			@Override
			public void mouseClicked(MouseEvent me) {
				System.out.println("Boss Type before events: " + bossType);
				if(drawCard == true){
					int playerValue = battleDraw(playerZone);
					System.out.println(bossType + ": " + bossValue + ", " + PLAYER_NAME + ":" + playerValue);
					lastMatch.setText("Last Match" + '\n' + bossType + ": " + bossValue + ", " + PLAYER_NAME + ":" + playerValue);
					lastMatch.revalidate();
					battle.pack();
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
							//if boss is Queen and secondWind has not already been used
							if(bossType.equals("Queen") && secondWind == 0){
								secondWind = 1;
								bossLives = 1;
								bossValue = battleDraw(bossZone);
								System.out.println("Queen's Second Wind: " + bossValue);
								guide.setText("Queen's Second Wind: " + bossValue);
								guide.revalidate();
								if(bossValue < playerValue){
									playerWins();
								} else if(bossValue == playerValue){
									tie(bossType);
								} else {
									playerLoses();
								}
							} else {
								playerWins();
							}
						}
					} else if(bossValue == playerValue){
						tie(bossType);
					} else {
						playerLoses();
					}

					System.out.println("Boss Type after events: " + bossType);
					//boss draws again
					if(drawCard == true && strongWill != 1){
						secondDraw(guide, bossZone);
					}

					System.out.println("Boss Type after secondDraw: " + bossType);
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
    
    public void tie(String bossType){
    	if(PLAYER_CLASS.equals("Swordsman")){
			guide.setText("Critical Hit");
			guide.revalidate();
			System.out.println("Swordsman critical hit, 1 damage to boss");
			bossLives--;
			bossZone.setBorder(BorderFactory
	                .createTitledBorder(bossType + "'s HP: " + bossLives));
			bossZone.revalidate();
			if(bossLives <= 0){
				playerWins();
			}
		} else {
			guide.setText("You tied");
			guide.revalidate();
			System.out.println("Player tied, no damage");
		}
    }
    
    public void playerLoses(){
    	guide.setText("You lose");
		guide.revalidate();
		System.out.println("Player loses, 1 damage to Player");
		PLAYER_LIVES--;
		playerZone.setBorder(BorderFactory
                .createTitledBorder(PLAYER_NAME + "'s HP: " + PLAYER_LIVES));
		playerZone.revalidate();
		lives.setText("Lives: " + PLAYER_LIVES);
		lives.repaint();
		System.out.println("Current player lives: " + PLAYER_LIVES);
		if(PLAYER_LIVES <= 0){
			//ClassSp: Soldier - Strong Will
			if(PLAYER_CLASS.equals("Soldier") && strongWill == 0){
				strongWill = 1;
				System.out.println("Strong will, try again");
				guide.setText("Srong Will, draw again");
				PLAYER_LIVES++;
				playerZone.setBorder(BorderFactory
		                .createTitledBorder(PLAYER_NAME + "'s HP: " + PLAYER_LIVES));
				playerZone.revalidate();
				lives.setText("Lives: " + PLAYER_LIVES);
				lives.repaint();
			} else {
				System.out.println("Player is dead");
				//game over popup
				playerZone.setEnabled(false);
				gameOver();
				drawCard = false;
				secondWind = 0;
				//TODO: add to hall of fame
			}
		}
    }
    
    public void playerWins(){
	    System.out.println("Boss is dead");
		PLAYER_LEVEL++;
		level.setText("LVL: " + PLAYER_LEVEL);
		level.repaint();
		drawListener = true;
		drawCard = false;
		youWin("");
		switchLvlButtons(true);
		secondWind = 0;
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
    
    public static JFrame gameOver(){
    	drawListener = false;
    	JFrame gameOver = new JFrame("Game Over");
		gameOver.setLocationRelativeTo(null);
		gameOver.setVisible(true);
		JTextArea end = new JTextArea();
		end.setText("Game Over");
		end.setOpaque(false);
		end.setForeground(Color.red);
		gameOver.add(end);
		gameOver.pack();
		gameOver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return gameOver;
    }
    
    public static JFrame youWin(String endGame){
    	final JFrame victory = new JFrame("You Win");
    	victory.setVisible(true);
    	victory.setLocationRelativeTo(null);
    	JTextArea win = new JTextArea();
    	win.setText("You Win!");
    	win.setOpaque(false);
    	win.setForeground(Color.green);
    	drawListener = false;
    	
    	JButton proceed = new JButton();
    	proceed.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawListener = true;
				battle.getContentPane().removeAll();
				battle.dispose();
				victory.dispose();

				//ClassSp: Athlete - Controlled Dash
				if(PLAYER_CLASS.equals("Athlete")){
                	notifications.setText("Select Direction for Controlled Dash");
                	notifications.revalidate();
		    		System.out.println("Controlled Dash");
		    		final JFrame controlledDash = new JFrame("Controlled Dash");
		    		final JTextArea valueText = new JTextArea();
		    		Card c = deck.drawCard();
		    		while(c.getValue().length() > 2){
		    			c = deck.drawCard();
		    		}
                	final String value = c.getValue();
                	valueText.setText("Select direction to move " + value + " spaces");
                	valueText.setEnabled(false);
		    		String[] data = {"North", "South", "East", "West"};
		    		JList<String> myList = new JList<String>(data);
		        	ListSelectionModel listSelectionModel = myList.getSelectionModel();

		        	listSelectionModel.addListSelectionListener( new ListSelectionListener(){
		    			public void valueChanged(ListSelectionEvent e) {
		    	            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		    	            if (!lsm.isSelectionEmpty()) {
		    	                // Find out which indexes are selected.
		    	                int minIndex = lsm.getMinSelectionIndex();
		    	                int maxIndex = lsm.getMaxSelectionIndex();
		    	                for (int i = minIndex; i <= maxIndex; i++) {
		    	                    if (lsm.isSelectedIndex(i)) {
		    	                    	
		    	                    	notifications.setText("Draw Again");
		    	                    	notifications.revalidate();
		    	                        switch(i){
		    	                        	case 0: moveCharacter("S", value);
		    	                        			break;
		    	                        	case 1: moveCharacter("D", value);
		    	                        			break;
		    	                        	case 2: moveCharacter("C", value);
		    	                        			break;
		    	                        	case 3: moveCharacter("H", value);
		    	                        			break;
		    	                        }
		    	                        controlledDash.dispose();
		    	                        notifications.setText("Draw Again");
		    	                    }
		    	                }
		    	            }
		    			}
		        	});
		        	
		        	controlledDash.setLayout(new BorderLayout());
		        	controlledDash.add(valueText, BorderLayout.NORTH);
		        	controlledDash.add(myList, BorderLayout.SOUTH);
		        	controlledDash.setLocationRelativeTo(null);
		        	controlledDash.setVisible(true);
		        	controlledDash.pack();
		    	} else {
		    		notifications.setText("Draw Again");
		    	}
			}
    	});
    	proceed.setText("Continue");
    	proceed.setVisible(true);
    	
    	victory.setLayout(new BorderLayout());
    	victory.add(win, BorderLayout.NORTH);
    	if(endGame.equals("end")){
    		victory.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	} else {
    		victory.add(proceed, BorderLayout.SOUTH);
    		exp++;
    	}
    	victory.pack();
    	return victory;
    }
    
    public class TestPane extends JPanel {
        public TestPane() {
        	setLayout(layout);
        	GridBagConstraints gbc = new GridBagConstraints();
        	for (int row = 0; row < gridRows; row++) {
	            for (int col = 0; col < gridCols; col++) {
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
	                cellPane.setBackground(Color.LIGHT_GRAY);
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