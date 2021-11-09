import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameMain extends JFrame implements ActionListener,KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -925518549680460124L;
	
	private bomber bomberman,bomberman2;
	private enemy enemy[] = new enemy[4];
	private bomb bomb_ex[] = new bomb[5];
	private bomb bomb2_ex[] = new bomb[5];
	
	private walls bricks;
	private int[][] map = { {0,0,1,0,1,1,1,1,1,0,0,0},{0,2,1,0,2,0,1,2,1,0,2,0},{0,1,0,0,0,0,1,1,1,1,0,1},{0,0,0,0,2,1,0,2,1,1,0,1},{0,1,0,0,1,0,0,1,1,1,0,1},{0,2,0,1,2,1,0,2,1,1,2,1},{0,1,0,1,1,1,0,1,1,1,0,1} };
	private int[][]bombermanPosition = new int [7][12];
	private int[][]bomberman2Position = new int [7][12];
	private int player1flag=0, player2flag=0; //flag to identify when the game is over 
	//labels to show the graphics
	private JLabel enemyLabel[] =  new JLabel[4];
	private JLabel bombermanLabel,bomberman2Label;
	private JLabel bombLabel[] = new JLabel[5];
	private JLabel bomb2Label[] = new JLabel[5];
	private JButton startButton;
	private JLabel[][] brickLabel = new JLabel[map.length][map[0].length];
	private ImageIcon bombermanImage, bombermanDownImage, bomberman2DownImage,bricksImage, bricksImage2,emptyImage, bombImage, enemyImage;
	private JLabel  scoreboardLabel,scores1Label,scores2Label,player1Label,player2Label;
	private String player1_name="",player2_name="";
	private int[] enemy_down = new int[4];
	private int points1,points2; 
	//container to hold graphics
	private Container content;
	private Connection conn = null;
	private Statement stmt = null;
	
	//GUI setup
	public GameMain() {
		super("Bomberman");
		setSize(GameProperties.SCREEN_WIDTH, GameProperties.SCREEN_HEIGHT);
		bombermanPosition[0][0]=1;
		bomberman2Position[0][11]=1;
		/////////////////////////////////////////////////Maze///////////////////////////////////////////////////////
		bricks = new walls();
		bricksImage = new ImageIcon( getClass().getResource( bricks.getFilename() ) );
		bricksImage2 = new ImageIcon( getClass().getResource( "walls2.png" ) );
		emptyImage = new ImageIcon( getClass().getResource( "white.png" ) );
		
		//setting values to the bricks label according to the map
		for (int i=0; i< map.length ; i++) {
			
			for (int j=0; j< map[i].length ; j++) {
				if( map[i][j]==1) {
					brickLabel[i][j] = new JLabel();
					brickLabel[i][j].setIcon(bricksImage);
					brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());
					}else if( map[i][j]==2) {
						brickLabel[i][j] = new JLabel();
						brickLabel[i][j].setIcon(bricksImage2);
						brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());
					} else {
							brickLabel[i][j] = new JLabel();
							brickLabel[i][j].setIcon(emptyImage);
							brickLabel[i][j].setSize(bricks.getWidth(),bricks.getHeight());	
						}
				if( j==0 ) {
					bricks.setX(bricks.getX());
					brickLabel[i][j].setLocation(bricks.getX(), bricks.getY());
				}else {
					bricks.setX(bricks.getX()+100);
					brickLabel[i][j].setLocation(bricks.getX(), bricks.getY());
					}
			}
			bricks.setX(0);
			bricks.setY(bricks.getY()+100);
		}
		
		
		/////////////////////////////////////////////////Bomberman///////////////////////////////////////////////////////
		bomberman = new bomber();
		bombermanLabel = new JLabel();
		bombermanImage = new ImageIcon( getClass().getResource( bomberman.getFilename() ) );
		bombermanLabel.setIcon(bombermanImage); 
		bombermanLabel.setSize(bomberman.getWidth(),bomberman.getHeight());	
		bombermanLabel.setVisible(bomberman.getVisible());
		
		bombermanDownImage = new ImageIcon( getClass().getResource( "smallninja2.png" ) );
		
		/////////////////////////////////////////////////Bomberman2///////////////////////////////////////////////////////
		bomberman2 = new bomber();
		bomberman2Label = new JLabel();
		bombermanImage = new ImageIcon( getClass().getResource( bomberman2.getFilename() ) );
		bomberman2Label.setIcon(bombermanImage); 
		bomberman2Label.setSize(bomberman2.getWidth(),bomberman2.getHeight());	
		bomberman2Label.setVisible(bomberman2.getVisible());
		
		bomberman2DownImage = new ImageIcon( getClass().getResource( "smallninja2.png" ) );
		
		/////////////////////////////////////////////////Bomb///////////////////////////////////////////////////////
		for (int i=0; i<5 ; i++) {
			bomb_ex[i] = new bomb();
			bombLabel[i] = new JLabel();
			bombLabel[i].setIcon(emptyImage); 
			bombLabel[i].setSize(bomb_ex[i].getWidth(),bomb_ex[i].getHeight());
			
		}
		
		for (int i=0; i<5 ; i++) {
			bomb2_ex[i] = new bomb();
			bomb2Label[i] = new JLabel();
			bomb2Label[i].setIcon(emptyImage); 
			bomb2Label[i].setSize(bomb2_ex[i].getWidth(),bomb2_ex[i].getHeight());
			
		}
		
		/////////////////////////////////////////////////Enemy///////////////////////////////////////////////////////
		
		enemy[0] = new enemy();
		enemy[1] = new enemy();
		enemy[2] = new enemy(false);
		enemy[3] = new enemy(false);
		
		for (int i =0; i<4 ; i++) {
			
			enemyImage = new ImageIcon( getClass().getResource( enemy[i].getFilename() ) );
			enemyLabel[i] = new JLabel();
			enemyLabel[i].setIcon(enemyImage); 
			enemyLabel[i].setSize(enemy[i].getWidth(),enemy[i].getHeight());
			
			enemy[i].setEnemyLabel(enemyLabel[i]); 
			enemy[i].setBomberman(bomberman); //collision with bomberman
			enemy[i].setBombermanLabel(bombermanLabel);
			enemy[i].setBomberman2(bomberman2); //collision with bomberman
			enemy[i].setBomberman2Label(bomberman2Label);
			enemy[i].setBomb(bomb_ex[0]);
			enemy[i].setBomb2(bomb2_ex[0]);//collision for initial bomb
			enemy[i].setBombEx(bomb_ex[1],bomb_ex[2],bomb_ex[3], bomb_ex[4],bomb2_ex[1],bomb2_ex[2],bomb2_ex[3], bomb2_ex[4]);//for collision with the explosion
			enemyLabel[i].setVisible(enemy[i].getVisible());
			
		}
		
		/////////////////////////////////////////////////Interface///////////////////////////////////////////////////////
		startButton = new JButton("Start Game");
		startButton.setSize(100,100);
		startButton.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-150);
		add(startButton);
		startButton.addActionListener(this);
		startButton.setFocusable(false);
		for (int i =0; i<4 ; i++) {
			enemy[i].setAnimationButton(startButton);
		}
		
		this.addKeyListener(this);
		
		scoreboardLabel = new JLabel("<html><body><center>CURRENT<br>SCORE</center></body></html>");
		scoreboardLabel.setSize(100,50);
		scoreboardLabel.setLocation(GameProperties.SCREEN_WIDTH-130,GameProperties.SCREEN_HEIGHT-700);
		
		player1Label = new JLabel(player1_name);
		player1Label.setSize(100,50);
		player1Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-600);
		
		player2Label = new JLabel(player2_name);
		player2Label.setSize(100,50);
		player2Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-400);
		
		scores1Label = new JLabel();
		scores1Label.setSize(100,50);
		scores1Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-550);
		
		scores2Label = new JLabel();
		scores2Label.setSize(100,50);
		scores2Label.setLocation(GameProperties.SCREEN_WIDTH-150,GameProperties.SCREEN_HEIGHT-350);
		
		add(player1Label);
		add(player2Label);
		add(scoreboardLabel);
		add(scores1Label);
		add(scores2Label);
		
		content = getContentPane();
		content.setBackground(Color.white);
		setLayout(null);
		
		bomberman.setCoordinates(25, 0);
		bomberman2.setCoordinates(1025, 0);
		enemy[0].setCoordinates(125, 300);
		enemy[1].setCoordinates(425, 200);
		enemy[2].setCoordinates(625, 500);
		enemy[3].setCoordinates(1025, 300);
		bomb_ex[0].setCoordinates(0, 0);
		bomb2_ex[0].setCoordinates(0, 0);
		
		//adding labels and update position for bomberman 		
		add(bombermanLabel);
		bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
		
		add(bomberman2Label);
		bomberman2Label.setLocation(bomberman2.getX(), bomberman2.getY());
		
		//adding labels for enemy 
		for (int i =0; i<4 ; i++) {
			add(enemyLabel[i]);
			enemyLabel[i].setLocation(enemy[i].getX(),enemy[i].getY());
		}
		
		//adding bomb labels
		for (int i=0; i< 5 ; i++) {
			add(bombLabel[i]);
			bombLabel[i].setLocation(bomb_ex[i].getX(), bomb_ex[i].getY());
			add(bomb2Label[i]);
			bomb2Label[i].setLocation(bomb2_ex[i].getX(), bomb2_ex[i].getY());
		}
				
		//adding bricksLabel
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				add(brickLabel[i][j]);
			}
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main (String[] args) {
		GameMain myGame = new GameMain();
		myGame.setVisible(true);
	}


	@Override
	////////////////////////////////////BOMBERMAN 1 CONTROLS //////////////////////////////////////////
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			
			//Set the boundaries for bomberman
			movingDown(bomberman, bombermanPosition, bombermanLabel);
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			
			//Set the boundaries for bomberman
			movingUp(bomberman, bombermanPosition, bombermanLabel);
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			
			//Set the boundaries for bomberman
			movingRight(bomberman, bombermanPosition, bombermanLabel);
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			
			//Set the boundaries for bomberman
			movingLeft(bomberman, bombermanPosition, bombermanLabel);
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			
			Timer timer = new Timer();
			TimerTask task = new TimerTask(){
				public void run() {
					processExplosion(bombLabel, bomb_ex);
				}
			};
			
			TimerTask task2 = new TimerTask(){
				public void run() {
					displayWinner(bombLabel, bomb_ex,1,scores1Label,player1_name);
				}
			};
			
			//Process: 
			//Change image for the initial bomb 
			bombImage = new ImageIcon( getClass().getResource( bomb_ex[0].getFilename() ) );
			bombLabel[0].setIcon(bombImage);
			//Place the bomb in the same place the bomberman is located
			bombLabel[0].setLocation(bomberman.getX(), bomberman.getY());
			//giving values X and Y so it can recognize collision with rectangles
			bomb_ex[0].setCoordinates(bomberman.getX(),bomberman.getY());
			
			// explosion and collisions with walls and bomberman
			timer.schedule(task, 1000);
			
			// reseting the bomb image
			timer.schedule(task2, 2000);
			
		}
		/////////////////////////////////////////////BOMBERMAN 2 CONTROLS////////////////////////////////////////////////////////////
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			
			//Set the boundaries for bomberman
			movingDown(bomberman2, bomberman2Position, bomberman2Label);
			
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_W) {
			
			//Set the boundaries for bomberman
			movingUp(bomberman2, bomberman2Position, bomberman2Label);
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_D) {
			
			//Set the boundaries for bomberman
			movingRight(bomberman2, bomberman2Position, bomberman2Label);
		}
		else if (e.getKeyCode() == KeyEvent.VK_A) {
			
			//Set the boundaries for bomberman
			movingLeft(bomberman2, bomberman2Position, bomberman2Label);
		}
		
		else if (e.getKeyCode() == KeyEvent.VK_X) {
			
			Timer timer = new Timer();
			
			TimerTask task = new TimerTask(){
				public void run() {
					
					processExplosion(bomb2Label, bomb2_ex);
				}
			};
			
			TimerTask task2 = new TimerTask(){
				public void run() {
					
					displayWinner(bomb2Label, bomb2_ex,2,scores2Label,player2_name);
				
				}
			};
			
			//Process: 
			//Change image for the initial bomb 
			bombImage = new ImageIcon( getClass().getResource( bomb2_ex[0].getFilename() ) );
			bomb2Label[0].setIcon(bombImage);
			//Place the bomb in the same place the bomberman is located
			bomb2Label[0].setLocation(bomberman2.getX(), bomberman2.getY());
			//giving values X and Y so it can recognize collision with rectangles
			bomb2_ex[0].setCoordinates(bomberman2.getX(),bomberman2.getY());
			
			// explosion and collisions with walls and bomberman
			timer.schedule(task, 1000);
			
			// reseting the bomb image
			timer.schedule(task2, 2000);
			
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == startButton) {
			
			points1 = 0;
			points2 = 0;
			for (int i=0; i< 4 ; i++) {
				enemy_down[i]=0;
			}
			
			player1_name = JOptionPane.showInputDialog("Enter the name of player 1");
			player2_name = JOptionPane.showInputDialog("Enter the name of player 2");
			scores1Label.setText(String.valueOf(points1));
			scores2Label.setText(String.valueOf(points2));
			////////////////////////////Save player's name into database
			try {
				Class.forName("org.sqlite.JDBC");
				System.out.println("Database Driver Loaded");
				
				String dbURL = "jdbc:sqlite:product.db";
				conn = DriverManager.getConnection(dbURL);
				
				if (conn != null) {
					System.out.println("Connected to database");
					conn.setAutoCommit(false);
					
					stmt = conn.createStatement();
										
					//String sql = "DROP TABLE SCORES";
					//stmt.executeUpdate(sql);
					//conn.commit();
					
					String sql = "CREATE TABLE IF NOT EXISTS SCORES " +
					             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
							     " NAME TEXT NOT NULL, " + 
					             " SCORE INT NOT NULL) ";
					
					stmt.executeUpdate(sql);
					conn.commit();
					System.out.println("Table Created Successfully");
					
					sql = "INSERT INTO SCORES (NAME, SCORE) VALUES " + 
	                        "('"+ player1_name+"', 0)";
					stmt.executeUpdate(sql);
					conn.commit();
					
					sql = "INSERT INTO SCORES (NAME, SCORE) VALUES " + 
	                        "('"+ player2_name+"', 0)";
					stmt.executeUpdate(sql);
					conn.commit();
										
					conn.close();
				}
				
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//Display player name into scoreboard
			player1Label.setText(player1_name);
			player2Label.setText(player2_name);
			
			map = new int[][] { {0,0,1,0,1,1,1,1,1,0,0,0},{0,2,1,0,2,0,1,2,1,0,2,0},{0,1,0,0,0,0,1,1,1,1,0,1},{0,0,0,0,2,1,0,2,1,1,0,1},{0,1,0,0,1,0,0,1,1,1,0,1},{0,2,0,1,2,1,0,2,1,1,2,1},{0,1,0,1,1,1,0,1,1,1,0,1} };
			
			//setting values to the bricks label according to the map
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
					if( map[i][j]==1) {
						brickLabel[i][j].setIcon(bricksImage);
						}else if( map[i][j]==2) {
							brickLabel[i][j].setIcon(bricksImage2);
						} else {
								brickLabel[i][j].setIcon(emptyImage);
						}
				}
			}
			
			bomberman.show();
			bombermanLabel.setVisible(bomberman.getVisible());
			bombermanLabel.setIcon(bombermanImage);
			bomberman.setCoordinates(25, 0);
			bombermanPosition = new int [7][12];
			bombermanPosition[0][0] = 1; 
			bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
			
			bomberman2.show();
			bomberman2Label.setVisible(bomberman2.getVisible());
			bomberman2Label.setIcon(bombermanImage);
			bomberman2.setCoordinates(1025, 0);
			bomberman2Position = new int [7][12];
			bomberman2Position[0][10] = 1; 
			bomberman2Label.setLocation(bomberman2.getX(), bomberman2.getY());
			
			
			player1flag=0;
			player2flag=0;
			
			for (int i=0; i< 4 ; i++) {
				enemyLabel[i].setIcon(enemyImage); 
				enemy[i].setEnemyLabel(enemyLabel[i]);
				enemy[i].show();
				enemy[i].setEnemyAlive(true);
				enemyLabel[i].setVisible(enemy[i].getVisible());
			}
			
			startButton.setText("Re-start");
			
			for (int i=0; i< 4 ; i++) {
				if (!enemy[i].getMoving()) { //check if enemies are moving
					//start moving
					enemy[i].moveEnemy();
				}
			}
			
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

