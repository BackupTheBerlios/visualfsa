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

import java.awt.Color;
import java.util.HashMap;

public class AppOptions {
    
    public static enum OPTION_TYPE {
        COLOR_OPTION,
        BOOL_OPTION,
        UNDEFINED_OPTION
    }
    
    private HashMap<String, SingleOption> options;
    // sollen nicht vorhandene Schlüssel mit ihren Default-Wert eingetragen werden?
    private boolean putDefault;
    
    public AppOptions(boolean putDefPolicy) {
        options = new HashMap<String, SingleOption>();
        putDefault = putDefPolicy;
    }
    
    public void putOption(String key, SingleOption opt) {
        options.put(key, opt);
    }
    
    public void setColorValueForKey(String key, Color val) {
        SingleOption o = new SingleOption(OPTION_TYPE.COLOR_OPTION, val.getRGB());
        options.put(key, o);
    }
    
    public Color getColorValueForKey(String key, Color def) {
        if (options.containsKey(key)) {
            
            Color res;
            SingleOption opt;
            
            opt = options.get(key);
            
            if (opt.getType()!=OPTION_TYPE.COLOR_OPTION)
                return def;
            
            return (new Color(opt.getValue()));
        } else {
            if (putDefault) {
                SingleOption newEntry;
                newEntry = new SingleOption(OPTION_TYPE.COLOR_OPTION,
                        def.getRGB());
                options.put(key, newEntry);
            }
            return def;
        }
    }
    
    
    public void setBoolValueForKey(String key, boolean val) {
        SingleOption o = new SingleOption(OPTION_TYPE.BOOL_OPTION, val ? 1 : 0);
        options.put(key,o);
    }
    
    
    public boolean getBoolValueForKey(String key, boolean def) {
        if (options.containsKey(key)) {
            
            Color res;
            SingleOption opt;
            
            opt = options.get(key);
            
            if (opt.getType()!=OPTION_TYPE.BOOL_OPTION)
                return def;
            
            return (opt.getValue()!=0);
            
        } else {
            if (putDefault) {
                SingleOption newEntry = new SingleOption(OPTION_TYPE.BOOL_OPTION,
                        def ? 1 : 0);
                options.put(key, newEntry);
            }
            return def;
        }
    }
    
    // deep-copy
    public AppOptions copy() {
        SingleOption opt;
        
        AppOptions res = new AppOptions(true);
        
        for ( String key : this.options.keySet() ) {
            
            opt = options.get(key);
            
            res.putOption(key, (SingleOption)opt.clone());
        }
        
        return res;
    }
    
    public static OPTION_TYPE mapTypeString(String aType) {
        aType = aType.toUpperCase();
        
        if (aType.equals("COLOR")) {
            return OPTION_TYPE.COLOR_OPTION;
        } else if (aType.equals("BOOL")) {
            return OPTION_TYPE.BOOL_OPTION;
        }
        
        return OPTION_TYPE.UNDEFINED_OPTION;
    }
    
    private String getTypeString(OPTION_TYPE t) {
        switch (t) {
            
            case COLOR_OPTION:
                return "COLOR";
            case BOOL_OPTION:
                return "BOOL";
                
            default:
                return "UNDEFINED";
        }
    }
    
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        SingleOption opt;
        
        for (String key : options.keySet() ) {
            opt = options.get(key);
            res.append(getTypeString(opt.getType()));
            res.append(":");
            res.append(key);
            res.append(":");
            res.append(((Integer)opt.getValue()).toString());
            res.append("\n");
        }
        
        return res.toString();
    }
    
}

