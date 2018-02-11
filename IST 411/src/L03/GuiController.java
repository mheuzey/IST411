package L03;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    private Canvas canvas;
    
    private final String[] TICKERS = {"AAPL", "GOOGL", "MSFT", "F", "TSLA"};
    private String ticker;
    private final String[] INTERVALS = {"1min", "5min", "15min", "30min", "60min"};
    private String interval;
    private final String address = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    private StockQuote[] quotes;
    private Line[] lines, bars;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //cmbTickers.getItems().removeAll(cmbTickers.getItems());
        cmbTickers.getItems().addAll(TICKERS);
        cmbTickers.getSelectionModel().select(ticker = TICKERS[0]);
        
        //cmbIntervals.getItems().removeAll(cmbIntervals.getItems());
        cmbIntervals.getItems().addAll(INTERVALS);
        cmbIntervals.getSelectionModel().select(interval = INTERVALS[0]);
        
        quotes = new StockQuote[100];
        lines = new Line[quotes.length];
        bars = new Line[quotes.length];
    }    
        
    @FXML
    private void handleTickerChange(ActionEvent event) {
        ticker = (String) cmbTickers.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    private void handleIntervalChange(ActionEvent event) {
        interval = (String) cmbIntervals.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    private void handleGetData(ActionEvent event) {
        connect();
        showCandleChart();
    }
    
    private void connect() {
        System.out.println("Connecting to site.......");
        try {
            URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=42LP66DV8I8H4X64");
            HttpsURLConnection is = (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            
            String s;
            String[] num;
            for (int i = 0; i < 100; i++){
                do {
                    // skip header information
                    s = rd.readLine();
                } while (!s.contains("1. open"));
                
                quotes[i] = new StockQuote();
                num = s.split("\"");
                quotes[i].setOpen(Double.parseDouble(num[3]));
                
                s = rd.readLine();
                num = s.split("\"");
                quotes[i].setHigh(Double.parseDouble(num[3]));
                
                s = rd.readLine();
                num = s.split("\"");
                quotes[i].setLow(Double.parseDouble(num[3]));
                
                s = rd.readLine();
                num = s.split("\"");
                quotes[i].setClose(Double.parseDouble(num[3]));
                
                s = rd.readLine();
                num = s.split("\"");
                quotes[i].setVolume(Integer.parseInt(num[3]));
            }
            System.err.println(Arrays.toString(quotes));
        } catch (MalformedURLException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void showCandleChart() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        //get range
        double height = canvas.getHeight();
        double width  = canvas.getWidth();
        double gap = width / quotes.length;
        double[] range = getRange();
        double scale = height / (range[1] - range[0]);
        
        // draw each candle
        gc.clearRect(0, 0, width, height);
        
        for (int i = 0; i < quotes.length; i++) {
//            // check if negative
//            if (quotes[i].getClose() < quotes[i].getOpen()) {
//                gc.setStroke(Color.RED);
//            } else {
//                gc.setStroke(Color.GREEN);
//            }
//            
//            // draw line
//            gc.setLineWidth(1);
//            gc.strokeLine(width - i * gap, height - (quotes[i].getHigh() - range[0]) * scale,
//                          width - i * gap, height - (quotes[i].getLow() - range[0]) * scale);
//            
//            // draw bar
//            gc.setLineWidth(5);
//            gc.strokeLine(width - i * gap, height - (quotes[i].getOpen() - range[0]) * scale,
//                          width - i * gap, height - (quotes[i].getClose() - range[0]) * scale);
                        
            
            ((Pane)canvas.getParent()).getChildren().remove(lines[i]);
            ((Pane)canvas.getParent()).getChildren().remove(bars[i]);

            lines[i] = new Line(width - i * gap, height - (quotes[i].getHigh()  - range[0]) * scale,
                                width - i * gap, height - (quotes[i].getLow()   - range[0]) * scale);
            lines[i].setStrokeWidth(1);
            bars[i]  = new Line(width - i * gap, height - (quotes[i].getOpen()  - range[0]) * scale,
                                width - i * gap, height - (quotes[i].getClose() - range[0]) * scale);
            bars[i].setStrokeWidth(5);
            
            // check if negative
            if (quotes[i].getClose() < quotes[i].getOpen()) {
                lines[i].setStroke(Color.RED);
                bars[i].setStroke(Color.RED);
            } else {
                lines[i].setStroke(Color.GREEN);
                bars[i].setStroke(Color.GREEN);
            }
            
            // tooltips
            String text = "High: " + quotes[i].getHigh();
            text += "\nOpen: " + quotes[i].getOpen();
            text += "\nClose: " + quotes[i].getClose();
            text += "\nLow: " + quotes[i].getLow();
            
            Tooltip.install(lines[i], new Tooltip(text));
            Tooltip.install(bars[i], new Tooltip(text));
            
            ((Pane)canvas.getParent()).getChildren().add(lines[i]);
            ((Pane)canvas.getParent()).getChildren().add(bars[i]);
            
        }
    }
    
    private double[] getRange() {
        double min = quotes[0].getLow();
        double max = quotes[0].getHigh();
        for (int i = 1; i < quotes.length; i++) {
            if (quotes[i].getLow() < min) min = quotes[i].getLow();
            if (quotes[i].getHigh() > max) max = quotes[i].getHigh();
        }
        return new double[] {min, max};
    }
}
