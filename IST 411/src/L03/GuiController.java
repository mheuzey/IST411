package L03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javax.net.ssl.HttpsURLConnection;

/**
 * FXML Controller class
 *
 * @author Team 2
 */
public class GuiController implements Initializable {
    @FXML
    private ComboBox cmbTicker;
    @FXML
    private ComboBox cmbTime;
    @FXML
    private Button btnGetData;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML
    private void handleGetData(ActionEvent event) {
        connect();
    }
    
    private void connect() {
        System.out.println("Connecting to site.......");
        try {
            URL url = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=AAPL&interval=1min&apikey=EK35BIPXHQP01Z6W");
            HttpsURLConnection is= (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            String s;
            while ((s = rd.readLine()) != null) {
                System.out.println(rd.readLine());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
