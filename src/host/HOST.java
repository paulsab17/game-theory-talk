package host;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import backgroundClasses.Helper;
import backgroundClasses.Sprite;
import backgroundClasses.SpriteImage;
import backgroundClasses.TextBox;

public class HOST extends JFrame implements KeyListener{

	//visual stuff
	private static final int FRAME_WIDTH = 1350;
	private static final int FRAME_HEIGHT = 725;
	private static Color background = new Color(159,194,186);
	private static Color tan = new Color(202,189,128);
	private static Color rouge = new Color(216,108,112);
	private static Color dBlue = new Color(0,51,102);
	private static SpriteImage image;
	private static ArrayList<Sprite> array = new ArrayList<Sprite>();

	//visual components
	private static TextBox numBox;
	private static TextBox nameBox;
	private static TextBox titleBox;
	private static TextBox detailsBox;
	private static TextBox centerBox;


	//server stuff
	private static HostServer serv;
	private static int socketNum = 5050;

	//players stuff
	private static int playerNum;
	private static ArrayList<ClientInfo> playerList;
	private static ArrayList<String> playerNames = new ArrayList<String>();
	private static ArrayList<String> waitingNames = new ArrayList<String>();
	private static String[]formattedNames = new String[0];

	//control flow
	public static boolean ready;
	public static boolean started;
	public static boolean ended;
	private static boolean override;
	public static int stage;
	/* 0 = not started
	 * 1 = prisoner's dilemma
	 * 2 = multiply pot
	 * 3 = deer hunt
	 * 4 = bear hunt
	 * 5 = deer hunt with 4
	 * 6 = iterated peace/war
	 */

	//game data
	protected static int numBetray = 0;
	protected static int numSilent = 0;
	protected static int potMoney = 0;
	protected static final double MULT = 2;
	protected static int numDeer = 0;
	protected static int numHare = 0;
	


