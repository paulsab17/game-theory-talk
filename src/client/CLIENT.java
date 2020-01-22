package client;

import host.HOST;
import host.HostServer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import backgroundClasses.Helper;
import backgroundClasses.Sprite;
import backgroundClasses.SpriteImage;
import backgroundClasses.TextBox;

public class CLIENT extends JFrame implements MouseListener{
	//visual stuff
	private static final int FRAME_WIDTH = 1350;
	private static final int FRAME_HEIGHT = 725;
	private static Color background = new Color(159,194,186);
	private static Color tan = new Color(202,189,128);
	private static Color rouge = new Color(216,108,112);
	private static Color greenish = new Color(109,192,102);
	private static Color dBlue = new Color(0,51,102);
	private static SpriteImage image;
	private static ArrayList<Sprite> array = new ArrayList<Sprite>();

	//visual components
	private static TextBox titleBox;
	private static TextBox detailsBox;
	private static TextBox leftButton;
	private static TextBox rightButton;
	private static TextBox bottomButton;

	//server stuff
	private static ClientServer serv;
	private static int socketNum = 5050;
	private static String machineName = "170-18sabharwpr";
	private static InetAddress address;

	//other stuff
	private static String name;
	private static boolean sent;
	private static int stage;

	//game stuff
	private static String prisonerChoice;
	private static int yourMoney = 10;
	private static int potDonation = 0;
	private static String deerChoice;