///////////////////////////////////////////////////DATABASE///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public void scorePoints(int current_score , String player_name) {
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="UPDATE SCORES SET SCORE = "+current_score+" WHERE NAME='"+player_name +"'"; 
                stmt.executeUpdate(sql);
				conn.commit();
								
				conn.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
///////////////////////////////////////////////////////////////CONTROLS/////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void movingDown(bomber bomberman, int[][] bombermanPosition, JLabel bombermanLabel) {
		if ((bomberman.getY()+200)<GameProperties.SCREEN_HEIGHT) { 
			
			loop :  // Loop to handle the position of bomberman and positions available in the map
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
				
					if(bombermanPosition[i][j]==1) { // check for current bomberman position
						if(map[i+1][j]==0) {    // check if the next space is available
							bombermanPosition[i][j]=0;		//change current position to 0
							bombermanPosition[i+1][j]=1;	//the new bomberman position
							bomberman.setY(bomberman.getY()+GameProperties.CHARACTER_STEP);
							bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
							break loop;// Once the position for bomberman has changed then finish loop to keep bomberman from moving further
						}
					}
				}
			}
		}
	}
	
	public void movingUp(bomber bomberman, int[][] bombermanPosition, JLabel bombermanLabel) {
		if ((bomberman.getY())>0) { 
			
			loop :
				for (int i=0; i< map.length ; i++) {
					for (int j=0; j< map[i].length ; j++) {
					
						if(bombermanPosition[i][j]==1) { 
							if(map[i-1][j]==0) {
								bombermanPosition[i][j]=0;
								bombermanPosition[i-1][j]=1;
								bomberman.setY(bomberman.getY()-GameProperties.CHARACTER_STEP);
								bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
								break loop;
							}
						}
					}
				}	
		}
	}
	
	public void movingRight(bomber bomberman, int[][] bombermanPosition, JLabel bombermanLabel) {
		if ((bomberman.getX()+125)<GameProperties.SCREEN_WIDTH) { 
			
			loop :
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
				
					if(bombermanPosition[i][j]==1) {
						if(map[i][j+1]==0) {
							bombermanPosition[i][j]=0;
							bombermanPosition[i][j+1]=1;
							bomberman.setX(bomberman.getX()+GameProperties.CHARACTER_STEP);
							bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
							break loop;
						}
					}
				}
			}
	}
	}
	
	public void movingLeft(bomber bomberman, int[][] bombermanPosition, JLabel bombermanLabel) {
		if ((bomberman.getX())-25>0) {
			
			loop :
				for (int i=0; i< map.length ; i++) {
					for (int j=0; j< map[i].length ; j++) {
					
						if(bombermanPosition[i][j]==1) {
							if(map[i][j-1]==0) {
								bombermanPosition[i][j]=0;
								bombermanPosition[i][j-1]=1;
								bomberman.setX(bomberman.getX()-GameProperties.CHARACTER_STEP);
								bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
								break loop;
							}
						}
					}
				}
		}
	}
	
