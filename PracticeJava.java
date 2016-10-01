/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TestingNewCode;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Philip Caputo
 */
public class PracticeJava{
    public static void main(String[] args){
       String days = getDaysToExpire("SI", "November");
       System.out.println(days);
       getAvailableOptionExpirations("SI");
       ArrayList<String> lastPrices = getFuturePrices();
       for(int i=0;i<lastPrices.size();i++){
           if(lastPrices.get(i).contains("SI")){
               System.out.println(lastPrices.get(i));
           }
       }
       getFutureOptions("SI", "November");
    }
    /**
     * Returns an arraylist of strings of every available option on
     * this cotract.
     * @param commodity string of commodity symbol
     * @param month string of the expiry month
     * @return 
     */
    private static ArrayList getFutureOptions(String commodity, String month){
        Document doc;
        ArrayList<String> futureOptions = new ArrayList<>();
        String option;
         
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/" + getCommodity(commodity) 
                    + "/options/" 
                    + commodity + getFutureMonth(month) + "16"//getContractCode();
                    + "?view=split&mode=i").get();
            Element table= doc.select("table").get(6);
            Elements rows = table.select("tr");

            for(int i=1;i<rows.size();i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                option = cols.text();
                futureOptions.add(option);
            }
            for(int i=1;i<futureOptions.size();i++){
                System.out.println(futureOptions.get(i));
            }
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, e);
        }return futureOptions;
    }
    /**
     * Returns the future month code.
     * @param month string of the month of the contract
     * @return 
     */
    private static char getFutureMonth(String month){
        switch (month) {
            case "January":
                return 'F';
            case "February":
                return 'G';
            case "March":
                return 'H';
            case "April":
                return 'J';
            case "May":
                return 'K';
            case "June":
                return 'M';
            case "July":
                return 'N';
            case "August":
                return 'Q';
            case "September":
                return 'U';
            case "October":
                return 'V';
            case "November":
                return 'X';
            default:
                //must be December
                return 'Z';
        }
    }
    /**
     * Returns string of the name of the futures contract given the
     * commodit symbol as the parameter.
     * @param commodity string of the commodity symbol
     * @return 
     */
    private static String getCommodity(String commodity){
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
    /**
     * Returns an arraylist of the available option contracts for the
     * given commodity.
     * @param commodity string of the commodity symbol
     * @return 
     */
    private static ArrayList getAvailableOptionExpirations(String commodity){
        Document doc;
        ArrayList<String> available = new ArrayList<>();
        String option;
         
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/"
                    + getCommodity(commodity)
                    + "/options/" + commodity
                    + "*0").get();
            Element table= doc.select("table").get(3);
            Elements rows = table.select("tr");

            for(int i=0;i<rows.size();i++){
                Element row = rows.get(i);
                Elements cols = row.select("td");
                option = cols.text();
                available.add(option);
            }
            for(int i=0;i<available.size();i++){
                System.out.println(available.get(i));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }return available;
    }
    /**
     * Uses the Jsoup library to pull directly from the tables
     * @return 
     */
    private static ArrayList getFuturePrices(){
        Document doc;
        ArrayList<String> prices = new ArrayList<>();
        String option;
         
        try{
            for(int j=3;j<11;j++){
                doc = Jsoup.connect("http://www.barchart.com/commodityfutures/All").get();
                Element table= doc.select("table").get(j);
                Elements rows = table.select("tr");

                for(int i=0;i<rows.size();i++){
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    option = cols.text();
                    prices.add(option);
                }
            }   
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, e);
        }return prices;
    }
    /**
     * Returns a string of the days remaining until expiration.
     * @param commodity string of the commodity symbol
     * @param month string of the expiration month of the contract
     * @return 
     */
    private static String getDaysToExpire(String commodity, String month){
        Document doc;
        ArrayList<String> days = new ArrayList<>();
        String details;
        String daysToExpire = null;
        try{
            doc = Jsoup.connect("http://www.barchart.com/commodityfutures/"
                    + getCommodity(commodity)
                    + "/options/" + commodity
                    + getFutureMonth(month)
                    + "16").get();
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
}
