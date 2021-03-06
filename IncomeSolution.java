package tetrapro;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.proteanit.sql.DbUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * A budget program on one side and another side where
 * we can generate the necessary income to cover the 
 * cost of living demanded by the budget.
 * @author Philip Matthew Caputo
 */
public class IncomeSolution extends javax.swing.JFrame {

    SQLData sql;//database object
    StockFutureData data;//barchart.com connections object
    boolean isXpanelOpen = false;//used to test if a jpanel object is open or not
    boolean isTotalReqOn = false;//used to test with jtogglebutton
    BigDecimal sum = null;//bigddecimal object of the sum of the budget amount
    String x;//string object of the expiration date
    String days = null;//string object of day count until option expiration

    
    /**
     * Creates new form IncomeSolution
     */
    public IncomeSolution() {
        initComponents();
        sql = new SQLData("root", "admin");
        data = new StockFutureData();
        sql.onStartUp();
        displayBudget.setModel(DbUtils.resultSetToTableModel(sql.updateTable("Budget")));
        setDefaultKey();
        getTotalBudget();
        setTimeBar();
        setDateLabel();
        setOpportunities();//sets the jlabel to the number of opptunities in the arraylist
        setSymbols();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets the default keystroke to the add button.
     * When the enter key is pressed on the keyboard,
     * the add button will be clicked.
     */
    private void setDefaultKey(){
        this.getRootPane().setDefaultButton(add);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets the quote with the minimum embedded into the 
     * string.
     * @param min Number of dollars must earn in 40 hr/wk
     */
    private void setTextAreaQoute(BigDecimal min){
        //make string and set tereaxt
        String qoute = "If you are earning or making over "  + min
                + " per hour in a regular 40 hour work week, you are winning.  "
                + "If you are not, then you are losing.";
        dispayTextArea.setText(qoute);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /** 
     *  Sets the jlabel that displays how many rows are in the array
     */ 
    private void setOpportunities(){
        displayNumOfOpps.setText(String.valueOf(data.contract_F.size()));
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
     * Sets the date label to todays date.
     */
    private void setDateLabel(){
        int year = getDate(1);
        int month = getDate(2);
        int day = getDate(5);
        String mo = "";
        switch (month) {
            case 0:
                mo = "Jan";
                break;
            case 1:
                mo = "Feb";
                break;
            case 2:
                mo = "Mar";
                break;
            case 3:
                mo = "Apr";
                break;
            case 4:
                mo = "May";
                break;
            case 5:
                mo = "Jun";
                break;
            case 6:
                mo = "Jul";
                break;
            case 7:
                mo = "Aug";
                break;
            case 8:
                mo = "Sep";
                break;
            case 9:
                mo = "Oct";
                break;
            case 10:
                mo = "Nov";
                break;
            default://must be 11
                mo = "Dec";
                break;
        }
        String dateString = day + "-"  + mo + "-" + year;
        date.setText(dateString);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Set the timeScale bar to the ratio value of today to the end of the month
     */
    private void setTimeBar(){
        int day = getDate(5);
        if(day == 31){
            day = 30;
        }
          BigDecimal qouteint = new BigDecimal(day).divide(new BigDecimal("30.0"), 2 ,RoundingMode.HALF_UP);
        int q =  qouteint.multiply(new BigDecimal("100.0")).intValue();
        timeScale.setValue(q);
    }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Takes bigdecimal amount in and formats it to a currency display
     * @param amount bigdecimal 
     * @return string formated as a currency
     */
    public String getCurrencyDisplay(BigDecimal amount){
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(amount);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Upon clicking a button the frame will expand out and 
     * then collapse back down.
     */
    private void setFrameSize(){
        if(isXpanelOpen){//it s already open
            extPanel.setVisible(false);//make invisible
             this.setSize(370, 515);//set to smaller size
            isXpanelOpen = false;//close panel
        }else{//panel must be closed
            extPanel.setVisible(true);//make visible
              this.setSize(725, 515);//set to larger size
            isXpanelOpen = true;//open panel
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Adds the symbols to the combo box.
     */
    private void setSymbols(){
        for(int i=0;i<data.name_F.size()-1;i++){
        contractBox.addItem(data.name_F.get(i)); 
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    private void setLoadingGif(){
        
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets only the call options in the listbox after rendering them readable
     */
    private void setCallOptions(){
        listBox.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        ArrayList<String> calls = data.futureOptions;    
        displayNumOfOpps.setText(String.valueOf(calls.size()));
        for(int i=1;i<calls.size()-2;i++){
            String[] parts = calls.get(i).split(" ");
            String numOfDays = days.substring(days.length()-2, days.length());

            dlm.addElement(numOfDays + " Day "  + parts[3] + " Call @ "  + parts[0] + " = $" + parts[2]);
        }IncomeSolution.listBox.setModel(dlm);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets only the call options in the listbox after rendering them readable
     */
    private void setPutOptions(){
        listBox.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        ArrayList<String> puts = data.futureOptions;    
        displayNumOfOpps.setText(String.valueOf(puts.size()));
        for(int i=1;i<puts.size()-2;i++){
            String[] parts = puts.get(i).split(" ");
            String numOfDays = days.substring(days.length()-2, days.length());

            dlm.addElement(numOfDays + " Day "  + parts[3] + " Put @ "  + parts[4] + " = $" + parts[6]);
        }IncomeSolution.listBox.setModel(dlm);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Saves an image of the chart of the selected commodity
     * @param imageUrl
     * @param destinationFile
     * @throws IOException 
     */
    public void saveImage(String imageUrl, String destinationFile) throws IOException{
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;
        
        while(((length = is.read(b)) != -1)){
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Returns the contract name of the commodity, DXZ16
     * @param symbol
     * @return 
     */
    public static String getContract(String symbol){
        String contract = null;
        String sym = null;
        Document doc;
        try{
            doc = Jsoup.connect("http://www.barchart.com/quotes/futures/" + symbol + "*0").get();
            Element contractName = doc.select("h1").first();
            contract = contractName.text();
            int len = contract.length();
            sym = contract.substring(len-6, len-1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }return sym;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
    private void displayOptions(String symbol){
        int count = contractExpiry.getItemCount();
        
        if(count != 0){
            String expire = contractExpiry.getSelectedItem().toString();        
            x = expire.substring(expire.length()-5, expire.length());
            days = data.getDaysToExpire(symbol, x);

            displayNumDays.setText(days);

            data.getSelectedData_F(symbol, x);
        }
        
        String last = data.getFuturePrice(contractBox.getSelectedIndex());
        displayPrice.setText(last);
        
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        expenseBox = new javax.swing.JTextField();
        amountBox = new javax.swing.JTextField();
        notesBox = new javax.swing.JTextField();
        expense = new javax.swing.JLabel();
        amount = new javax.swing.JLabel();
        notes = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        displayBudget = new javax.swing.JTable();
        total = new javax.swing.JLabel();
        displayTotal = new javax.swing.JLabel();
        yearly = new javax.swing.JLabel();
        displayYearly = new javax.swing.JLabel();
        weekly = new javax.swing.JLabel();
        displayWeekly = new javax.swing.JLabel();
        daily = new javax.swing.JLabel();
        displayDaily = new javax.swing.JLabel();
        hourly = new javax.swing.JLabel();
        displayHourly = new javax.swing.JLabel();
        jScroll_TArea = new javax.swing.JScrollPane();
        dispayTextArea = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        extPanel = new javax.swing.JPanel();
        timeScale = new javax.swing.JProgressBar();
        date = new javax.swing.JLabel();
        tradingCapital = new javax.swing.JProgressBar();
        putOption = new javax.swing.JButton();
        displayNumOfOpps = new javax.swing.JLabel();
        incomeBar = new javax.swing.JProgressBar();
        toBeFree = new javax.swing.JLabel();
        totalRequired = new javax.swing.JToggleButton();
        contractBox = new javax.swing.JComboBox<>();
        displayPrice = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listBox = new javax.swing.JList<>();
        chart = new javax.swing.JButton();
        contractExpiry = new javax.swing.JComboBox<>();
        displayContractSpecs = new javax.swing.JButton();
        callOption = new javax.swing.JButton();
        displayNumDays = new javax.swing.JLabel();
        more = new javax.swing.JButton();
        removeAccount = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setTitle("tetraPRO( ) Income_Solution");

        expense.setText("Expense:");

        amount.setText("Amount:");

        notes.setText("Notes:");

        add.setText("Add");
        add.addActionListener((java.awt.event.ActionEvent evt) -> {
            addActionPerformed(evt);
        });

        displayBudget.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Expense", "Amount", "Notes"
            }
        ));
        jScrollPane1.setViewportView(displayBudget);

        total.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        total.setText("Total:");

        displayTotal.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        yearly.setText("Yearly:");

        weekly.setText("Weekly:");

        daily.setText("Daily:");

        hourly.setText("Hourly:");

        jScroll_TArea.setOpaque(false);

        dispayTextArea.setBackground(new java.awt.Color(204, 204, 204));
        dispayTextArea.setColumns(20);
        dispayTextArea.setFont(new java.awt.Font("FreeMono", 0, 11)); // NOI18N
        dispayTextArea.setLineWrap(true);
        dispayTextArea.setRows(5);
        dispayTextArea.setWrapStyleWord(true);
        jScroll_TArea.setViewportView(dispayTextArea);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        timeScale.setToolTipText("The Current Months Timeframe");
        timeScale.setStringPainted(true);

        date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        tradingCapital.setToolTipText("Percent of Trading Capital Needed");
        tradingCapital.setString("");
        tradingCapital.setStringPainted(true);

        putOption.setText("Put Option");
        putOption.addActionListener((java.awt.event.ActionEvent evt) -> {
            putOptionActionPerformed(evt);
        });

        displayNumOfOpps.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        incomeBar.setToolTipText("Percent of Monthly Budget");
        incomeBar.setString("");
        incomeBar.setStringPainted(true);

        toBeFree.setText("To Be Free!");

        totalRequired.setText("Total Required");
        totalRequired.addActionListener((java.awt.event.ActionEvent evt) -> {
            totalRequiredActionPerformed(evt);
        });

        contractBox.addActionListener((java.awt.event.ActionEvent evt) -> {
            contractBoxActionPerformed(evt);
        });

        displayPrice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        listBox.setBackground(new java.awt.Color(204, 204, 204));
        listBox.addListSelectionListener((javax.swing.event.ListSelectionEvent evt) -> {
            listBoxValueChanged(evt);
        });
        jScrollPane2.setViewportView(listBox);

        chart.setText("Chart");
        chart.addActionListener((java.awt.event.ActionEvent evt) -> {
            chartActionPerformed(evt);
        });

        contractExpiry.addActionListener((java.awt.event.ActionEvent evt) -> {
            contractExpiryActionPerformed(evt);
        });

        displayContractSpecs.setText("+");
        displayContractSpecs.addActionListener((java.awt.event.ActionEvent evt) -> {
            displayContractSpecsActionPerformed(evt);
        });

        callOption.setText("Call Option");
        callOption.addActionListener((java.awt.event.ActionEvent evt) -> {
            callOptionActionPerformed(evt);
        });

        displayNumDays.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout extPanelLayout = new javax.swing.GroupLayout(extPanel);
        extPanel.setLayout(extPanelLayout);
        extPanelLayout.setHorizontalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extPanelLayout.createSequentialGroup()
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(extPanelLayout.createSequentialGroup()
                            .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(totalRequired, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(extPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(toBeFree, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chart))
                        .addComponent(timeScale, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tradingCapital, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, extPanelLayout.createSequentialGroup()
                            .addGap(122, 122, 122)
                            .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(incomeBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, extPanelLayout.createSequentialGroup()
                            .addComponent(putOption)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(displayNumOfOpps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(callOption)))
                    .addGroup(extPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                            .addGroup(extPanelLayout.createSequentialGroup()
                                .addComponent(displayContractSpecs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contractBox, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(displayPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(displayNumDays, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                    .addGroup(extPanelLayout.createSequentialGroup()
                                        .addComponent(contractExpiry, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(7, 7, 7)))))))
                .addGap(0, 0, 0))
        );
        extPanelLayout.setVerticalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extPanelLayout.createSequentialGroup()
                .addComponent(timeScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(extPanelLayout.createSequentialGroup()
                        .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(putOption)
                            .addComponent(callOption, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(incomeBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(contractBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(contractExpiry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(displayContractSpecs)))
                    .addComponent(displayNumOfOpps, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(displayPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                    .addComponent(displayNumDays, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toBeFree, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalRequired)
                    .addComponent(chart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tradingCapital, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        more.setText("...");
        more.addActionListener((java.awt.event.ActionEvent evt) -> {
            moreActionPerformed(evt);
        });

        removeAccount.setText("-");
        removeAccount.addActionListener((java.awt.event.ActionEvent evt) -> {
            removeAccountActionPerformed(evt);
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(expenseBox, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(removeAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(add, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(amountBox)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(expense)
                                .addGap(63, 63, 63)
                                .addComponent(amount)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(notesBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(notes)
                                .addGap(37, 37, 37))
                            .addComponent(more)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScroll_TArea, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(total)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(displayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(yearly)
                                    .addComponent(weekly)
                                    .addComponent(daily)
                                    .addComponent(hourly))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(displayYearly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(displayWeekly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(displayDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(displayHourly, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(extPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(expense)
                            .addComponent(amount)
                            .addComponent(notes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(expenseBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(amountBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(notesBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(more)
                            .addComponent(removeAccount))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(displayTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(total, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(yearly)
                                    .addComponent(displayYearly, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(weekly)
                                    .addComponent(displayWeekly, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(daily)
                                    .addComponent(displayDaily, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(displayHourly, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hourly)))
                            .addComponent(jScroll_TArea, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        extPanel.setVisible(false);

        pack();
    }// </editor-fold>                        
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Adds the expense, amount, & notes to the database
     * @param evt  
     */
    private void addActionPerformed(java.awt.event.ActionEvent evt) {                                    
        String bill = expenseBox.getText();
        BigDecimal cost = new BigDecimal(amountBox.getText());
        String details = notesBox.getText();
        
        sql.addToBudget(bill, cost, details);
        displayBudget.setModel(DbUtils.resultSetToTableModel(sql.updateTable("Budget")));
        
        expenseBox.setText("");
        amountBox.setText("");
        notesBox.setText("");
        
        getTotalBudget();
        expenseBox.requestFocus();
        
    }                                   
    /**
     * This little button expands the jframe by adding adding another
     * jpanel to the right hand side.  The panel is in fact already created 
     * upon 1st initialization this button just makes it visible and sets the 
     * boolean isXpanelOpen accordingly.
     * @param evt the click on the little button {...}
     */
    private void moreActionPerformed(java.awt.event.ActionEvent evt) {                                     
        setFrameSize();
    }    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets the buttons title to the amount required to stop working upon clicking
     * Checks the isTotalReqOn boolean value to determine choice here
     * @param evt 
     */
    private void totalRequiredActionPerformed(java.awt.event.ActionEvent evt) {                                              
       String message = "This is the amount that you need to be able to stop working if \n" 
               + " it is used to sell options as a security insurance business.";
        
        if(isTotalReqOn){
            totalRequired.setText("Total Required");
            toBeFree.setText("To Be Free!");
            isTotalReqOn = false;
        }else{
            JOptionPane.showMessageDialog(this, message, "Very Important Number!", JOptionPane.INFORMATION_MESSAGE);
            BigDecimal quoteint = sum.divide(new  BigDecimal(".05"), 2, RoundingMode.HALF_UP);
            totalRequired.setText(getCurrencyDisplay(quoteint));
            toBeFree.setText("@ 5% / Month w/ Premium Income!");
            isTotalReqOn = true;
        }   
    }                                             
    /**
     *  Deletes selected records from table
     * @param evt 
     */
    private void removeAccountActionPerformed(java.awt.event.ActionEvent evt) {                                              
        JPanel deletePanel = new DeleteRecord();
        String[] buttons = {"Cancel", "Delete"};
        int n = JOptionPane.showOptionDialog(this,  deletePanel,"Delete Record" , 
            JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
        String accountToRemove = DeleteRecord.expenseBox.getSelectedItem().toString();
        switch (n) {
            case JOptionPane.YES_OPTION://Cancel
                JOptionPane.getRootFrame().dispose();
                break;
            case JOptionPane.NO_OPTION://Delete
                data.accounts = sql.getAccounts();
                sql.deleteExpense(accountToRemove);
                break;
            case JOptionPane.CLOSED_OPTION://x up in the right corner
                JOptionPane.getRootFrame().dispose();
                break;
            default:
                JOptionPane.getRootFrame().dispose();//defualt
                break;
        }
        displayBudget.setModel(DbUtils.resultSetToTableModel(sql.updateTable("Budget")));
        getTotalBudget(); 
    }                                             
    /**
     * Displays put option definition and then sets only the put options available 
     * in the listbox
     * @param evt 
     */
    private void putOptionActionPerformed(java.awt.event.ActionEvent evt) {                                          
         String qoute = "A put option is a contract.  It is a term sercurity  insurance policy. \n" + 
                "It has a expiration date, coverage amount, deductible, and premium cost! \n" + 
                "One could use these instruments as a strategy to get into a position.  Theroetically\n" + 
                " one could get paid to enter a position if one collected enough premium!";
        JOptionPane.showMessageDialog(this, qoute, "What is a Put Option?", JOptionPane.INFORMATION_MESSAGE);
        
        setPutOptions();
    }                                         
    /**
     * Resets progress bars back to zero reading
     * Sets expiration dates in combo box
     * Sets available options in the listbox
     * Display both the price of the future contract 
     * and the days remaining until the front month 
     * contract expires
     * @param evt 
     */
    private void contractBoxActionPerformed(java.awt.event.ActionEvent evt) {                                            
        setLoadingGif();
        
        //the problem is here when I change commodities.
        
        incomeBar.setValue(0);
        incomeBar.setString("");
        tradingCapital.setValue(0);
        tradingCapital.setString("");
        
        contractExpiry.removeAllItems();
        String name = contractBox.getSelectedItem().toString();
        String sym = data.getCommoditySymbol(name);
        ArrayList<String> expiries = data.getAvailableOptionExpirations(sym);
        
        for(int i=0;i<expiries.size();i++){
            contractExpiry.addItem(expiries.get(i));
        }
        
        displayOptions(sym);
    }                                           
    /**
     * Displays an image of the chart or the commodity
     * @param evt 
     */
    private void chartActionPerformed(java.awt.event.ActionEvent evt) {    
        String name = contractBox.getSelectedItem().toString();
        String contract = getContract(data.getCommoditySymbol(name));
        String imageUrl = "http://www.barchart.com/imagechart.php?sym=" + contract
             + "& notitle=true&width=290&height=150&type=basic&plot=BAR";
        String destinationFile = contract + ".jpg";

        try {
            saveImage(imageUrl, destinationFile);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
        
        /**
         * Set image of chart in jframe
         */
        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(destinationFile);
        JLabel label = new JLabel(icon);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Snapshot!");
        frame.pack();
        frame.setVisible(true);
        
        //delete saved image file now...need method
        
    }    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Updates the data in the listbox with the selsected expiration date
     * @param evt 
     */
    private void contractExpiryActionPerformed(java.awt.event.ActionEvent evt) {   
        incomeBar.setValue(0);
        incomeBar.setString("");
        tradingCapital.setValue(0);
        tradingCapital.setString("");
        
        String name = contractBox.getSelectedItem().toString();
        String sym = data.getCommoditySymbol(name);
        displayOptions(sym);
    } 
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Displays a jpanel dialog of the contract specifications
     * and the recent trading data beneath
     * @param evt 
     */
    private void displayContractSpecsActionPerformed(java.awt.event.ActionEvent evt) {                                                     
       String option = data.getContractSpecs(contractBox.getSelectedItem().toString());
        int len = option.length();
       
        String partOne = data.tradingData.get(0);
        String partTwo = data.tradingData.get(1);
        
        if(len%2 == 0){//if len is perfectly divisible by 2
            String part_1 = option.substring(0, len/2);
            String part_2 = option.substring((len+1)/2, len);
            String combined = part_1 + "\n" + part_2 
                    + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" 
                    + partOne + "\n" + partTwo;
            JOptionPane.showMessageDialog(this, combined, "Contract Specifications", JOptionPane.INFORMATION_MESSAGE);
        }else{//otherwise it is not
            String part_1 = option.substring(0, (len+1)/2);
            String part_2 = option.substring((len+1)/2, len);
            String combined = part_1 + "\n" + part_2 
                   + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" 
                   + partOne + "\n" + partTwo;
            JOptionPane.showMessageDialog(this, combined, "Contract Specifications", JOptionPane.INFORMATION_MESSAGE);
        }
    }                                                    
    /**
     * Displays call option definition and then sets only the call options available 
     * in the listbox
     * @param evt 
     */
    private void callOptionActionPerformed(java.awt.event.ActionEvent evt) {                                           
        String qoute = "A call option is a contract.  It is a term sercurity  insurance policy. \n" + 
                "It has a expiration date, coverage amount, deductible, and premium cost! \n" + 
                "One can use these instruments as a strategy to get out of a position.";
        JOptionPane.showMessageDialog(this, qoute, "What is a Call Option?", JOptionPane.INFORMATION_MESSAGE);
        
        setCallOptions();
    }                                          

    private void listBoxValueChanged(javax.swing.event.ListSelectionEvent evt) {                                     
        if(!evt.getValueIsAdjusting()){
                        
            //Need premium to determine number of contracts necessary
            String option = listBox.getSelectedValue();
            if(option == null){
                // nothing to do here.
            }else{
                int n = option.indexOf("$");
                 
                if(n == -1){
                    JOptionPane.showMessageDialog(this, "Please select the put or call option first.", 
                            "Huh Oh!", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    String premium = option.substring(n+1, option.length());//amount as decimal... 145
                String p = premium.replace(",", "");
                
                //total monthly expenses divided by premium
                String expenses = displayTotal.getText().substring(1, displayTotal.getText().length());//2,262
                String e = expenses.replace(",", "");
                BigDecimal qouteint = new BigDecimal(e).divide(new BigDecimal(p), 2, RoundingMode.CEILING);
                int m = (int) Math.ceil(qouteint.doubleValue());//number of contracts needed to cover monthly cost of living
                
                //Need margin required to determin percent of trading capital
                String contractSpecs = data.getContractSpecs(contractBox.getSelectedItem().toString());
                int len = contractSpecs.length();
                String part_2 = contractSpecs.substring((len+1)/2, len);
                int index = part_2.indexOf("/");
                String marginPart = part_2.substring(index+1, part_2.length());
                String[] parts = marginPart.split(" ");
                String mar = parts[0].replace(",", "");
                BigDecimal margin = new BigDecimal(mar);//amount of margin usually more than required
                BigDecimal numOfContracts = new BigDecimal(m);//number of contracts needed to cover monthly cost of living
                BigDecimal needed = margin.multiply(numOfContracts);
                BigDecimal quoteint = sum.divide(new  BigDecimal(".05"), 2, RoundingMode.HALF_UP);
                BigDecimal q = needed.divide(quoteint, 2, RoundingMode.CEILING).multiply(new BigDecimal("100"));
                BigDecimal totalPrem = new BigDecimal(p).multiply(numOfContracts);
                BigDecimal ratio = totalPrem.divide(new BigDecimal(e), 2, RoundingMode.CEILING)
                        .multiply(new BigDecimal("100"));
                
                //set progress bars
                tradingCapital.setValue(q.intValue());
                tradingCapital.setString(q.intValue() + "% = " + getCurrencyDisplay(needed) + " : " + m + " Contracts");
                incomeBar.setValue(ratio.intValue());
                incomeBar.setString(ratio.intValue() + "% = " + getCurrencyDisplay(totalPrem));
                }  
            }
        }
    }                                    
    /**
     *  Get and set the total budget value
     *  is displayed on gui
     */
    private void getTotalBudget(){
        sum = sql.getSumTotal();
        
        if(sum == null){//if sum is null
            displayTotal.setText("0.0");        
        }else{//otherwise it is not null
            //1st make sum a formatted US dollar
            displayTotal.setText(getCurrencyDisplay(sum));
            
            //do some math and display the other time frames
          BigDecimal yearlyCost = sum.multiply(new BigDecimal("12"));//sum*12 months
        displayYearly.setText(String.valueOf(yearlyCost));
        BigDecimal weeklyCost = yearlyCost.divide(new BigDecimal("52"), 2, RoundingMode.HALF_UP);//yearly / 52 weeks
        displayWeekly.setText(String.valueOf(weeklyCost));
        BigDecimal dailyCost = weeklyCost.divide(new BigDecimal("7"), 2 ,RoundingMode.HALF_UP);//weekly / 7
        displayDaily.setText(String.valueOf(dailyCost));
        BigDecimal hourlyCost = dailyCost.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP);//daily / 24
        displayHourly.setText(String.valueOf(hourlyCost));
        
        BigDecimal fortyHourW_W = weeklyCost.divide(new BigDecimal("40.0"), 2, RoundingMode.HALF_UP);
        setTextAreaQoute(fortyHourW_W);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IncomeSolution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            IncomeSolution solution = new IncomeSolution();
            solution .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            solution .setLocationRelativeTo(null);
           solution .setResizable(false);
            solution .setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton add;
    private javax.swing.JLabel amount;
    private javax.swing.JTextField amountBox;
    private javax.swing.JButton callOption;
    private javax.swing.JButton chart;
    private javax.swing.JComboBox<String> contractBox;
    private javax.swing.JComboBox<String> contractExpiry;
    private javax.swing.JLabel daily;
    private javax.swing.JLabel date;
    private javax.swing.JTextArea dispayTextArea;
    private javax.swing.JTable displayBudget;
    private javax.swing.JButton displayContractSpecs;
    private javax.swing.JLabel displayDaily;
    private javax.swing.JLabel displayHourly;
    private javax.swing.JLabel displayNumDays;
    private javax.swing.JLabel displayNumOfOpps;
    private javax.swing.JLabel displayPrice;
    private javax.swing.JLabel displayTotal;
    private javax.swing.JLabel displayWeekly;
    private javax.swing.JLabel displayYearly;
    private javax.swing.JLabel expense;
    private javax.swing.JTextField expenseBox;
    private javax.swing.JPanel extPanel;
    private javax.swing.JLabel hourly;
    private javax.swing.JProgressBar incomeBar;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScroll_TArea;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JList<String> listBox;
    private javax.swing.JButton more;
    private javax.swing.JLabel notes;
    private javax.swing.JTextField notesBox;
    private javax.swing.JButton putOption;
    private javax.swing.JButton removeAccount;
    private javax.swing.JProgressBar timeScale;
    private javax.swing.JLabel toBeFree;
    private javax.swing.JLabel total;
    private javax.swing.JToggleButton totalRequired;
    private javax.swing.JProgressBar tradingCapital;
    private javax.swing.JLabel weekly;
    private javax.swing.JLabel yearly;
    // End of variables declaration                   

}