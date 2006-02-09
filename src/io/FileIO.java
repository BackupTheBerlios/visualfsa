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


/*
 
 .fsa datei format
 
 Es steht _immer_ mindestens ein Automat in der Liste
 Jeder Automat hat mindestens einen Zustand
 
 Format:
 
 NEXTAUT
 Automatname
 Zustand#1,istStartZustand,istEndzustand,Position.x,Position.y,AnzahlTransitionen
 [Zielzustand,Transitionzeichen]  // transition bezogen auf letzten zustand
 .
 .
 .
 Zustand#2,......
 [NEXTAUT || EOF]
 
 */


package io;

import datastructs.FSA;
import datastructs.Transition;
import datastructs.AppOptions;
import datastructs.SingleOption;
import java.awt.Color;
import java.awt.Point;
import java.awt.Component;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFileChooser;

public class FileIO {
    
    private final static String optionsFile = "vfsa.ini";
    
    private class ParsedState {
        
        public ParsedState() {
            position = new Point();
        }
        
        public int num;
        public boolean isStartState, isFinalState;
        public int transCount;
        public Point position;
        
    }
    
    private class ParsedTransition {
        public int endState;
        public char transChar;
    }
    
    
    
    /* eine Liste von Automatenobjekten wird in die Datei 'filename'
       geschrieben
     */
    public static void fsaListToFile(LinkedList<FSA> fsaList, String filename)
    throws FileNotFoundException {
        
        PrintWriter writer;
        FSA currentAut;
        Set<Integer> stateSet;
        int currentState;
        LinkedList<Transition> currentTL;
        Transition currentTrans;
        
        writer = new PrintWriter(filename);
        
        for ( Iterator<FSA> it = fsaList.iterator(); it.hasNext();) {
            
            currentAut = it.next();
            
            // Schlüsselwort NEXTAUT zeigt an das nun ein Automat kommt
            writer.println("NEXTAUT");
            
            // Name des Automaten
            writer.println(currentAut.getName());
            
            // nun alle Zustände in der Form
            // Nr, istStart, istEnde, xpos, ypos
            // Transitionen
            
            stateSet = currentAut.getStates();
            
            for ( Iterator<Integer> setIt = stateSet.iterator(); setIt.hasNext();) {
                
                currentState = setIt.next().intValue();;
                
                writer.print(currentState+",");
                writer.print(currentAut.isStartState(currentState)+",");
                writer.print(currentAut.isFinalState(currentState)+",");
                
                // Position
                writer.print(currentAut.getPosition(currentState).x+",");
                writer.print(currentAut.getPosition(currentState).y+",");
                
                currentTL = currentAut.getStateTransitions(currentState);
                
                if (currentTL==null) {
                    writer.println(0);
                } else {
                    
                    writer.println(currentTL.size());
                    
                    for ( Iterator<Transition> transIt = currentTL.iterator();
                    transIt.hasNext(); ) {
                        
                        currentTrans = transIt.next();
                        
                        writer.print(currentTrans.getEndState()+",");
                        writer.println(currentTrans.getChar());
                        
                    }
                    
                    
                }
                
            }
            
        }
        
        writer.println("EOF");
        writer.close();
        
    }
    
