package ch.tyratox.infcom.share.dropshare;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class DropShareMenu extends JPopupMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1212255632423371545L;

	public DropShareMenu(final DropShare ds){
		JMenuItem multicast = new JMenuItem("Enable Multicasting");
		multicast.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ds.enableMulticastOSX();
			}
		});
		this.add(multicast);
	}

}
