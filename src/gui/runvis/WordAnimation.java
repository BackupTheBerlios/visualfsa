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


package gui.runvis;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JComponent;


public class WordAnimation extends JComponent {
    
    private String word;
    private int step;
    private boolean isAnimating;
    
    private final int TEXT_Y = 25;
    
    public WordAnimation(String _word) {
        word = _word;
        step = -1;
    }
    
    public void setWord(String _word) {
        word = _word;
        repaint();
    }
    
    public String getWord() {
        return word;
    }
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font myFont, oldFont;
        
        oldFont = g.getFont();
        
        myFont = new Font(oldFont.getFontName(), Font.BOLD, 18);
        
        g.setFont(myFont);
        
        // zeichne den String, umrande den aktuellen Buchstaben
        // mit einem roten Rechteck
        if (isAnimating) {
            String rearString, currChar;
            
            currChar = ((Character)word.charAt(step)).toString();
            rearString = word.substring(step, word.length());
            
            g.setColor(Color.RED);
            g.drawString(currChar, 100, TEXT_Y);
            g.setColor(Color.BLACK);
            
            g.drawString(rearString, 200, TEXT_Y);
         } else {
            g.setColor(Color.BLACK);
            g.drawString(word, 200, TEXT_Y);
         }
        
        g.setFont(oldFont);
    }
    
    public void setAnimating(boolean _isAnimating) {
        isAnimating = _isAnimating;
        if (!isAnimating)
            step = -1;
    }
    
    public int getMaxStep() {
        return word.length();
    }
    
    public void animateStep() {
        step++;
        repaint();
    }
    
}
