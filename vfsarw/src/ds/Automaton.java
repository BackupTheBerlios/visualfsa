/*
  Copyright (C) 2007 Mathias Lichtner
  mlic at informatik dot uni-kiel dot de
 
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

/*
 *  Automaton.java
 *
 *  Interface fuer einen endlichen Automaten
 *
 */

package ds;

public interface Automaton {
    
    // Ein Automat sollte in der Lage sein, Auskunft darueber zu erteilen
    // ob er (nicht-)/deterministisch ist
    boolean isDeterministic();
    
    // fuegt eine Transition in den Automaten ein, true zeigt an das der
    // Vorgang erfolgreich war
    boolean addTransition(Transition t);
    
}
