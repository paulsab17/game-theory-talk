package host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientInfo {
	protected Socket s;
	protected PrintWriter pw;
	protected BufferedReader rR;
	protected String name;
	private String message;
	protected boolean newMessage = false;
	protected ArrayList<String> messageHistory = new ArrayList<String>();
	
	public ClientInfo(Socket sock){
		s = sock;
		try {
			pw = new PrintWriter(s.getOutputStream());
			rR = new BufferedReader(new InputStreamReader(s.getInputStream()));
			name = rR.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<String> getNames(ArrayList<ClientInfo> list){
		ArrayList<String> res = new ArrayList<String>();
		for(ClientInfo c: list){
			res.add(c.name);
		}
		
		return res;
	}
	public void setMessage(String s){
		messageHistory.add(message);
		message = s;
		newMessage = true;
	}
	public void resetNew(){
		newMessage = false;
	}
	public String getMessage(){
		return message;
	}
}
