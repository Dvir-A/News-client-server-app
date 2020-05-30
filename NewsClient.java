package mmn16_2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

import javax.swing.*;

public class NewsClient extends JFrame{
	private JList<String> newsList;
	private JLabel statusBar;
	private Vector<String> newsVec = new Vector<>(); 
	private MulticastSocket socket;
	private final String groupIP = "230.0.0.1";
	private final int port =7777;
	private InetAddress group;
	private boolean isInGroup=false;
	private boolean received = false;
	
	public NewsClient() {
		super("News Client");
		try {
			//socket = new MulticastSocket(port);
			group = InetAddress.getByName(groupIP);
		} catch (IOException e) {
			System.exit(1);
		}
		
		newsList = new JList<>();
		JButton clearB = new JButton("Clear");
		JButton joinB = new JButton("Join");
		JButton quitB = new JButton("Leave");
		ActionListener lis = new ActionLis();
		clearB.addActionListener(lis);
		joinB.addActionListener(lis);
		quitB.addActionListener(lis);
		JPanel buttonP = new JPanel(new GridLayout(3,1,10,10));
		buttonP.add(joinB);
		buttonP.add(quitB);
		buttonP.add(clearB);
		statusBar = new JLabel();
		//JPanel statusPanel = new JPanel(new BorderLayout());
		//statusPanel.add(statusBar,BorderLayout.WEST);
		//statusPanel.add(new JSeparator(SwingConstants.VERTICAL),BorderLayout.NORTH);
		//statusPanel.add(new JLabel("Server :"+group.getCanonicalHostName()),BorderLayout.EAST);
		this.add(buttonP,BorderLayout.EAST);
		this.add(new JScrollPane(newsList),BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400,400);
		this.setVisible(true);
	}
	
	private class NewsListener extends Thread{
		/**
		 * create thread that listen to the news
		 */
		public  NewsListener() {  super("NewsListener");  }
		public void run() {
			try {
				socket = new MulticastSocket(port);
				socket.joinGroup(group);
				isInGroup=true;
				startClient();
			} catch (IOException e) {
			}finally {
				quit();
			}	
		}
	}

	/**
	 * quit from the server news.
	 */
	private void quit() {
		try {			
			socket.leaveGroup(group);
			if(isInGroup) {
				statusBar.setText("Disconnected");
			}
			received=false;
			isInGroup = false;
			socket.close();
		} catch (IOException e) {
			if(!statusBar.getText().contains("Dis") && !statusBar.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Error in the trying to leave group", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	public void startClient() throws IOException{
		DatagramPacket packet;
		while(isInGroup) {
			byte[] buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			if(!received) {
				received=true;
				statusBar.setText("Connected "+packet.getAddress().toString());				
			}
			updateNews(new String(packet.getData()));
		}
	}
	
	/**
	 * append to the news list the string news
	 * @param news - to update
	 */
	public void updateNews(String news) {
		newsVec.add(news);
		newsList.setListData(newsVec);
	}
	
	private class ActionLis implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().compareTo("Clear")==0) {
				newsList.setListData(newsVec=new Vector<String>());
			}else if(event.getActionCommand().compareTo("Join")==0) {
				if(received) {
					statusBar.setText("Already in the news group!");
				}else if(!isInGroup){
					new NewsListener().start();
				}
			}else if(event.getActionCommand().compareTo("Leave")==0) {
				if(received) {
					quit();
				}else {
					statusBar.setText("Can't quit,not belong to any group!");
				}	
			}
		}
	} 
	public static void main(String[] args) {
		NewsClient newsReport = new NewsClient();

	}

}
