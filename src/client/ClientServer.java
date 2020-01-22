package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientServer {

	protected Socket s;
	protected PrintWriter pw;
	protected BufferedReader rR;
	protected String name;
	private String message;
	protected boolean newMessage = false;
	protected ArrayList<String> messageHistory = new ArrayList<String>();
	protected GetHostMessageWorker worker;

	public ClientServer(int socketPort, InetAddress ip,String n) throws IOException {
		name = n;
		s = new Socket(ip, socketPort);
		pw = new PrintWriter(s.getOutputStream());
		rR = new BufferedReader(new InputStreamReader(s.getInputStream()));
		pw.println(name);
		pw.flush();
		
		worker = new GetHostMessageWorker(this);
		worker.execute();
		
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

	public void print(String s){
		pw.println(s);
		pw.flush();
	}
	public void printDoubles(double d[]){
		String s = "";
		s+=d[0];
		for(int i = 1; i<d.length; i++){
			s+="/"+d[i];
		}
		pw.println(s);
		pw.flush();
	}
	public double[] getDoubles() throws IOException{

		String s = rR.readLine();

		String[] arr = s.split("/");

		double[] d = new double[arr.length];

		for(int i = 0; i<d.length; i++){
			d[i] = Double.parseDouble(arr[i]);
		}

		return d;
	}
	public void end(){
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
