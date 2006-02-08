
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

import datastructs.AppOptions.OPTION_TYPE;

public class SingleOption {
    
    private OPTION_TYPE optionType;
    private int value;
    
    public SingleOption(OPTION_TYPE type, int val) {
        optionType = type;
        value = val;
    }
    
    public OPTION_TYPE getType() {
        return optionType;
    }
    
    public int getValue() {
        return value;
    }
    
    public Object clone() {
        return (new SingleOption(optionType, value));
    }
    
}