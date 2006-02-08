/*
  Copyright 2005, 2006 Mathias Lichtner
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


package datastructs;

import java.util.Vector;
import java.util.Iterator;

public class WordTree {
    
    private WordTreeNode root;
    
    public WordTree(Vector<Character> alpha, int depth) {
        
        int alphaSize = alpha.size();
        
        long totalMem, currentMem;
        double usage;
        
        totalMem = Runtime.getRuntime().totalMemory();
        
        Vector<WordTreeNode> newChilds = new Vector<WordTreeNode>();
        Vector<WordTreeNode> parents, newParents;
        
        // erzeuge zunächst das Wurzelelement, hänge dort
        // alle Buchstaben des Eingabealphabets ran
        
        for ( int i = 0 ; i < alphaSize ; i++ ) {
            WordTreeNode newNode = new WordTreeNode(alpha.get(i));
            newChilds.add(newNode);
        }
        
        root = new WordTreeNode(newChilds);
        
        // die weiterzubearbeitenden Knoten sind die Kinder der Wurzel
        
        parents = newChilds;
        
        for ( int i = 1 ; i < (depth-1) ; i++ ) {
            
            newParents = new Vector<WordTreeNode>();
            
            // für jeden Knoten der derzeitigen parents-Menge...
            for ( Iterator<WordTreeNode> it = parents.iterator(); it.hasNext(); ) {
                
                // ...füge wiederrum als Kinder alle Elemente des
                // Eingabealphabets ein
                
                newChilds = new Vector<WordTreeNode>();
                
                for ( int j = 0 ; j < alphaSize ; j++ ) {
                    WordTreeNode newNode = new WordTreeNode(alpha.get(j));
                    newChilds.add(newNode);
                }
                
                // setze die Kinder des aktuellen Elternknotens
                it.next().setChilds(newChilds);
                
                // diese Kinderknoten sind nun Teil der neuen Elternknoten
                newParents.addAll(newChilds);
                
            }
            
            parents = newParents;
            
            // der erstellte Baum ist vom Speicherverbauch her der Knackpunkt
            // bei der Spracherkennung. Deshalb wird ja jedem Teildurchlauf
            // der aktuelle Speicherverbrauch abgefragt. Überschreitet dieser
            // (empirisch) festgestellte Höchstwerte bricht die Baumerstellung
            // ab. Klar ist, auch der halb-fertige Baum erzeugt immer noch
            // eine echte - vor allem aber sinvolle - Teilmenge von "A*"
            currentMem = Runtime.getRuntime().freeMemory();
            usage = (double)currentMem/(double)totalMem;
            
            if (usage>0.90) {
                break;
            }
        }
        
    }
    
    public void setRootData(char _d) {
        root.setData(_d);
    }
    
    
    private StringBuffer nextWordRec(StringBuffer curr, WordTreeNode root, Vector<WordTreeNode> childs) {
        WordTreeNode currNode;
        
        if (root.isVisited()) return null;
        
        // füge den Buchstaben der Wurzel ein
        curr.append(root.getData());
        
        // haben wir keine Nachfolger mehr?
        if (childs==null) {
            root.setVisited(true);
            return curr;
        }
        
        // durchsuche die Kinder nach unbesuchten Knoten
        for ( Iterator<WordTreeNode> it = childs.iterator(); it.hasNext(); ) {
            currNode = it.next();
            
            // wurde ein unbesuchter Knoten gefunden, rufe rekursiv auf
            // mit diesem Knoten als neue Wurzel
            if (!currNode.isVisited()) {
                return nextWordRec(curr, currNode, currNode.getChilds());
            }
        }
        
        // alle unsere Nachfolger waren schon besucht...
        root.setVisited(true);
        
        return curr;
    }
    
    public String nextWord() {
        StringBuffer res = nextWordRec(new StringBuffer(""), root, root.getChilds());
        
        if (res==null)
            return null;
        else
            return res.toString();
    }
    
    // setzt alle Knoten als unmakiert
    public void resetVisited() {
        resetVisitedRec(root);
    }
    
    
    private void resetVisitedRec(WordTreeNode currentRoot) {
        Vector<WordTreeNode> childs;
        
        currentRoot.setVisited(false);
        
        childs = currentRoot.getChilds();
        
        if (childs==null) return;
        
        for ( Iterator<WordTreeNode> it = childs.iterator(); it.hasNext(); ) {
            resetVisitedRec(it.next());
        }
        
        return;
    }
    
    
    
    
}
