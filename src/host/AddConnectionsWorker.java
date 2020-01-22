package host;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

public class AddConnectionsWorker extends SwingWorker<Integer,ClientInfo>{
	
	private int currentClientNum;
	private int goalClientNum;
	private ServerSocket serSo;
	private ArrayList<ClientInfo> destList;
	
	public AddConnectionsWorker(int cliNum,ServerSocket ss,ArrayList<ClientInfo> destination){
		currentClientNum = 0;
		goalClientNum = cliNum;
		serSo = ss;
		destList = destination;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		
		while(currentClientNum<goalClientNum){
			ClientInfo c = new ClientInfo(serSo.accept());
			publish(c);
			currentClientNum++;
		}
		
		return currentClientNum;
	}
	
	@Override
    protected void process(List<ClientInfo> chunks) {
        for (ClientInfo c : chunks) {
            destList.add(c);
        }
    }
	
	 @Override
     protected void done() {
         HOST.setReady(true);
     }
}