    public LinkedList fileToFsaList(String filename)
    throws Exception {
        
        LinkedList<FSA> result;
        boolean done = false;
        String currentLine;
        FSA currentAut;
        ParsedState currentState;
        ParsedTransition currentTrans;
        
        result = new LinkedList<FSA>();
        
        BufferedReader input = new BufferedReader(new FileReader(filename));
        
        // verbrauche das erste 'NEXTAUT'
        currentLine = input.readLine();
        
        while (!done) {
            
            currentAut = new FSA();
            
            // Am Ende angelangt?
            if (currentLine.equals("EOF")) {
                done = true;
                continue;
            }
            
            // suche das Schlüsselwort NEXTAUT
            if (!currentLine.equals("NEXTAUT"))
                throw new Exception("expected NEXTAUT");
            
            currentAut.setName(input.readLine());
            
            // lese und parse die Zustandszeile
            // es ist vom Programm her sichergestellt das jeder
            // Automat mindestens einen Zustand hat
            currentLine = input.readLine();
            
            do {
                
                currentState = parseState(currentLine);
                
                currentAut.setPosition(currentState.num, currentState.position);
                currentAut.setStartFlag(currentState.num, currentState.isStartState);
                currentAut.setFinalFlag(currentState.num, currentState.isFinalState);
                
                // hat der Zustand keine Transitionen wird diese Schleife
                // richtigerweise nicht durchlaufen
                for (int i = 0 ; i < currentState.transCount ; i++ ) {
                    currentTrans = parseTrans(input.readLine());
                    currentAut.addTransition(currentState.num, currentTrans.endState,
                            currentTrans.transChar);
                }
                currentLine = input.readLine();
                
            } while (!currentLine.equals("NEXTAUT") &&
                    !currentLine.equals("EOF"));
            
            // automat fertig, rein in die dicke Liste ;-)
            result.add(currentAut);
            
        }
        
        input.close();
        
        
        return result;
    }
    
    
    private ParsedTransition parseTrans(String line)
    throws Exception {
        
        StringTokenizer token = new StringTokenizer(line, ",");
        
        ParsedTransition result = new ParsedTransition();
        
        result.endState = Integer.parseInt(token.nextToken());
        result.transChar = token.nextToken().charAt(0);
        
        return result;
    }
    
    
    private ParsedState parseState(String line)
    throws Exception {
        
        StringTokenizer token =  new StringTokenizer(line,",");
        
        ParsedState newState = new ParsedState();
        
        newState.num = Integer.parseInt(token.nextToken());
        newState.isStartState = Boolean.parseBoolean(token.nextToken());
        newState.isFinalState = Boolean.parseBoolean(token.nextToken());
        newState.position.x = Integer.parseInt(token.nextToken());
        newState.position.y = Integer.parseInt(token.nextToken());
        newState.transCount = Integer.parseInt(token.nextToken());
        
        return newState;
    }
    
    
    
    public static String getSaveFilename(Component owner, String initial) {
        JFileChooser fileDlg;
        
        fileDlg = new JFileChooser();
        fileDlg.setDialogTitle("Save File");
        fileDlg.setSelectedFile(new File(initial));
        
        if (fileDlg.showSaveDialog(owner)==JFileChooser.APPROVE_OPTION) {
            return fileDlg.getSelectedFile().toString();
        } else {
            return null;
        }
    }
    
    public static String getOpenFilename(Component owner) {
        JFileChooser fileDlg;
        
        fileDlg = new JFileChooser();
        fileDlg.setDialogTitle("Open File");
        fileDlg.setMultiSelectionEnabled(false);
        if (fileDlg.showOpenDialog(owner)==JFileChooser.APPROVE_OPTION) {
            return fileDlg.getSelectedFile().toString();
        } else {
            return null;
        }
    }
    
    
    /*
     
     Optionsdatei Format:
     
     OptionsTyp:Schluessel:Wert
     .
     .
     
     zb.
     
     Color:LINE_COLOR:WeirdIntVal
     Bool:BLUBB_OPTION:WeirdIntVal
     
     */
    
    public static void readOptions(AppOptions opt)
    throws Exception {
        
        BufferedReader inFile;
        String currentLine;
        boolean done = false;
        
        
        try {
            inFile = new BufferedReader(new FileReader(optionsFile));
            
            do {
                currentLine = inFile.readLine();
                
                // done
                if (currentLine==null || currentLine.length()==0) {
                    done = true;
                } else {
                    parseOption(currentLine, opt);
                }
            } while (!done);
            inFile.close();
        } catch (FileNotFoundException nfEx) {
            
            // Optionsdatei nicht gefunden, versuche
            // eine neue mit default-Werten anzulegen
            
            throw new Exception("option file not found...");
        }
        
    }
    
    // returns null if no valid option could be parsed
    private static void parseOption(String line, AppOptions options) {
        StringTokenizer tok = new StringTokenizer(line,":");
        
        String optKey;
        String optType;
        int optVal;
        
        SingleOption result;
        
        if (tok.countTokens()!=3)
            return;
        
        optType = tok.nextToken();
        optKey  = tok.nextToken();
        
        try {
            optVal = Integer.parseInt(tok.nextToken());
            result = new SingleOption(AppOptions.mapTypeString(optType), optVal);
            options.putOption(optKey, result);
        } catch (NumberFormatException nfEx) {
            return;
        }
    }
    
    
    public static void writeOptions(AppOptions opt)
    throws Exception {
        
        PrintWriter writer;
        
        writer = new PrintWriter(optionsFile);
        
        writer.println(opt.toString());
        
        writer.close();
    }
    
    
}
