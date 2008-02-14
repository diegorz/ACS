/*
 * ALMA - Atacama Large Millimiter Array (c) European Southern Observatory, 2007
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package alma.acsplugins.alarmsystem.gui.statusline;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * The status line showing info to the user
 * 
 * @author acaproni
 *
 */
public class StatusLine extends JPanel {

	/**
	 * Constructor
	 */
	public StatusLine() {
		initialize();
	}
	
	/**
	 * Init the status line
	 */
	private void initialize() {
		setBorder(BorderFactory.createLoweredBevelBorder());
	}
}
