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

package gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.LinkedList;

public class PyTreeDialog extends JFrame {
    
    
    private int MAX_STEP = 12;
    
    /* innere klasse, extra für das baumfoo n neues classfile... nee */
    
    private class PyTreePanel extends JComponent {
        
        private int RECT_SIZE = 60;
        private int RECT_HALFSIZE = 30;
        private int currentStep;
        private double wDis, hFac;
        private LinkedList<Polygon> polys;
        
        public PyTreePanel(int _s) {
            super();
            currentStep = _s;
            wDis = 0.5f;
            hFac = 1.5f;
            polys = new LinkedList<Polygon>();
        }
        
        public void setWidthDistribution(int val) {
            wDis = (double)val/100f;
        }
        
        public void setHeightFactor(int val) {
            hFac = 1f + ( (double)val/100f );
        }
        
        public int getStep() {
            return currentStep;
        }
        
        public void setStep(int _s) {
            if (_s<=0) return;
            currentStep = Math.min(_s, MAX_STEP);
        }
        
        /*
            Einführung in die Informatik, H.-P. Gumm, M.Sommer, 5. Auflage
            Seite 640 :)
         */
        private void calcPoly(int step, Point p1, Point p2)   {
            int dx = p2.x - p1.x;
            int dy = p1.y - p2.y;
            int hy = dx*dx+dy*dy;
            
            Point p3 = new Point(p2.x - dy, p2.y - dx);
            Point p4 = new Point(p1.x - dy, p1.y - dx);
            
            Polygon p = new Polygon();
            
            p.addPoint(p1.x, p1.y);
            p.addPoint(p2.x, p2.y);
            p.addPoint(p3.x, p3.y);
            p.addPoint(p4.x, p4.y);
            
            polys.add(p);
            
            int newX, newY;
            
            newX = (int) ( (1f-wDis)*p1.x + wDis*p2.x - hFac*dy);
            newY = (int) ( (1f-wDis)*p1.y + wDis*p2.y - hFac*dx);
            
            Point pNew = new Point(newX, newY);
            
            if (step-- >= 0) {
                calcPoly(step,p4, pNew);
                calcPoly(step, pNew, p3);
            }
            
        }
        
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            int halfW = getWidth()/2;
            int height = getHeight() - 1;
            
            Point px = new Point(halfW - RECT_HALFSIZE, height - RECT_SIZE);
            Point py = new Point(halfW + RECT_HALFSIZE, height - RECT_SIZE);
            
            polys.clear();
            
            long consTime = System.currentTimeMillis();
            
            calcPoly(currentStep, px, py);
            
            consTime = System.currentTimeMillis() - consTime;
            
            // new cool for loop iff Java 5 :)
            for ( Polygon poly : polys) {
                g.drawPolygon(poly);
            }
            
            g.setColor(Color.RED);
            g.drawString("calculated "+polys.size()+" polygons in "+consTime+ "ms",20,height-20);
            
        }
        
    }
    
    
    
    public void run() {
        
        JPanel buttonPanel;
        final PyTreePanel myTree;
        
        JButton moreDepth, lessDepth, doneButton;
        final JSlider heightSlide, centerSlide;
        
        buttonPanel = new JPanel();
        
        buttonPanel.setLayout(new GridLayout(1,3));
        
        moreDepth = new JButton("Step in");
        lessDepth = new JButton("Step out");
        doneButton = new JButton("Quit");
        
        buttonPanel.add(moreDepth);
        buttonPanel.add(lessDepth);
        buttonPanel.add(doneButton);
        buttonPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        
        myTree = new PyTreePanel(4);
        
        myTree.setPreferredSize(new Dimension(640,480));
        
        
        moreDepth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTree.setStep(myTree.getStep()+1);
                myTree.repaint();
            }
        });
        
        lessDepth.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myTree.setStep(myTree.getStep()-1);
                myTree.repaint();
            }
        });
        
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.runFinalization();
            }
        });
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        
        getContentPane().add(myTree, BorderLayout.CENTER);
        
        heightSlide = new JSlider(JSlider.VERTICAL, 0, 100, 50);
        centerSlide = new JSlider(JSlider.HORIZONTAL,0,100,50);
        
        getContentPane().add(heightSlide, BorderLayout.EAST);
        getContentPane().add(centerSlide, BorderLayout.SOUTH);
        
        centerSlide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                myTree.setWidthDistribution(centerSlide.getValue());
                myTree.repaint();
            }
        });
        
        heightSlide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                myTree.setHeightFactor(heightSlide.getValue());
                myTree.repaint();
            }
        });
        
        setLocation(40,40);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        pack();
        setVisible(true);
    }
    
    
}
