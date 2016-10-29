package tetrapro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author capphil1
 */
public class StockFutureData {
    
    public ArrayList<String> accounts = null;
    public ArrayList<String> futureOptions = null;
    public ArrayList<String> name_F = null;
    public ArrayList<String> contract_F = null;
    public ArrayList<String> last_F = null;
    public ArrayList<String> change_F = null;
    public ArrayList<String> open_F = null;
    public ArrayList<String> high_F = null;
    public ArrayList<String> low_F = null;
    public ArrayList<String> time_F = null;
    public ArrayList<String> returns_F = null; 
    public ArrayList<String> qoutes_F = null; 
    public ArrayList<String> tradingData = null;
    /**
     * Connects to barchart.com to pull data from.
     */
    public StockFutureData(){
        initComps();
    }
    /**
     * Initialize components
     */
    private void initComps(){
        setArraysCols_F();
    }
    /**
     * Connect to barchart.com and returns a list of future
     * and commodity contracts to trade.
     * @return arraylist of the commodities
     */
    public ArrayList getFutureOpportunities(){
        Document doc;
        ArrayList<String> prices = new ArrayList<>();
        String option;
         
        try{
            for(int j=3;j<11;j++){
                doc = Jsoup.connect("http://www.barchart.com/commodityfutures/All").timeout(0).get();
                
                Element table= doc.select("table").get(j);
                Elements rows = table.select("tr");

                for(int i=1;i<rows.size();i++){
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                     option = cols.text();
                     
                     if(j == 8 && i == 4){
                        
                    }else{
                         prices.add(option); 
                    }
                }
            }   
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, e);
        }return prices;
    }//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Builds an instance of the gregorian calendar and then
     * with the parameter you can request the day, month or year
     * @param n 1 = year, 2 = month, & 5 = day_of_month
     * @return correct integer number of the day, month, or year
     */
    private int getDate(int n){
        GregorianCalendar gcal = new GregorianCalendar();
        switch (n) {
            case 1:
                return gcal.get(Calendar.YEAR);//n=1
            case 2:
                return gcal.get(Calendar.MONTH);//n=2
            case 5:
                return gcal.get(Calendar.DAY_OF_MONTH);//n=5
            default:
                break;
        }return 0;
    }
     //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Takes in a  date string and returns each component.
     *  So "09/16/16" will give date[0] = 09 and so forth.
     * [0] = month [1] = day & [2] = year
     * @param expire string of expiration date
     * @return int array with corresponding components
     */
    private int[] getDateBreakdown(String expire){
        int day = Integer.parseInt(expire.substring(0,2));
        int month = Integer.parseInt(expire.substring(3,5));
        int year = Integer.parseInt(expire.substring(6, 8));
        return new int[]{month,day,2000+year};//mmDDyyyy
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 
     * @param commodity
     * @return 
     */
    public String getCommoditySymbol(String commodity){
         switch (commodity) {
            case "U.S. Dollar Index":
                return "DX";
            case "British Pound":
                return "B6";
            case "Canadian Dollar":
                return "D6";
            case "Japanese Yen":
                return "J6";
            case "Swiss Franc":
                return "S6";
            case "Euro FX":
                return "E6";
            case "Australian Dollar":
                return "A6";
            case "Mexican Peso":
                return "M6";
            case "New Zealand Dollar":
                return "N6";
            case "South African Rand":
                return "T6";
            case "Brazilian Real":
                return "R6";
            case "Russian Ruble":
                return "R6";
            case "Crude Oil WTI":
                return "CL";
            case "ULSD NY Harbor":
                    return "HO";
            case "Gasoline RBOB":
                return "RB";
            case "Natural Gas":
                return "NG";
            case "Crude Oil Brent (F)":
                return "QA";
            case "Ethanal":
                return "ZK";
            case "T-Bond":
                return "ZB";
            case "Ultra T-Bond":
                return "UD";
            case "10-Year T-Note":
                return "ZN";
            case "5-Year T-Note":
                return "ZF";
            case "2-Year T-Note":
                return "ZT";
            case "30-Day Fed Funds":
                return "ZQ";
            case "Eurodollar":
                return "GE";
            case "Wheat":
                return "ZW";
            case "Corn":
                return "ZC";
            case "Soybeans":
                return "ZS"; 
             case "Soybean Meal":
                return "ZM";
            case "Soybean Oil":
                return "ZL";
            case "Oats":
                return "ZO";
            case "Rough Rice":
                return "ZR";
            case "Hard Red Rice":
                return "KE";
            case "Spring Wheat":
                return "MW";
            case "Canola":
                return "RS";
            case "S&P 500 E-Mini":
                return "ES";
            case "Nasdaq 100 E-Mini":
                return "NQ";
            case "Dow  Indu 30 E-Mini":
                return "YM";
            case "Russell 2000 Mini":
                return "RJ";
            case "S&P Midcap E-Mini":
                return "EW";
            case "S&P 500 VIX":
                return "VI"; 
            case "S&P GSCI":
                return "GD";
            case "Live Cattle":
                return "LE";
            case "Feeder Cattle":
                return "GF";
            case "Lean Hogs":
                return "HE";
            case "Gold":
                return "GC";
            case "Silver":
                return "SI";
            case "High Grade Copper":
                return "HG";
            case "Platinum":
                return "PL";
            case "Palladium":
                return "PA";
            case "Cotton #2":
                return "CT";
            case "Orange Juice":
                return "OJ";
            case "Coffee":
                return "KC";
            case "Sugar #11":
                return "SB";
            case "Cocoa":
                return "CC";
            case "Lumber":
                return "LS";
            default:
                //must be December
                return "Not a correct Symbol";
         }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Returns string of the name of the futures contract given the
     * commodit symbol as the parameter.
     * @param commodity string of the commodity symbol
     * @return 
     */
    public String getCommodity(String commodity){
         switch (commodity) {
            case "ES":
                return "S&P_500_E-Mini_Futures";
            case "SI":
                return "Silver_Futures";
            case "GC":
                return "Gold_Futures";
            case "CL":
                return "Crude_Oil_WTI_Futures";
            case "RB":
                return "Gasoline_RBOB_Futures";
            case "NG":
                return "Natural_Gas_Futures";
            case "QA":
                return "Crude_Oil_Brent_(F)_Futures";
            case "NQ":
                return "Nasdaq_100_E-Mini_Futures";
            case "YM":
                return "Dow_Indu_30_E-Mini_Futures";
            case "RJ":
                return "Russell_2000_Mini_Futures";
            case "VI":
                return "S%26P_500_VIX_Futures";
            case "DX":
                return "US_Dollar_Index_Futures";
            case "B6":
                return "British_Pound_Futures";
            case "D6":
                return "Canadian_Dollar_Futures";
            case "J6":
                return "Japanese_Yen_Futures";
            case "S6":
                return "Swiss_Franc_Futures";
            case "E6":
                return "Euro_FX_Futures";
            case "M6":
                return "Mexican_Peso_Futures";
            case "N6":
                return "New_Zealand_Dollar_Futures";
            case "L6":
                return "Brazilian_Real_Futures";
            case "ZB":
                return "T-Bond_Futures";
            case "UD":
                return "Ultra_T-Bond_Futures";
            case "ZN":
                return "10-Year_T-Note_Futures";
            case "ZF":
                return "5-Year_T-Note_Futures";
            case "ZT":
                return "2-Year_T-Note_Futures"; 
             case "ZQ":
                return "30-Day_Fed_Funds_Futures";
            case "GE":
                return "Eurodollar_Futures";
            case "ZW":
                return "Wheat_Futures";
            case "ZC":
                return "Corn_Futures";
            case "ZM":
                return "Soybean_Meal_Futures";
            case "ZL":
                return "Soybean_Oil_Futures";
            case "HG":
                return "High_Grade_Copper_Futures";
            case "PL":
                return "Platinum_Futures";
            case "PA":
                return "Palladium_Futures";
            case "CT":
                return "Cotton_2_Futures";
            case "OJ":
                return "Orange_Juice_Futures";
            case "KC":
                return "Coffee_Futures";
            case "SB":
                return "Sugar_11_Futures"; 
            case "CC":
                return "Cocoa_Futures";
            default:
                //must be December
                return "Not a correct Symbol";
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * Returns an arraylist of the available option contracts for the
     * given commodity.
     * @param commodity string of the commodity symbol
     * @return 
     */
    public ArrayList getAvailableOptionExpirations(String commodity){
        Document doc;
        ArrayList<String> available = new ArrayList<>();
        String option;
         
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/"
                    + getCommodity(commodity)
                    + "/options/" + commodity
                    + "*0").timeout(0).get();
            Element table= doc.select("table").get(3);
            Elements rows = table.select("tr");

            for(int i=0;i<rows.size();i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                option = cols.text();
                available.add(option);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }return available;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Returns an arraylist of strings of every available option on
     * this cotract.
     * @param commodity string of commodity symbol
     * @param expiry string of the expiry month
     * @return 
     */
    public ArrayList getFutureOptions(String commodity, String expiry){
        Document doc;
        futureOptions = new ArrayList<>();
        tradingData = new ArrayList<>();
        String option;
         
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/" + getCommodity(commodity) 
                    + "/options/" 
                    + expiry
                    + "?view=split&mode=i").get();
            Element table= doc.select("table").get(6);
            Elements rows = table.select("tr");

            int n = rows.size();
            
            for(int i=1;i<n;i++){
                if(i == n -2|| i == n-1){
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    option = cols.text();
                    tradingData.add(option);
                }else{
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    option = cols.text();
                    futureOptions.add(option);
                }
            }
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, e);
        }return futureOptions;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Gets the available options to trade on the given contract and 
     * adds all to the listBox to display.
     * @param symbol string of the commodity symbol
     * @param expiry string of the expiration date
     */
    public void getSelectedData_F(String symbol, String expiry){
         DefaultListModel dlm = new DefaultListModel();
              ArrayList qoute = getFutureOptions(symbol, expiry);
              for(int i=0;i<qoute.size();i++){
                   dlm.addElement(qoute.get(i));
            }IncomeSolution.listBox.setModel(dlm);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 
     * @param n uses contains(asset) so could be symbol
     * or name of contract
     * @return 
     */
      public String getFuturePrice(int n) {
        for (String last_F1 : last_F) {
            return last_F.get(n);
        }return null;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Breaks the data down into respective parts.
     */
    public void setArraysCols_F(){
        qoutes_F = getFutureOpportunities();
        
        name_F =  new ArrayList<>();
        contract_F = new ArrayList<>();
        last_F = new ArrayList<>();
        change_F = new ArrayList<>();
        open_F = new ArrayList<>();
        high_F = new ArrayList<>();
        low_F = new ArrayList<>();
        time_F = new ArrayList<>();
        returns_F = new ArrayList<>();
            
        /**
         * Isolate the name of market to add to name arraylist
         */
        for(int i=0;i<qoutes_F.size();i++){
            String name = null;
            int n = qoutes_F.get(i).lastIndexOf("(");
            name = qoutes_F.get(i).substring(0, n-7);
            name_F.add(name);
            contract_F.add(qoutes_F.get(i).substring(n-6, n+9));
            
            String remaining = qoutes_F.get(i).substring(n+10, qoutes_F.get(i).length()-1);
            String[] parts = remaining.split(" ");
            last_F.add(parts[0]);
            change_F.add(parts[1]);
            open_F.add(parts[2]);
            high_F.add(parts[3]);
            low_F.add(parts[4]);
            time_F.add(parts[5]);
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Gets the contract specifications like quantity of a given commodity,
     * the quality, min tick, initial margin and so forth
     * @param commodity string of the commodity in question.
     * @return string of the specifications
     */
    public String getContractSpecs(String commodity){
        Document doc;
        String contractSpecs = null;
        String option;
         
        try{
            doc = Jsoup.connect("http://www.barchart.com/futures/specifications.php").get();
            Element table= doc.select("table").get(3);
            Elements rows = table.select("tr");

            for(int i=0;i<rows.size();i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                option = cols.text();
                
                if(option.contains(commodity)){
                    contractSpecs = option;
                }
            }
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, e);
        }return contractSpecs;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Return the number of days until the contract given by commodity 
     * and expiration date will expire.
     * @param commodity string of the commodity
     * @param expiry string of the expiration date
     * @return string of the number of days until the contract
     * expires
     */
    public String getDaysToExpire(String commodity, String expiry){
        Document doc;
        ArrayList<String> days = new ArrayList<>();
        String details;
        String daysToExpire = null;
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/"
                    + getCommodity(commodity)
                    + "/options/" + expiry).get();
            Element table= doc.select("table").get(3);
            Elements rows = table.select("tr");

            for(int i=0;i<rows.size();i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                details = cols.text();
                days.add(details);
            }
        daysToExpire = days.get(2);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }return daysToExpire;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}