	public HOST(){
		playerNum = Integer.parseInt(JOptionPane.showInputDialog(null, "How many players are joining?"));
		numBox = new TextBox(""+playerNames.size()+"/"+playerNum,FRAME_WIDTH/4,FRAME_HEIGHT/2,Color.black,40);
		nameBox = new TextBox(formattedNames,FRAME_WIDTH*3/4,FRAME_HEIGHT*3/5,Color.DARK_GRAY,30);
		titleBox = new TextBox(new String[]{"",""},FRAME_WIDTH/2,FRAME_HEIGHT/6,tan,Color.DARK_GRAY,60);
		detailsBox = new TextBox(new String[]{"",""},FRAME_WIDTH/4,FRAME_HEIGHT/2,rouge,20);
		centerBox = new TextBox("",FRAME_WIDTH/2,FRAME_HEIGHT*3/5,dBlue,25);
		image = new SpriteImage(array);

		ready = false;
		started = false;
		ended = false;
		override = false;
		stage = 0;

		playerList = new ArrayList<ClientInfo>();

		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

	}
	public static void initializeServer(){
		try {
			serv = new HostServer(playerNum,socketNum,playerList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				serv.end();
			}
		}, "Shutdown-thread"));

		/* a weird voodoo magic-y way to allow mouse inputs that creates
		 * code that will run once and involves instantiating an object
		 * of this class within the main method of this class for some reason
		 */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {			
			public void run(){
				HOST frame = new HOST();
				frame.setTitle("Host");
				frame.setResizable(false);
				frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setBackground(background);
				frame.getContentPane().add(image);
				frame.pack();
				frame.setVisible(true); 

				Timer mover = new Timer(5,stuffMover);
				mover.start();

				initializeServer();
			}
		});
	}

	private static ActionListener stuffMover = new ActionListener(){
		public void actionPerformed(ActionEvent evt) {
			if(!ready){
				playerNames = ClientInfo.getNames(playerList);
				formatNames(playerNames);
				nameBox.setText(formattedNames);
				numBox.setText(""+playerNames.size()+"/"+playerNum);

				array.clear();
				array.add(nameBox);
				array.add(numBox);
				image.repaint();
			}else if(!started){
				playerNames = ClientInfo.getNames(playerList);
				formatNames(playerNames);
				nameBox.setText(formattedNames);
				numBox.setText(""+playerNames.size()+"/"+playerNum+" - READY!");

				array.clear();
				array.add(nameBox);
				array.add(numBox);
				image.repaint();
			}else if(stage==1){
				if(!waitingNames.isEmpty()){
					for(ClientInfo c: playerList){
						if(c.newMessage){
							if(c.getMessage().equals("silent")){
								numSilent++;
								c.resetNew();
								waitingNames.remove(c.name);
							}else if(c.getMessage().equals("betray")){
								numBetray++;
								c.resetNew();
								waitingNames.remove(c.name);
							}else{
								System.out.println("Something went wrong");
							}
						}
					}
					formatNames(waitingNames);
					nameBox.setText(formattedNames);
					if(override){
						for(ClientInfo c: playerList){
							if(waitingNames.contains(c.name)){
								String s;
								if(Math.random()<0.5){
									s = "betray";
								}else{
									s = "silent";
								}
								c.setMessage(s);
							}
						}
						override = false;
					}
					if(waitingNames.isEmpty()){
						prisonersResults();
					}
					array.clear();
					array.add(titleBox);
					array.add(detailsBox);
					array.add(nameBox);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(centerBox);
					image.repaint();
				}
			}else if(stage==2){
				if(!waitingNames.isEmpty()){
					for(ClientInfo c: playerList){
						if(c.newMessage){
							int donation = Integer.parseInt(c.getMessage());
							potMoney+=donation;
							c.resetNew();
							waitingNames.remove(c.name);
						}
					}
					formatNames(waitingNames);
					nameBox.setText(formattedNames);
					if(override){
						for(ClientInfo c: playerList){
							if(waitingNames.contains(c.name)){
								c.setMessage("0");
							}
						}
						override = false;
					}
					if(waitingNames.isEmpty()){
						potResults();
					}
					array.clear();
					array.add(titleBox);
					array.add(detailsBox);
					array.add(nameBox);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(centerBox);
					image.repaint();
				}
			}else if(stage==3){
				
				if(!waitingNames.isEmpty()){
					for(ClientInfo c: playerList){
						if(c.newMessage){
							if(c.getMessage().equals("deer")){
								numDeer++;
								c.resetNew();
								waitingNames.remove(c.name);
							}else if(c.getMessage().equals("hare")){
								numHare++;
								c.resetNew();
								waitingNames.remove(c.name);
							}else{
								System.out.println("Something went wrong");
							}
						}
					}
					formatNames(waitingNames);
					nameBox.setText(formattedNames);
					if(override){
						for(ClientInfo c: playerList){
							if(waitingNames.contains(c.name)){
								String s;
								if(Math.random()<0.5){
									s = "deer";
								}else{
									s = "hare";
								}
								c.setMessage(s);
							}
						}
						override = false;
					}
					if(waitingNames.isEmpty()){
						deerResults();
					}
					array.clear();
					array.add(titleBox);
					array.add(detailsBox);
					array.add(nameBox);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(centerBox);
					image.repaint();
				}
			}
		}
	};

	protected static void setReady(boolean a){
		ready = a;
		serv.initInfoListeners();
		String[] str = {"setStage","0"};
		serv.massPrint(str);
	}
	protected static void setStarted(boolean a){
		started = a;
	}
	protected static void setEnded(boolean a){
		ended = a;
	}
	private static void formatNames(ArrayList<String> names){
		formattedNames = new String[(int)Math.ceil(names.size()/3.0)];
		for(int i=0;i<formattedNames.length;i++){
			formattedNames[i] = "";
		}
		for(int i=0;i<names.size();i++){	
			formattedNames[i/3] +=names.get(i)+"  ";
		}
	}

	@SuppressWarnings("unchecked")
	private static void initPrisoners(){
		String[] t = {"Prisoners Dilemma:","Betray your partner or stay silent."};
		titleBox.setText(t);
		waitingNames = (ArrayList<String>) playerNames.clone();
		String[] deets = {"Both betray - 2 years each",
				"You betray, partner stays silent - you: 0 years, partner: 3 years",
				"You stay silent, partner betrays - you: 3 years, partner: 0 years",
				"Both stay silent - 1 year each"
		};
		detailsBox.setText(deets);

		String[] str = {"setStage","1"};
		serv.massPrint(str);
	}
	@SuppressWarnings("unchecked")
	private static void prisonersResults(){
		double numYears = 0;
		ArrayList<ClientInfo> unmatched = (ArrayList<ClientInfo>) playerList.clone();
		while(unmatched.size()>1){
			ClientInfo a = unmatched.remove(Helper.randMinMax(0,unmatched.size()-1));
			ClientInfo b = unmatched.remove(Helper.randMinMax(0,unmatched.size()-1));
			String[] aMsg = {"result","1",b.name,b.getMessage()};
			String[] bMsg = {"result","1",a.name,a.getMessage()};
			serv.printTo(aMsg, a);
			serv.printTo(bMsg, b);
			numYears+=Helper.getJailTime(a.getMessage(), b.getMessage());
			numYears+=Helper.getJailTime(b.getMessage(), a.getMessage());
		}
		if(!unmatched.isEmpty()){
			ClientInfo a = playerList.get(Helper.randMinMax(0, playerList.size()-1));
			String[] msg = {"result","1",a.name,a.getMessage()};
			serv.printTo(msg, unmatched.get(0));
			numYears+=Helper.getJailTime(unmatched.get(0).getMessage(), a.getMessage());
		}
		String[] str = {""+numBetray+" people betrayed",""+numSilent+" people stayed silent","The average jail time was "+Helper.trunc(numYears/playerNum)};
		centerBox.setText(str);
	}
	@SuppressWarnings("unchecked")
	private static void initPot(){
		String[] t = {"Public Goods:","Choose how much money to donate."};
		titleBox.setText(t);
		waitingNames = (ArrayList<String>) playerNames.clone();
		String[] deets = {"You start with $10.",
				"Choose how much of it you want to be placed in a public",
				"pot using the buttons, then click confirm. After everyone",
				"has chosen, the amount in the pot will be multiplied by "+MULT,
				"then distributed equally back to everyone (including those",
				"who did not contribute)."
		};
		detailsBox.setText(deets);
		potMoney = 0;
		String[] str = {"setStage","2"};
		serv.massPrint(str);
	}
	private static void potResults(){
		int multPotMoney = (int) (potMoney*MULT);
		int give = multPotMoney/playerNum;
		String[] str = {"result","2",""+give};
		serv.massPrint(str);

		String[] res = {"There was $"+potMoney+" in the pot.",
				"This grew to $"+multPotMoney,
				"Each player was given $"+give+"."
		};
		centerBox.setText(res);
	}
	@SuppressWarnings("unchecked")
	private static void initDeer(){
		String[] t = {"Deer Hunt:","Stay to trap deer or leave to catch hare?"};
		titleBox.setText(t);
		waitingNames = (ArrayList<String>) playerNames.clone();
		String[] deets = {"You and a friend are hunting deer to survive...",
				"Both wait for the deer - 2 food each",
				"You wait for deer, partner goes for hare - you: 0, partner: 1",
				"You go for hare, partner waits for deer - you: 1, partner: 0",
				"Both go for the hare - 1 food each"
		};
		detailsBox.setText(deets);
		String[] str = {"setStage","3"};
		serv.massPrint(str);
	}
	@SuppressWarnings("unchecked")
	private static void deerResults(){
		int gotDeer = 0;
		int gotHare = 0;
		int gotNothing = 0;
		ArrayList<ClientInfo> unmatched = (ArrayList<ClientInfo>) playerList.clone();
		while(unmatched.size()>1){
			ClientInfo a = unmatched.remove(Helper.randMinMax(0,unmatched.size()-1));
			ClientInfo b = unmatched.remove(Helper.randMinMax(0,unmatched.size()-1));
			String[] aMsg = {"result","3",b.name,b.getMessage()};
			String[] bMsg = {"result","3",a.name,a.getMessage()};
			serv.printTo(aMsg, a);
			serv.printTo(bMsg, b);
			switch (Helper.getDeerFood(a.getMessage(), b.getMessage())){
			case 2:
				gotDeer++;
				break;
			case 1:
				gotHare++;
				break;
			case 0:
				gotNothing++;
				break;
			}
			switch (Helper.getDeerFood(b.getMessage(), a.getMessage())){
			case 2:
				gotDeer++;
				break;
			case 1:
				gotHare++;
				break;
			case 0:
				gotNothing++;
				break;
			}
		}
		if(!unmatched.isEmpty()){
			ClientInfo a = playerList.get(Helper.randMinMax(0, playerList.size()-1));
			String[] msg = {"result","3",a.name,a.getMessage()};
			serv.printTo(msg, unmatched.get(0));
			switch (Helper.getDeerFood(unmatched.get(0).getMessage(), a.getMessage())){
			case 2:
				gotDeer++;
				break;
			case 1:
				gotHare++;
				break;
			case 0:
				gotNothing++;
				break;
			}
		}
		String[] str = {""+numDeer+" people waited for deer.",
				""+numHare+" people went for the hare.",
				"",
				""+gotDeer+" people ate deer.",
				""+gotHare+" people ate hare.",
				""+gotNothing+" people ate nothing."
				};
		centerBox.setText(str);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			if(ready && !started){
				started = true;
				stage = 1;
				initPrisoners();
			}else if(stage==1){
				stage = 2;
				initPot();
			}else if(stage==2){
				stage = 3;
				initDeer();
			}
		}else if(e.getKeyCode()==KeyEvent.VK_ENTER){
			if(stage==2){
				initPot();
			}
		}else if(e.getKeyCode()==KeyEvent.VK_O){
			override = true;
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}
}
