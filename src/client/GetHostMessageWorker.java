package client;

import java.io.IOException;
import java.util.List;

import host.HOST;

import javax.swing.SwingWorker;

public class GetHostMessageWorker extends SwingWorker<Boolean,String>{

	ClientServer server;
	
	public GetHostMessageWorker(ClientServer cs){
		server = cs;
	}
	
	@Override
	protected Boolean doInBackground() {
		while(!HOST.ended){
			try {
				String s = server.rR.readLine();
				publish(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	protected void process(List<String> chunks) {
		for(String s:chunks){
			server.setMessage(s);
		}
	}
}
