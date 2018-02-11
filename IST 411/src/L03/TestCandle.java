package L03;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;
import org.jfree.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class TestCandle extends JFrame {
    private String ticker;
    private int interval;
    private String stockSymbol;
    private ChartPanel chartPanel;
    private JPanel panel;
    private XYPlot mainPlot;
    private JFreeChart chart;
    JLabel highest, lowest;
    private final String address = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
    String[] stock_list = {"AAPL", "MSFT", "GOOGL","INTC", "NVDA"};
    String[] itv_list = {"1", "5", "15", "30", "60"};
    
    public TestCandle(String stockSymbol, int interval) {
        super("CandlestickDemo");
        
        this.stockSymbol = stockSymbol;
        this.interval = interval;
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
            
        DateAxis    domainAxis       = new DateAxis("Date");
        NumberAxis  rangeAxis        = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset   dataset          = getDataSet(stockSymbol, interval);

        mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline( SegmentedTimeline.newMondayThroughFridayTimeline() );

        
        chart = new JFreeChart(stockSymbol, null, mainPlot, false);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        
        JPanel infoPanel = new JPanel();
        JPanel selectPanel = new JPanel();
        
        highest = new JLabel("Highest: " + getHighest(stockSymbol));
        lowest = new JLabel("Lowest: " + getLowest(stockSymbol));
        
        infoPanel.add(highest);
        infoPanel.add(lowest);
        
        JLabel stockLabel = new JLabel("Stock: ");
        JLabel intervalLabel = new JLabel("Interval: ");
        JComboBox stockCB = new JComboBox(stock_list);
        JComboBox intervalCB = new JComboBox(itv_list);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String selected_stock = (String)stockCB.getSelectedItem();
                int selected_interval = Integer.parseInt((String)intervalCB.getSelectedItem());
                
                mainPlot = new XYPlot(getDataSet(selected_stock, selected_interval), domainAxis, rangeAxis, renderer);
                chart = new JFreeChart(selected_stock, null, mainPlot, false);
                
                chartPanel.setChart(chart);
                chartPanel.validate();
                chartPanel.repaint();
                
                highest.setText("Highest: " + getHighest(selected_stock));
                lowest.setText("Lowest: " + getLowest(selected_stock));
                
                panel.validate();
                panel.repaint();
            }
        });
        
        selectPanel.add(stockLabel);
        selectPanel.add(stockCB);
        selectPanel.add(intervalLabel);
        selectPanel.add(intervalCB);
        selectPanel.add(refreshButton);
        
        panel.add(selectPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        this.add(panel);
        this.pack();
    }
    protected AbstractXYDataset getDataSet(String stockSymbol, int interval) {
        //This is the dataset
        DefaultOHLCDataset result = null;
        //This is the data needed
        OHLCDataItem[] data;
        
        data = getData(stockSymbol, interval);

        //Create an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    }
   
    protected OHLCDataItem[] getData(String stockSymbol, int interval) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        try {
            //URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=EK35BIPXHQP01Z6W" + "&datatype=csv");
            String strUrl= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=" + interval + "min&apikey=42LP66DV8I8H4X64&datatype=csv";
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
    
    protected Double getHighest(String stockSymbol){
        List<Double> highs = new ArrayList<Double>();
        
                try {
            //URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=EK35BIPXHQP01Z6W" + "&datatype=csv");
            String strUrl= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=60min&apikey=42LP66DV8I8H4X64&datatype=csv";
            URL url = new URL(strUrl);
            HttpsURLConnection is= (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            String inputLine;
            rd.readLine();
            while ((inputLine = rd.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date timestamp       = df.parse( st.nextToken() );
                double open     = Double.parseDouble( st.nextToken() );
                double high     = Double.parseDouble( st.nextToken() );
                highs.add(high);
                double low      = Double.parseDouble( st.nextToken() );
                double close    = Double.parseDouble( st.nextToken() );
                double volume   = Double.parseDouble( st.nextToken() );
                

                OHLCDataItem item = new OHLCDataItem(timestamp, open, high, low, close, volume);
            }
            rd.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
                
        return Collections.max(highs);
    }
    
        protected Double getLowest(String stockSymbol){
        List<Double> lows = new ArrayList<Double>();
        
                try {
            //URL url = new URL(address + "&symbol=" + ticker + "&interval=" + interval + "&apikey=EK35BIPXHQP01Z6W" + "&datatype=csv");
            String strUrl= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=60min&apikey=42LP66DV8I8H4X64&datatype=csv";
            URL url = new URL(strUrl);
            HttpsURLConnection is= (HttpsURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is.getInputStream()));
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            String inputLine;
            rd.readLine();
            while ((inputLine = rd.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date timestamp       = df.parse( st.nextToken() );
                double open     = Double.parseDouble( st.nextToken() );
                double high     = Double.parseDouble( st.nextToken() );
                double low      = Double.parseDouble( st.nextToken() );
                lows.add(low);
                double close    = Double.parseDouble( st.nextToken() );
                double volume   = Double.parseDouble( st.nextToken() );
                

                OHLCDataItem item = new OHLCDataItem(timestamp, open, high, low, close, volume);
            }
            rd.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
                
        return Collections.min(lows);
    }
   
    
    public static void main(String[] args) {
        new TestCandle("SRCE", 60).setVisible(true);
    }
}
