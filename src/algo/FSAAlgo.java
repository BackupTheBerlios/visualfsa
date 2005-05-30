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

package algo;

import datastructs.FSA;
import datastructs.Transition;
import datastructs.IntegerSet;

import java.util.*;

public class FSAAlgo  {
    
    public FSAAlgo() {
    }
    

    /*
        guessLang versucht die Sprache die ein Automat erkennt zu identifzieren,
        dabei werden aus dem Eingabealphabet alle W�rter gebildet deren L�nge
        kleiner 'step' ist (da die Sprachen potentiell unendlich sind (sein k�nnen))
        Mit jedem erzeugten Wort wird aut.accepts aufgerufen
    */
    public static synchronized HashSet<String> guessLang(FSA aut, int step) {
        Vector<Character> alpha;

        alpha = aut.getAlphabet();
        
        Vector<String> words;
        Vector<String> newWords;
        HashSet<String> lang;
        
        words = new Vector<String>();
        String newWord;
        lang = new HashSet<String>();
        String curr;
        
        boolean autType = aut.isDeterministic();
        
        /*
            words ist die Menge aller Strings aus denen wir noch neue
            Kombinationen bilden k�nnen, anfangs wird jeder einzelne Buchstabe
            des Eingabealphabets dort hineingetan
        */
        for ( Iterator<Character> it = alpha.iterator(); it.hasNext(); ) {
            words.add(it.next().toString());
        }
        
        for (int i = 0 ; i < step ; i++ ) {
            
            /* teste die aktuelle Wortliste */
            
            for ( Iterator<String> strIt = words.iterator(); strIt.hasNext(); ) {
                curr = strIt.next();
                
                /* da der Automat w�hrend des Sprachtests nicht ver�ndert
                   wird kann der in FSA.java eingebaute Determinismustest
                   �bersprungen werden 
                */
                
                if (aut.accepts(curr, true, autType)) {
                    lang.add(curr);
                }
            }
            
            newWords = new Vector<String>();
            
            /* hinter jedes Wort der aktuellen Wortliste wird jeder Buchstabe
               des Eingabealphabets geh�ngt, die neu enstandenen W�rter ersetzen
               die alten wodurch nie alle W�rter im Speicher gehalten werden m�ssen
            */
            
            for ( Iterator<String> strIt = words.iterator(); strIt.hasNext(); ) {
                    
                newWord = strIt.next();
                
                
                    for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                        newWords.add(newWord+alphaIt.next().toString());
            
                }
                
            }

            System.out.println(newWords.size());
            
            words.clear();
            words.addAll(newWords);

        }
        
        return lang;
    }
    
    
    
}