	public CLIENT(){
		image = new SpriteImage(array);
		try{
			address = InetAddress.getByName(machineName);
			name = JOptionPane.showInputDialog(null, "Enter your name:");
			serv = new ClientServer(socketNum,address,name);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		sent = false;
		stage = 0;

		titleBox = new TextBox(new String[]{"Waiting for players",("You are "+name)},FRAME_WIDTH/2,FRAME_HEIGHT/6,tan,Color.DARK_GRAY,60);
		leftButton = new TextBox("",FRAME_WIDTH/5,FRAME_HEIGHT*3/5,greenish,Color.black,40);
		rightButton = new TextBox("",FRAME_WIDTH*4/5,FRAME_HEIGHT*3/5,rouge,Color.black,40);
		detailsBox = new TextBox(new String[]{""},FRAME_WIDTH/2,FRAME_HEIGHT*3/5,dBlue,35);
		bottomButton = new TextBox("",FRAME_WIDTH/2,FRAME_HEIGHT*4/5,dBlue,Color.white,35);

		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		addMouseListener(this);
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
				CLIENT frame = new CLIENT();
				frame.setTitle("Player: "+name);
				frame.setResizable(false);
				frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setBackground(background);
				frame.getContentPane().add(image);
				frame.pack();
				frame.setVisible(true); 

				Timer mover = new Timer(10,stuffMover);
				mover.start();
			}
		});
	}

	private static ActionListener stuffMover = new ActionListener(){
		public void actionPerformed(ActionEvent evt) {
			String[] message;
			if(serv.newMessage){
				String s = serv.getMessage();
				message = s.split("/");
				serv.resetNew();
				if(message[0].equals("setStage")){
					switch (message[1]){
					case "0":
						String[] str = {"Ready to Start!","You are "+name};
						titleBox.setText(str);
						break;
					case "1":
						String[] t = {"Prisoners Dilemma:","Betray your partner or stay silent."};
						titleBox.setText(t);
						leftButton.setText("STAY SILENT");
						rightButton.setText("BETRAY");
						stage = 1;
						sent = false;
						break;
					case "2":
						String[] t2 = {"Public Goods:","Choose how much money to donate."};
						titleBox.setText(t2);
						leftButton.setText("Give more");
						rightButton.setText("Give less");
						detailsBox.setText(new String[]{"You have: $"+yourMoney,"Donating: $"+potDonation});
						bottomButton.setText("CONFIRM");
						stage = 2;
						sent = false;
						break;
					case "3":
						String[] t3 = {"Deer Hunt:","Stay to trap deer or leave to catch hare?"};
						titleBox.setText(t3);
						leftButton.setText("WAIT FOR DEER");
						rightButton.setText("GO FOR HARE");
						stage = 3;
						sent = false;
					default:
						break;
					}
				}else if(message[0].equals("result")){
					switch (message[1]){
					case"1":
						if(!sent){
							if(Math.random()<0.5){
								prisonerChoice = "betray";
							}else{
								prisonerChoice = "silent";
							}
						}
						String oppName = message[2];
						String oppChoice = message[3];
						String s1 = "You chose ";
						if(prisonerChoice.equals("silent")){
							s1+="STAY SILENT";
						}else if(prisonerChoice.equals("betray")){
							s1+="BETRAY";
						}
						String s2 = oppName+" chose ";
						if(oppChoice.equals("silent")){
							s2+="STAY SILENT";
						}else if(oppChoice.equals("betray")){
							s2+="BETRAY";
						}
						String s3 = "You got "+Helper.getJailTime(prisonerChoice, oppChoice)+" years";
						String s4 = "They got "+Helper.getJailTime(oppChoice,prisonerChoice)+" years";
						detailsBox.setText(new String[]{s1,s2,s3,s4});
						break;
					case "2":
						if(!sent){
							potDonation = 0;
						}
						int gotBack = Integer.parseInt(message[2]);
						yourMoney+=gotBack;
						bottomButton.setText("You donated: $"+potDonation+" and got back $"+gotBack);
						potDonation = 0;
						detailsBox.setText(new String[]{"You have: $"+yourMoney,"Donating: $"+potDonation});
						break;
					case "3":
						if(!sent){
							if(Math.random()<0.5){
								deerChoice = "deer";
							}else{
								deerChoice = "hare";
							}
						}
						String oppName2 = message[2];
						String oppChoice2 = message[3];
						String t1 = "You chose ";
						if(deerChoice.equals("hare")){
							t1+="GO FOR HARE";
						}else if(prisonerChoice.equals("deer")){
							t1+="WAIT FOR DEER";
						}
						String t2 = oppName2+" chose ";
						if(oppChoice2.equals("hare")){
							t2+="GO FOR HARE";
						}else if(oppChoice2.equals("deer")){
							t2+="WAIT FOR DEER";
						}
						String t3 = "You";
						switch (Helper.getDeerFood(deerChoice, oppChoice2)){
						case 2:
							t3+=" ate deer!";
							break;
						case 1:
							t3+=" ate hare.";
							break;
						case 0:
							t3+=" ate nothing.";
							break;
						}
						String t4 = "They";
						switch (Helper.getDeerFood(oppChoice2, deerChoice)){
						case 2:
							t4+=" ate deer!";
							break;
						case 1:
							t4+=" ate hare.";
							break;
						case 0:
							t4+=" ate nothing.";
							break;
						}
						detailsBox.setText(new String[]{t1,t2,t3,t4});
						break;
					default:
						break;
					}
					sent = true;
				}
			}
			if(stage==0){
				array.clear();
				array.add(titleBox);
				image.repaint();
			}else if(stage==1){
				if(!sent){
					array.clear();
					array.add(titleBox);
					array.add(leftButton);
					array.add(rightButton);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(detailsBox);
					image.repaint();
				}
			}else if(stage==2){
				if(!sent){
					detailsBox.setText(new String[]{"You have: $"+yourMoney,"Donating: $"+potDonation});
					array.clear();
					array.add(titleBox);
					array.add(leftButton);
					array.add(rightButton);
					array.add(bottomButton);
					array.add(detailsBox);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(bottomButton);
					array.add(detailsBox);
					image.repaint();
				}
			}else if(stage==3){
				if(!sent){
					array.clear();
					array.add(titleBox);
					array.add(leftButton);
					array.add(rightButton);
					image.repaint();
				}else{
					array.clear();
					array.add(titleBox);
					array.add(detailsBox);
					image.repaint();
				}
			}
		}
	};

	@Override
	public void mouseClicked(MouseEvent e) {
		Point  realPoint = new Point(e.getPoint());
		realPoint.translate(0, -20);
		if(stage==1 && !sent){
			if(leftButton.isInside(realPoint)){
				serv.print("silent");
				detailsBox.setText(new String[]{"You chose","STAY SILENT"});
				sent = true;
				prisonerChoice = "silent";
			}else if(rightButton.isInside(realPoint)){
				serv.print("betray");
				detailsBox.setText(new String[]{"You chose","BETRAY"});
				sent = true;
				prisonerChoice = "betray";
			}
		}else if(stage==2 && !sent){
			if(bottomButton.isInside(realPoint)){
				String s = ""+potDonation;
				serv.print(s);
				yourMoney-=potDonation;
				bottomButton.setText("You donated: $"+potDonation);
				detailsBox.setText(new String[]{"You have: $"+yourMoney,"Donating: $"+potDonation});
				sent = true;
			}else if(leftButton.isInside(realPoint)){
				if(potDonation<yourMoney){
					potDonation++;
				}
			}else if(rightButton.isInside(realPoint)){
				if(potDonation>0){
					potDonation--;
				}
			}
		}else if(stage==3 && !sent){
			if(leftButton.isInside(realPoint)){
				serv.print("deer");
				detailsBox.setText(new String[]{"You chose","WAIT FOR DEER"});
				sent = true;
				deerChoice = "deer";
			}else if(rightButton.isInside(realPoint)){
				serv.print("hare");
				detailsBox.setText(new String[]{"You chose","GO FOR HARE"});
				sent = true;
				deerChoice = "hare";
			}
		}


	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
