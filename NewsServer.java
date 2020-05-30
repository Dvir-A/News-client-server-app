package mmn16_2;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.sql.Time;

import javax.swing.*;

public class NewsServer extends JFrame{
	private JTextField _newsField;
	private DatagramSocket _dSocket;
	private DatagramPacket packet;
	private InetAddress address;
	private final String NEWS_GROUP= "230.0.0.1";
	private final int PORT = 7777;
	
	public NewsServer() {
		super("News Server");
		try {
			this._dSocket = new DatagramSocket(4445);
			address = InetAddress.getByName(NEWS_GROUP);
		} catch (SocketException | UnknownHostException socketException) {
			socketException.printStackTrace();
			System.exit(1);
		}
		setSize(new Dimension(400,100));
		
		_newsField = new JTextField();
		_newsField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String newsStr = event.getActionCommand().trim();
				if(newsStr.isEmpty()) {
					return;
				}
				new SendingThread(newsStr,new Time(System.currentTimeMillis())).start();
				_newsField.setText("");
			}
		});
		this.setLayout(new GridLayout(2, 1));
		add(new JLabel("Enter news:"));
		add(_newsField);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	


	private class SendingThread extends Thread{
		private News news;
		public SendingThread(String newsStr,Time currTime) {
			super("Sending Thread");
			news = new News(currTime, newsStr);
		}
		public void run() {
			try {
				byte[] buf = news.toString().getBytes(); 
				packet = new DatagramPacket(buf, buf.length, address, PORT);
				_dSocket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		NewsServer newsS=new NewsServer();
		newsS.setVisible(true);
	}

}