//////////////////////////////////////////////////////INTERACTION WITH THE BOMBS/////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void processExplosion(JLabel[] bomb, bomb[] bomb_ex) {
		
		// checking if there is an unbreakable wall besides the bomb, if not then there is an explosion
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				
				//bomb explosions check spaces beside the bomb
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-25)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()-100))
						&& map[i][j]!=2) {
					bomb[1].setIcon(bombImage); 
					bomb[1].setLocation(bomb[0].getX(), bomb[0].getY()-100);
					bomb_ex[1].setCoordinates(bomb[0].getX(),bomb[0].getY()-100);
					}
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-25)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()+100))
						&& map[i][j]!=2) {
					bomb[2].setIcon(bombImage); 
					bomb[2].setLocation(bomb[0].getX(), bomb[0].getY()+100);
					bomb_ex[2].setCoordinates(bomb[0].getX(),bomb[0].getY()+100);
					} 
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()+75)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()))
						&& map[i][j]!=2) {
					bomb[3].setIcon(bombImage);
					bomb[3].setLocation(bomb[0].getX()+100, bomb[0].getY());
					bomb_ex[3].setCoordinates(bomb[0].getX()+100,bomb[0].getY());	
					}
				if ((brickLabel[i][j].getLocation().getX() == (bomb[0].getX()-125)) && (brickLabel[i][j].getLocation().getY() == (bomb[0].getY()))
					&& map[i][j]!=2) {
					bomb[4].setIcon(bombImage);
					bomb[4].setLocation(bomb[0].getX()-100,bomb[0].getY() );
					bomb_ex[4].setCoordinates(bomb[0].getX()-100,bomb[0].getY());
				}
				
			}
		}

		//Erase walls in case there was an explosion		
		for (int i=0; i< map.length ; i++) {
			for (int j=0; j< map[i].length ; j++) {
				
				//bomb explosions check spaces beside the bomb
				for (int k=1; k< 5 ; k++) {
					if ((brickLabel[i][j].getLocation().getX() == (bomb[k].getLocation().getX()-25)) 
							&& (brickLabel[i][j].getLocation().getY() == (bomb[k].getLocation().getY())) 
							&& map[i][j]!=2) {
						brickLabel[i][j].setIcon(emptyImage);
						map[i][j]=0;
					}
				}
				
				for (int k=0; k< 5 ; k++) {
					//check if bomberman was caught in the explosions 
					if (((bombermanLabel.getLocation().getX()-25) == (bomb[k].getLocation().getX()-25)) 
							&& (bombermanLabel.getLocation().getY() == (bomb[k].getLocation().getY()))) {
						bombermanLabel.setIcon(bombermanDownImage); 
						player1flag=1;
					}
					
					if (((bomberman2Label.getLocation().getX()-25) == (bomb[k].getLocation().getX()-25)) 
							&& (bomberman2Label.getLocation().getY() == (bomb[k].getLocation().getY()))) {
						bomberman2Label.setIcon(bomberman2DownImage); 
						player2flag=1;
					}
				}
											
			}
		}
	}
	
