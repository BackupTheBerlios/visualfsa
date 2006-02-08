
package threads;

import gui.dialogs.BusyDialog;
import algo.GenericAlgorithm;

public class GenericAlgoThread {
    
    private GenericAlgorithm myAlgo;
    public String errMsg;
    
    public GenericAlgoThread(GenericAlgorithm _myAlgo) {
        myAlgo = _myAlgo;
    }
    
    public void runAlgo(final BusyDialog waitDlg) {
        
        Thread work = new Thread() {
            public void run() {
                myAlgo.runAlgorithm();
                waitDlg.dispose();
            }
        };
        work.start();
    };
    
    
    
}
