package host;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class HostServer {

	private ServerSocket ss;
	private ArrayList<ClientInfo> clients;
	private int clientNum;
	protected AddConnectionsWorker addConn;
	protected ArrayList<GetClientInfoWorker> cliListeners;

	/*
	public HostServer(){
		clientNum = 0;
		clients = new ArrayList<ClientInfo>();
	}
	*/
	public HostServer(int cliNum,int socketPort,ArrayList<ClientInfo> cliList) throws IOException {
		clientNum = cliNum;
		clients = cliList;
		ss = new ServerSocket(socketPort);
		
		addConn = new AddConnectionsWorker(clientNum,ss,clients);
		addConn.execute();
	}
	public void initInfoListeners(){
		cliListeners = new ArrayList<GetClientInfoWorker>();
		for(ClientInfo c: clients){
			GetClientInfoWorker worker = new GetClientInfoWorker(c);
			cliListeners.add(worker);
			worker.execute();
		}
	}
	
	protected void printTo(String[] msg,ClientInfo c){
		String s = "";
		s+=msg[0];
		for(int i = 1; i<msg.length; i++){
			s+="/"+msg[i];
		}
		c.pw.println(s);
		c.pw.flush();
	}
	protected void massPrint(String[] msg){
		String s = "";
		s+=msg[0];
		for(int i = 1; i<msg.length; i++){
			s+="/"+msg[i];
		}
		for(ClientInfo c: clients){
			c.pw.println(s);
			c.pw.flush();
		}
	}
	public void printDoubles(double[] d,ClientInfo client){
		String s = "";
		s+=d[0];
		for(int i = 1; i<d.length; i++){
			s+="/"+d[i];
		}
		client.pw.println(s);
		client.pw.flush();
	}
	public double[] getDoubles(ClientInfo client) throws IOException{

		String s = client.rR.readLine();

		String[] arr = s.split("/");


		double[] d = new double[arr.length];

		for(int i = 0; i<d.length; i++){
			d[i] = Double.parseDouble(arr[i]);
		}

		return d;
	}
	public void end(){
		try {
			for(ClientInfo c: clients){
				c.s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
