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
    private ComboBox cmbTickers;
    @FXML
    private ComboBox cmbIntervals;
    @FXML
    private Button btnGetData;
    
    private final String[] TICKERS = {"AAPL", "GOOGL", "MSFT"};
    private String ticker;
    private final String[] INTERVALS = {"1min", "5min", "15min", "30min", "60min"};
    private String interval;
    private final String address = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTickers.getItems().removeAll(cmbTickers.getItems());
        cmbTickers.getItems().addAll(TICKERS);
        cmbTickers.getSelectionModel().select(ticker = TICKERS[0]);
        
        cmbIntervals.getItems().removeAll(cmbIntervals.getItems());
        cmbIntervals.getItems().addAll(INTERVALS);
        cmbIntervals.getSelectionModel().select(interval = INTERVALS[0]);
    }    
    
    @FXML
    private void handleGetData(ActionEvent event) {
        connect();
    }
    
    @FXML
    private void handleTickerChange(ActionEvent event) {
        ticker = (String) cmbTickers.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    private void handleIntervalChange(ActionEvent event) {
        interval = (String) cmbIntervals.getSelectionModel().getSelectedItem();
    }
    
    private void connect() {
        System.out.println("Connecting to site.......");
        try {
            URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=EK35BIPXHQP01Z6W");
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
