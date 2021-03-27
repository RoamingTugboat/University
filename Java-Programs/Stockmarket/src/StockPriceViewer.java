//import java.util.TimerTask;
//import java.util.Timer;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
/**
 * JPanel, das ein Objekt einer der Unterklassen von StockPriceProvider uebergeben bekommt und entsprechend Aktienkurse graphisch darstellt.
 *
 */

@SuppressWarnings("serial")
public class StockPriceViewer extends JFrame {
	
    private JLabel clockLabel;
    private StockPriceProvider sprovider;
    
    public StockPriceViewer(StockPriceProvider sprovider) {
    	this.sprovider = sprovider;
    	clockLabel = new JLabel(" LOADING GUI ...");
        add("Center", clockLabel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 70);
        setVisible(true);
    }
    
	
        public void run() { 				
            String output = createText();
            clockLabel.setText(output);
            clockLabel.repaint();
        }

        private String createText() {       // FENSTERUPDATES ff.     	
            String output = "<html><body>" + sprovider.returnAllShareData(); 
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            DateFormat dateFormatter = DateFormat.getDateTimeInstance();
            output += "  <font size=\"2\"><i>" + dateFormatter.format(date) +  "</i></font></body></html>";
            return output;
        }

 }