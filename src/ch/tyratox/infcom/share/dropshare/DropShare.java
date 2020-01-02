package ch.tyratox.infcom.share.dropshare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;

import helper.*;


public class DropShare extends JFrame implements DropTargetListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3300794883253367135L;
	private static JPanel contentPane;
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String USER = System.getProperty("user.name").toLowerCase();
	static JLabel label;
	ArrayList<String> file_list = new ArrayList<String>();
	static MulticastSocket getSocket;
	static MulticastSocket sendSocket;
	static ServerSocket sSocket;
	static ArrayList<String> socketIps = new ArrayList<String>();
	static InetAddress group;
	@SuppressWarnings("unused")
	private static DropTarget dt;
	@SuppressWarnings("unused")
	private static int files_saved = 0;
	private static boolean animationLock = false;
	private static int port_tcp = 11111;
	private static int port_udp = 22222;
	static String ls = System.getProperty("line.separator");
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try{
						System.out.println("Starting Getting Multicast Socket....");
						group = InetAddress.getByName("224.2.2.3");
						getSocket = new MulticastSocket(port_udp);
						getSocket.joinGroup(group);
						System.out.println("Starting Sending Multicast Socket....");
						sendSocket = new MulticastSocket();
						
					}catch(BindException e){
						handleException(e);
						System.exit(0);
					}
					System.out.println("New Thread: Get Clients over UDP");
					new Thread(){
						public void run(){
							getMessages();
						}
					}.start();
					System.out.println("New Thread: Send IP over UDP");
					new Thread(){
						public void run(){
							sendIP();
						}
					}.start();
					System.out.println("New Thread: Accept all Clients");
					new Thread(){
						public void run(){
							try {
								sSocket = new ServerSocket(port_tcp);
								while(true){
									Socket x = sSocket.accept();
									getMessagesfromSocket(x);
								}
							} catch (IOException e) {
								handleException(e);
							}
						}
					}.start();
				     System.out.println("Creating Frame");
					DropShare frame = new DropShare();
					System.out.println("Set Frame Moveable");
					ComponentMover cm = new ComponentMover();
					cm.registerComponent(frame);
					System.out.println("Set Frame Visible");
					frame.setVisible(true);
				} catch (Exception e) {
					handleException(e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DropShare() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 325);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setAlwaysOnTop(true);
		
		
		label = new JLabel("");
		label.setIcon(new ImageIcon(DropShare.class.getResource("/res/GFX_main.png")));
		if (isWindows()) {
			label.setBounds(0, 0, 300, 300);
		} else if (isMac()) {
			label.setBounds(0, 25, 300, 300);
		} else if (isUnix()) {
			label.setBounds(0, 25, 300, 300);
		}
		dt = new DropTarget(label, this);
		
		final DropShareMenu dsm = new DropShareMenu(this);
		
		contentPane.add(label);
		
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		// Set undecorated
		setUndecorated(true);
		setBackground(new Color(255, 255, 255, 0));
	}
	public static boolean isWindows() {
		 
		return (OS.indexOf("win") >= 0);
 
	}
 
	public static boolean isMac() {
 
		return (OS.indexOf("mac") >= 0);
 
	}
 
	public static boolean isUnix() {
 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
 
	}
	public static void animate(final int percent){
		if(animationLock == false){
			animationLock = true;
//			new Thread(){
//				public void run(){
					String path = "/res/animation/progress/";
					String preSet = "GFX_anim";
					String preInt = "00";
					for(int i = 1;i<=percent;i++){
						if(i<10){
							preInt = "00";
						}else if(i<100){
							preInt = "0";
						}else{
							preInt = "";
						}
						System.out.println(path + preSet + preInt + i + ".png");
						label.setIcon(new ImageIcon(DropShare.class.getResource(path + preSet + preInt + i + ".png")));
						try {
							Thread.sleep(33);
						} catch (InterruptedException e) {
							handleException(e);
						}
					}
//				}
//			}.start();
			animationLock = false;
		}
	}
	public static void animate_green(){
		if(animationLock == false){
			animationLock = true;
//			new Thread(){
//				public void run(){
					String path = "/res/animation/green/";
					String preSet = "GFX_anim_green";
					String preInt = "00";
					for(int i = 1;i<=17;i++){
						if(i<10){
							preInt = "00";
						}else if(i<100){
							preInt = "0";
						}else{
							preInt = "";
						}
						System.out.println(path + preSet + preInt + i + ".png");
						label.setIcon(new ImageIcon(DropShare.class.getResource(path + preSet + preInt + i + ".png")));
						try {
							Thread.sleep(33);
						} catch (InterruptedException e) {
							handleException(e);
						}
					}
//				}
//			}.start();
			animationLock = false;
		}
	}
	public static void animate_red(){
		if(animationLock == false){
			animationLock = true;
//			new Thread(){
//				public void run(){
					String path = "/res/animation/red/";
					String preSet = "GFX_anim_red";
					String preInt = "00";
					for(int i = 1;i<=17;i++){
						if(i<10){
							preInt = "00";
						}else if(i<100){
							preInt = "0";
						}else{
							preInt = "";
						}
						System.out.println(path + preSet + preInt + i + ".png");
						label.setIcon(new ImageIcon(DropShare.class.getResource(path + preSet + preInt + i + ".png")));
						try {
							Thread.sleep(33);
						} catch (InterruptedException e) {
							handleException(e);
						}
					}
//				}
//			}.start();
			animationLock = false;
		}
	}
	public void dragEnter(DropTargetDragEvent dtde) {
		
	  }

	  public void dragExit(DropTargetEvent dte) {
		  label.setIcon(new ImageIcon(DropShare.class.getResource("/res/GFX_main.png")));
	  }

	  public void dragOver(DropTargetDragEvent dtde) {
		  
	  }

	  public void dropActionChanged(DropTargetDragEvent dtde) {
	    System.out.println("Drop Action Changed");
	  }

	  public void drop(final DropTargetDropEvent dtde) {
		  label.setIcon(new ImageIcon(DropShare.class.getResource("/res/GFX_main.png")));
	    try {
	    	Transferable tr = dtde.getTransferable();
	        DataFlavor[] flavors = tr.getTransferDataFlavors();
	        for (int i = 0; i < flavors.length; i++) {
	          if (flavors[i].isFlavorJavaFileListType()) {
	            dtde.acceptDrop(DnDConstants.ACTION_COPY);
	            @SuppressWarnings("rawtypes")
				List list = (List) tr.getTransferData(flavors[i]);
	            for (int j = 0; j < list.size(); j++) {
	             String file = list.get(j).toString();
	             System.out.println(file);
	             file_list.add(file);
	            }
	            System.out.println("You are sending " + file_list.size() + " Files!");
	            System.out.println("Sending Files..");
	            System.out.println("Size of Ips" + socketIps.size());
	            System.out.println("Size of file list:" + file_list.size());
	            new Thread(){
	            	public void run(){
	            		try{
				            for(int j = 0;j<socketIps.size();j++){
				            	for(int y = 0;y<file_list.size();y++){
				            		System.out.println("Connecting to " + socketIps.get(j) );
				            		Socket ss = new Socket(socketIps.get(j), port_tcp);
				            		System.out.println("SENDING " + file_list.get(y) + " to " + socketIps.get(j) + ":" + port_tcp);
				            		sendFileToClient(ss, file_list.get(y));
				            		ss.close();
				            	}
				            }
				            file_list.clear();
	            		}catch(Exception e){
	            			handleException(e);
	            			file_list.clear();
	            		}
	            	}
	            }.start();
	            dtde.dropComplete(true);
	            return;
	          }
	        }
	        System.out.println("Drop failed: " + dtde);
	        dtde.rejectDrop();
	    } catch (Exception e) {
	      handleException(e);
	      try{
	      dtde.rejectDrop();
	      }catch(Exception e1){
	    	  handleException(e1);
	      }
	    }
	  }
	  public static void saveToDownloads(byte[] file, String filename) throws IOException{
		  System.out.println("Saving File....");
		  String path = null;
		  String filename_copy = filename;
		  if(isWindows()){
			  path = "C:/Users/" + USER + "/Downloads/";
		  }else if(isMac()){
			  path = "/Users/" + USER + "/Downloads/";
		  }else if(isUnix()){
			  path = "/home/" + USER + "/Downloads/"; 
		  }
		  
		  File exist = new File(path + filename);
		  int i = 0;
		  while(exist.exists()){
			  filename = FilenameUtils.getBaseName(filename_copy) + "_" + i + "." + FilenameUtils.getExtension(filename_copy);
			  exist = new File(path + filename);
			  i++;
		  }
			  FileOutputStream fos = new FileOutputStream(path + filename);
			  fos.write(file);
			  fos.close();
			  animate_green();
	  }
	  public static void getMessages(){
		  while(true){
			try {
				  byte buf[] = new byte[1024 * 64];
				  DatagramPacket pack = new DatagramPacket(buf, buf.length);
				  System.out.println("Get Pack....");
				  getSocket.receive(pack);
				  System.out.println("Checking bytes");
				  
				  ArrayList<String> ips = getip();
				  
				  String ip = pack.getAddress().toString().replace("/", "");
				  String[] ip_ = ip.split(":");
				  boolean me = false;
				  
				  for(int i = 0;i<ips.size();i++){
					  if(ip_[0].equalsIgnoreCase(ips.get(i))){
						  me = true;
					  }
				  }
				  if(me == false){
					  String iip = ip_[0];
						  if(socketIps.indexOf(iip) == -1){
							  socketIps.add(iip);
							  System.out.println("Received data from: " + pack.getAddress().toString() +
									    ":" + pack.getPort() + " with length: " +
									    pack.getLength());
							  
						  }
				  }
			} catch (IOException e) {
				handleException(e);
			}
		  }
	  }
	  public static void sendIP(){
		  while(true){
			  byte[] y = "Yolo".getBytes();
				  DatagramPacket pack = new DatagramPacket(y, y.length, group, port_udp);
		          try {
					sendSocket.send(pack);
					System.out.println("Sent: " + new String(y));
				} catch (IOException e) {
					e.printStackTrace();
				animate_red();
				} 
		          try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
				animate_red();
					}
		  }
	  }
	  public static ArrayList<String> getip(){
			ArrayList<String> ip = new ArrayList<String>();
			String ds = null;
			try {
				  InetAddress inet = InetAddress.getLocalHost();
				  InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
				  if (ips  != null ) {
				    for (int i = 0; i < ips.length; i++) {
				      String ip_s = ips[i].toString();
				      String[] sd = ip_s.split("/");
				      ds = sd[1];
				      ip.add(ds);
				    }
				  }
				} catch (UnknownHostException e) {
					handleException(e);
				}
			
			return ip;
		}

	  public static void sendFileToClient(Socket socket, String fileToSend) throws IOException{
			System.out.println("Getting bytes....");
			byte[] fileByte = Files.readAllBytes(Paths.get(fileToSend));
			File file = new File(fileToSend);
			String filename = file.getName();
			sendBytes(socket, fileByte, 0, fileByte.length, filename);
		}
	  
	  public static void sendBytes(Socket socket, byte[] myByteArray, int start, int len, String filename) throws IOException {
		    if (len < 0)
		        throw new IllegalArgumentException("Negative length not allowed");
		    if (start < 0 || start >= myByteArray.length)
		        throw new IndexOutOfBoundsException("Out of bounds: " + start);
		    // Other checks if needed.

		    // May be better to save the streams in the support class;
		    // just like the socket variable.
		    OutputStream out = socket.getOutputStream(); 
		    DataOutputStream dos = new DataOutputStream(out);
		    dos.writeUTF(filename);
		    dos.writeInt(len);
		    if (len > 0) {
		        dos.write(myByteArray, start, len);
		    }
		}
	  public static void getMessagesfromSocket(final Socket socket) throws IOException{
		  new Thread(){
			  public void run(){
					  while(true){
						  try{
						  InputStream in = socket.getInputStream();
						  DataInputStream dis = new DataInputStream(in);
						  String filename = dis.readUTF();
						  int len = dis.readInt();
						  if(filename != null && len != 0){
							  byte[] data = new byte[len];
							  if(JOptionPane.showConfirmDialog(label, "Do you want to save " + filename + " ?") == 0){
								  if (len > 0) {
									  dis.readFully(data);
								  }
								  saveToDownloads(data, filename);
							  }
						  }
						  }catch(Exception e){
							  handleException(e);
							  break;
						  }
					  }
		  }
		  }.start();
		}
	  public static void handleException(Exception e){
		  e.printStackTrace();
		  showMessage(e.toString());
	  }
	  public static void showMessage(String message){
		  JOptionPane.showMessageDialog(label, message);
	  }
	  
	  public void enableMulticastOSX(){
			System.out.println("Requesting Admin Password on Mac beacuse of Multicasting");
			String[] command = {
			        "osascript",
			        "-e",
			        "do shell script \"route -nv add -net 224.2.2.3 -interface en0\" with administrator privileges" };
			Runtime runtime = Runtime.getRuntime();
			try {
			    runtime.exec(command);
			} catch (IOException e) {
			    handleException(e);
			}
	  }
	  
}
