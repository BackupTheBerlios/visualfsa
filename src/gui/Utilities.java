/*
  Copyright 2005 Mathias Lichtner
  mlic at informatik.uni-kiel.de
 
  This file is part of visualfsa.
 
  visualfsa is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation;
  either version 2 of the License, or (at your option) any later version.
 
  visualfsa is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.
 
  You should have received a copy of the GNU General Public License along with visualfsa;
  if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package gui;

import java.awt.Component;
import javax.swing.JScrollPane;

import datastructs.FSA;

public class Utilities {
    
    /*
        taken from swp project at the unversity kiel
        thx to ssm :)
     */
    
    public static void fitToWindow(FSA aut, AutWindow autWin) {
        int xdim, ydim, vpWidth, vpHeight;
        int leftmost    = 0;
        int rightmost   = 0;
        int topmost     = 0;
        int bottommost  = 0;
        int marginx     = 0;
        int marginy     = 0;
        float fitScaleX = 1;
        float fitScaleY = 1;
        Component[] comps;
        int compCount;
        vpWidth  = (int)autWin.getWidth() - 40;
        vpHeight = (int)autWin.getHeight() - 40;
        leftmost = vpWidth;
        topmost  = vpHeight;
        
        comps = autWin.getComponents();
        compCount = autWin.getComponentCount();
        
        for (int ip=0;ip<compCount;ip++) {
            if (comps[ip].getLocation().x < leftmost)   { leftmost   = comps[ip].getLocation().x; }
            if (comps[ip].getLocation().x > rightmost)  { rightmost  = comps[ip].getLocation().x; }
            if (comps[ip].getLocation().y < topmost)    { topmost    = comps[ip].getLocation().y; }
            if (comps[ip].getLocation().y > bottommost) { bottommost = comps[ip].getLocation().y; }
        }
        
        xdim = rightmost - leftmost;
        ydim = bottommost - topmost;
        marginx = -leftmost;
        marginy = -topmost;
        fitScaleX = (float)vpWidth / (float)xdim;
        fitScaleY = (float)vpHeight / (float)ydim;
        
        for (int ip=0;ip<compCount;ip++) {
            JState toFit = (JState)comps[ip];
            toFit.setBounds((int)((toFit.getLocation().x + marginx) * fitScaleX),
                    (int)((toFit.getLocation().y + marginy) * fitScaleY),
                    autWin.STATE_SIZE, autWin.STATE_SIZE);
        }
        
        
    }
    
    public static void processGrid(AutWindow autWin) {
        Component[] comps;
        boolean grid = true;
        
        if ( !grid ) { return; }
        
        comps = autWin.getComponentsInLayer(autWin.STATE_LAYER);
        
        int compCount = autWin.getComponentCountInLayer(autWin.STATE_LAYER);
        
        for (int ip=0;ip<compCount;ip++) {
    
            JState toFit = (JState)comps[ip];
            int x = toFit.getLocation().x;
            int y = toFit.getLocation().y;
            toFit.setBounds((int)(x - ( x % autWin.GRID_SIZE)),
                    (int)(y - ( y % autWin.GRID_SIZE)),
                    autWin.STATE_SIZE, autWin.STATE_SIZE);
        }
        
        autWin.repaint();
    }
    
    
    
    
    
}
