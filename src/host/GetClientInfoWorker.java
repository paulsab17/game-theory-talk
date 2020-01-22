package host;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

public class GetClientInfoWorker extends SwingWorker<Boolean,String>{	

	private ClientInfo client;

	public GetClientInfoWorker(ClientInfo cli){
		client = cli;

	}

	@Override
	protected Boolean doInBackground() throws Exception {
		while(!HOST.ended){
			try {
				String s = client.rR.readLine();
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
			client.setMessage(s);
		}
	}
}
