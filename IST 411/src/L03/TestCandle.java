package L03;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class TestCandle extends JFrame {
    private String ticker;
    private String interval;
    private final String address = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    
    public TestCandle(String stockSymbol) {
        super("CandlestickDemo");
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DateAxis    domainAxis       = new DateAxis("Date");
        NumberAxis  rangeAxis        = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset   dataset          = getDataSet(stockSymbol);

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

       

        
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline( SegmentedTimeline.newMondayThroughFridayTimeline() );

        
        JFreeChart chart = new JFreeChart(stockSymbol, null, mainPlot, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));

        this.add(chartPanel);
        this.pack();
    }
    protected AbstractXYDataset getDataSet(String stockSymbol) {
        //This is the dataset
        DefaultOHLCDataset result = null;
        //This is the data needed
        OHLCDataItem[] data;
        
        data = getData(stockSymbol);

        //Create an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    }
   
    protected OHLCDataItem[] getData(String stockSymbol) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
            //URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=EK35BIPXHQP01Z6W" + "&datatype=csv");
            String strUrl= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=15min&apikey=EK35BIPXHQP01Z6W&datatype=csv";
            URL url = new URL(strUrl);
            HttpsURLConnection is= (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            String inputLine;
            rd.readLine();
            while ((inputLine = rd.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date timestamp       = df.parse( st.nextToken() );
                System.out.println(timestamp);
                double open     = Double.parseDouble( st.nextToken() );
                System.out.println(open);
                double high     = Double.parseDouble( st.nextToken() );
                double low      = Double.parseDouble( st.nextToken() );
                double close    = Double.parseDouble( st.nextToken() );
                double volume   = Double.parseDouble( st.nextToken() );
                

                OHLCDataItem item = new OHLCDataItem(timestamp, open, high, low, close, volume);
                dataItems.add(item);
            }
            rd.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);

        return data;
    }

    public static void main(String[] args) {
        new TestCandle("MSFT").setVisible(true);
    }
}
