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

public class WordTreeNode {
    
    private Vector<WordTreeNode> childs;
    private char data;
    private boolean visited;
    
    public WordTreeNode(Vector<WordTreeNode> _childs, char _data) {
        childs = _childs;
        data = _data;
    }

    public WordTreeNode(Vector<WordTreeNode> _childs) {
        childs = _childs;
    }
    
    public WordTreeNode(char _data) {
        childs = null;
        data = _data;
    }

    
    public void setData(char _data) {
        data = _data;
    }
    
    public char getData() {
        return data;
    }

    public void setChilds(Vector<WordTreeNode> _childs) {
        childs = _childs;
    }
    
    public Vector<WordTreeNode> getChilds() {
        return childs;
    }
    
    public void setVisited(boolean _v) {
        visited = _v;
    }
    
    public boolean isVisited() {
        return visited;
    }
    
}