///////////////////////////////////////////////////////DISPLAY WIN AND LOSE WHEN PLAYER CLEARS THE GAME////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void displayWinner(JLabel[] bomb, bomb[] bomb_ex, int player, JLabel scoreLabel, String player_name) {
		
		int points = 0;
		
		for (int i=0; i< 5 ; i++) {
			bomb_ex[i].setCoordinates(0,0);
			bomb[i].setLocation(bomb_ex[i].getX(), bomb_ex[i].getY());
			bomb[i].setIcon(emptyImage);
			}
			
			//hide enemy if they are caught by explosion
			
			for (int i=0; i< 4 ; i++) {
				if (enemy[i].getEnemyAlive()==false) { 
					enemy[i].hide();
					if (enemy_down[i]==0) {
						if (player==1) {
							points = points1 + 1000;
							points1 = points;
						} else {
							points = points2 + 1000;
							points2 = points;
						}
												
						scoreLabel.setText(String.valueOf(points));
						scorePoints(points,player_name);
						enemy_down[i]=1;
					}
				}
			} 
			
			//send message if bomberman dies
			if (player1flag==1) {
				JOptionPane.showMessageDialog(null, "Player 1 Died! Better luck next time!", "Ooops!", JOptionPane.INFORMATION_MESSAGE);
				player1flag=0;
				bombermanLabel.setVisible(false);
			}
			
			if (player2flag==1) {
				JOptionPane.showMessageDialog(null, "Player 2 Died! Better luck next time!", "Ooops!", JOptionPane.INFORMATION_MESSAGE);
				player2flag=0;
				bomberman2Label.setVisible(false);
			}
			
			if (player1flag==1 && player2flag==1) {
				JOptionPane.showMessageDialog(null, "Both players died", "GAME OVER!", JOptionPane.INFORMATION_MESSAGE);
				displayAllScores();
			}
			
			//message if all enemies are down
			if(enemy[0].getEnemyAlive()==false && enemy[1].getEnemyAlive()==false && enemy[2].getEnemyAlive()==false && enemy[3].getEnemyAlive()==false) {
				JOptionPane.showMessageDialog(null, "YOU WON!", "CONGRATULATIONS!", JOptionPane.INFORMATION_MESSAGE);
				displayAllScores();
				bomberman.hide();
				
			}
			
			for (int i=0; i< 4 ; i++) {
				enemyLabel[i].setVisible(enemy[i].getVisible());
			}
	}
	
	////////////////////////////////////////////////////DISPLAY SCORES///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void displayAllScores() {
		
		String[] id_array = new String[1] ;
		String[] name_array = new String[1];
		String[] score_array = new String[1];
		
		int counter= 0;
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                ResultSet rs = stmt.executeQuery(sql);
				while ( rs.next() ) {
					counter=counter+1;
				}
				rs.close();
			}
			
			if (conn != null) {
				System.out.println("Connected to database");
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                
				ResultSet rs = stmt.executeQuery(sql);
				
				id_array = new String [counter];
				name_array = new String [counter];
				score_array = new String [counter];
				
				counter = 0;
				
				while ( rs.next() ) {
					
					int id = rs.getInt("id");
					String name = rs.getString("name");
					int score = rs.getInt("score");
					id_array[counter] = String.valueOf(id);
					name_array[counter] = name;
					score_array[counter] = String.valueOf(score);
					counter=counter+1;
				}
				
				rs.close();
				conn.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder(64);
		
		String[] record = new String[5];
		
		if(counter>5) {counter=5;}
		
		for (int i=0; i<counter; i++) {
		
			record[i] = "<td>" + String.valueOf(id_array[i]) + "</td><td>"+ String.valueOf(name_array[i]) + "</td><td>" + String.valueOf(score_array[i]) + "</td>";
		
		}
		
		sb.append("<html><table><tr><td>Player</td><td>Name</td><td>Score</td></tr>");
	    
	    for (int i=0; i<5; i++) {
	    	sb.append("<tr>").append(record[i]).append("</tr>");
	    }
	    
	    sb.append("</table></html>");
	    
		JOptionPane.showMessageDialog(null, sb, "Top Scores", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
		
}

