
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author capphil1
 */
public class CostBurden extends javax.swing.JFrame {

    SQLData sql;
    boolean isXpanelOpen = false;
    boolean isFirstPage = false;
    boolean isTotalReqOn = false;
   JPanel[] expensePanels = null;
   private static ArrayList<String> accounts = null;
   public static ArrayList<String> returns = null;
   public static ArrayList<String> qoutes  = null;
   public static ArrayList<String> symbol = new ArrayList<>();
    public static ArrayList<String>  type = new ArrayList<>();
    public static ArrayList<String> strike = new ArrayList<>();
    public static ArrayList<String> expiration = new ArrayList<>();
    public static ArrayList<String> bid = new ArrayList<>();
    public static ArrayList<String> ask = new ArrayList<>();
    public static ArrayList<String> last = new ArrayList<>();
    public static ArrayList<String> volume = new ArrayList<>();
    public static ArrayList<String>  openInterest = new ArrayList<>();
    public static ArrayList<String> volatility= new ArrayList<>();
    public static ArrayList<String> time= new ArrayList<>();
    
    /**
     * Creates new form CostBurden
     */
    public CostBurden() {
        initComponents();
        sql = new SQLData("root", "admin");
        sql.onStartUp();
        displayBudget.setModel(DbUtils.resultSetToTableModel(sql.updateTable("Budget")));
        setDefaultKey();
        getTotalBudget();
        setTimeBar();
        setDateLabel();
        loadAccounts();
       setOpportunities();//sets the jlabel to the number of opptunities in the arraylist
       buildTabs();
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
     * Sets the quote with the min embedded into the 
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
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     *  Build a url link to a barchart excel file with a list of the options with
     *  the highest implied volatilities. 
     * @return arraylist of the data from barchart
     */
    private static ArrayList getOpportunities(){
        String nextLine;
         ArrayList<String> list = new ArrayList<>();
         try{            
             String qoute = "http://www.barchart.com/option-center/getExcel.php?type=stocks&page=Highest%20Implied%"
                     + "20Volatility&dir=desc&sort=volatility&filter_conditions=a%3A3%3A%7Bi%3A0%3Ba%3A3%3A%7Bi%"
                     + "3A0%3Bs%3A10%3A%22volatility%22%3Bi%3A1%3Bs%3A1%3A%22%3E%22%3Bi%3A2%3Bs%3A1%"
                     + "3A%220%22%3B%7Di%3A1%3Ba%3A3%3A%7Bi%3A0%3Bs%3A6%3A%22volume%22%3Bi%3A1%3Bs%"
                     + "3A1%3A%22%3E%22%3Bi%3A2%3Bs%3A3%3A%22500%22%3B%7Di%3A2%3Ba%3A3%3A%7Bi%3A0%"
                     + "3Bs%3A13%3A%22open_interest%22%3Bi%3A1%3Bs%3A1%3A%22%3E%22%3Bi%3A2%3Bs%3A3%3A%"
                     + "22100%22%3B%7D%7D&f=base_symbol,type,strike,expiration_date,bid,ask,last,volume,open_interest,volatility,"
                     + "timestamp&fn=Symbol,Type,Strike,Expiration,Bid,Ask,Last,Volume,Open%20Interest,Volatility,Time";
             URL url = new URL(qoute);
             URLConnection connect = url.openConnection();
             InputStreamReader inStream = new InputStreamReader(connect.getInputStream());
             BufferedReader buffer = new BufferedReader(inStream);
            
             while(buffer.readLine() != null){
                 nextLine = buffer.readLine();
                     //do something here with data
                     list.add(nextLine);
                 }
         }catch(Exception ex){
             JOptionPane.showMessageDialog(null, ex);
         }return list;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     *  Breaks the original arraylist down into smaller arrays of the corresponding columns.
     */
    public  static void setArrayCols(){
        qoutes = getOpportunities();
        returns = new ArrayList<>();
        
    for(int i=0;i<qoutes.size()-1;i++){
        String[] parts = qoutes.get(i).split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"); 

        symbol.add(parts[0]);
        type.add(parts[1]);

         BigDecimal part;
        if(parts[2].startsWith("\"")){
            part = new BigDecimal("888");
        }else{
            part = new BigDecimal(parts[2]);
        }

        strike.add(part.toString());
        expiration.add(parts[3]);
        bid.add(parts[4]);
        ask.add(parts[5]);
        last.add(parts[6]);
        volume.add(parts[7]);
        openInterest.add(parts[8]);
        volatility.add(parts[9]);
        time.add(parts[10]);
        }
    
    //compute and then add the roi into the returns array for every i
        for(int i=0;i<qoutes.size()-1;i++){
            BigDecimal x = new BigDecimal(strike.get(i));
            
        if(type.get(i).equalsIgnoreCase("Put")){
            BigDecimal p = new BigDecimal(last.get(i));
            BigDecimal q = p.multiply(new BigDecimal("100")).divide(x, 2, RoundingMode.HALF_UP);
          returns.add(q.toString());
        }else{//must be a call
           returns.add("I am a call");
           }
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    //private String[] getDesiredOrder(String[] unOrdered){
        //reorder the qoutes array with the highest return at the top
       // String[] ordered = {};
    //}
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /** 
     *  Sets the jlabel that displays how many rows are in the array
     */ 
    private void setOpportunities(){
        setArrayCols();
        displayNumOfOpps.setText(String.valueOf(symbol.size()));
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
     *  Load budget accounts into combo box
     */
    private void loadAccounts(){
        expenseList.removeAllItems();
        accounts = sql.getAccounts();
        for(int i =0; i<accounts.size();i++){
            expenseList.addItem(accounts.get(i));
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Takes bigdecimal amount in and formats it to a currency display
     * @param amount bigdecimal 
     * @return string formated as a currency
     */
    public static String getCurrencyDisplay(BigDecimal amount){
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
             this.setSize(370, 505);//set to smaller size
            isXpanelOpen = false;//close panel
        }else{//panel must be closed
            extPanel.setVisible(true);//make visible
              this.setSize(725, 505);//set to larger size
            isXpanelOpen = true;//open panel
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Create a tabbed pane for every account in the budget.
     * To be used when the add button is clicked to add a new 
     * expense to the budget.
     */
    private void createTabs(String account){
        JPanel nextTab = new TabbedPanel();
        jTabbedPane1.addTab(account, nextTab);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     *  Build a tab for every expense in the budget 
     *  to be called upon 1st creation
     */
    private void buildTabs(){
        //get list of expenses
        //create a tabedPane for each and initialize
        int i = 0;
        accounts = sql.getAccounts();
        while(i < accounts.size()){
            expensePanels = new JPanel[accounts.size()];
            expensePanels[i] = new TabbedPanel();
            jTabbedPane1.addTab( accounts.get(i), expensePanels[i]);
            i++;
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        expenseList = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        budget = new javax.swing.JLabel();
        displayBudgetAmount = new javax.swing.JLabel();
        needWorking = new javax.swing.JProgressBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        savingsBox = new javax.swing.JTextField();
        savings = new javax.swing.JLabel();
        enter = new javax.swing.JButton();
        totalRequired = new javax.swing.JToggleButton();
        toBeFree = new javax.swing.JLabel();
        putOption = new javax.swing.JButton();
        oppsToday = new javax.swing.JLabel();
        displayNumOfOpps = new javax.swing.JLabel();
        savingsBar = new javax.swing.JProgressBar();
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
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
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

        jLabel1.setText("Select an account:");

        budget.setText("Budget:");

        displayBudgetAmount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        needWorking.setToolTipText("Percent of Total Required Capital to be FREE of the Rat Race!");
        needWorking.setStringPainted(true);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        savings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        savings.setText("Savings:");

        enter.setText("Enter");
        enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterActionPerformed(evt);
            }
        });

        totalRequired.setText("Total Required");
        totalRequired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalRequiredActionPerformed(evt);
            }
        });

        toBeFree.setText("To Be Free!");

        putOption.setText("Put Option");
        putOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                putOptionActionPerformed(evt);
            }
        });

        oppsToday.setText("Opportunities Today:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(savings, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(putOption)
                                .addGap(46, 46, 46)
                                .addComponent(oppsToday)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(displayNumOfOpps, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(savingsBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(toBeFree, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(savingsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(totalRequired, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(enter, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(putOption)
                    .addComponent(oppsToday)
                    .addComponent(displayNumOfOpps, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(savingsBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                .addComponent(savings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(savingsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toBeFree, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalRequired)
                    .addComponent(enter))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Savings", jPanel1);

        javax.swing.GroupLayout extPanelLayout = new javax.swing.GroupLayout(extPanel);
        extPanel.setLayout(extPanelLayout);
        extPanelLayout.setHorizontalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(timeScale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(needWorking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
            .addGroup(extPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(11, 11, 11)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(extPanelLayout.createSequentialGroup()
                        .addComponent(expenseList, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(budget)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(displayBudgetAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        extPanelLayout.setVerticalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extPanelLayout.createSequentialGroup()
                .addComponent(timeScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayBudgetAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(expenseList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(budget, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(needWorking, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        expenseList.addItemListener((ItemEvent ie) -> {
            //add amount to displayBudgetAmount...
            if(ie.getStateChange()   == ItemEvent.SELECTED){
                BigDecimal amountOfBudget = sql.getAmountOfBudget(expenseList.getSelectedItem().toString());
                displayBudgetAmount.setText(amountOfBudget.toString());

                //need to fix tab method...so the correect tab shows up...
            }
        });
        jTabbedPane1.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                JTabbedPane sourceOfPane = (JTabbedPane) ce.getSource();
                int index = sourceOfPane.getSelectedIndex();
                String expense = sourceOfPane.getTitleAt(index);
                if(expense.equals("Savings")){

                }else{
                    BigDecimal budgetAmount = sql.getAmountOfBudget(expense);
                    BigDecimal quoteint = budgetAmount.divide(new BigDecimal(".05"), 2, RoundingMode.HALF_UP);

                    //TabbedPanel.requiredCapital_0.setText(getCurrencyDisplay(quoteint));

                    expenseList.setSelectedItem(expense);
                }

            }
        });

        more.setText("...");
        more.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreActionPerformed(evt);
            }
        });

        removeAccount.setText("-");
        removeAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAccountActionPerformed(evt);
            }
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
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(total)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(displayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(jScroll_TArea))))
                .addContainerGap())
        );

        extPanel.setVisible(false);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
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
        
        loadAccounts();//load new account into combo box
        //create a tabbedpane for each account of budget.
       createTabs(bill);
    }//GEN-LAST:event_addActionPerformed
    /**
     * This little button expands the jframe by adding adding another
     * jpanel to the right hand side.  The panel is in fact already created 
     * upon 1st initialization this button just makes it visible and sets the 
     * boolean isXpanelOpen accordingly.
     * @param evt the click on the little button {...}
     */
    private void moreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreActionPerformed
        setFrameSize();
    }//GEN-LAST:event_moreActionPerformed

    private void totalRequiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalRequiredActionPerformed
       String message = "This is the amount that you need to be able to stop working if " 
               + " it is used to sell options as a sercurity business..";
        JOptionPane.showMessageDialog(this, message, "Very Important Number!", JOptionPane.INFORMATION_MESSAGE);
        
        if(isTotalReqOn){
            totalRequired.setText("Total Required");
            toBeFree.setText("To Be Free!");
            isTotalReqOn = false;
        }else{
            BigDecimal sum= sql.getSumTotal();
            BigDecimal quoteint = sum.divide(new  BigDecimal(".05"), 2, RoundingMode.HALF_UP);
            totalRequired.setText(getCurrencyDisplay(quoteint));
            toBeFree.setText("@ 5% / Month w/ Premium Income!");
            isTotalReqOn = true;
        }
        int n = 100;
        needWorking.setValue(n);//sets the progress bar to 100% full.  Then as we apply
        //the funds to our positions, we will be able to see how much income we can generate.
    }//GEN-LAST:event_totalRequiredActionPerformed

    private void enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterActionPerformed
        BigDecimal quoteint = sql.getSumTotal().divide(new  BigDecimal(".05"), 2, RoundingMode.HALF_UP);
        BigDecimal diff = quoteint.subtract(new BigDecimal(savingsBox.getText()));
        BigDecimal percent = BigDecimal.ONE.subtract(diff.divide(quoteint, 2, RoundingMode.HALF_UP));
        BigDecimal timesHundred = percent.multiply(new BigDecimal("100"));
        needWorking.setValue(timesHundred.intValue());
        savingsBox.setText("");
    }//GEN-LAST:event_enterActionPerformed
    /**
     *  Deletes selected records from table
     * @param evt 
     */
    private void removeAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAccountActionPerformed
        JPanel deletePanel = new DeleteRecord();
        String[] buttons = {"Cancel", "Delete"};
         int n = JOptionPane.showOptionDialog(this,  deletePanel,"Delete Record" , 
                JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
         String accountToRemove = DeleteRecord.accountsBox.getSelectedItem().toString();
        switch (n) {
            case JOptionPane.YES_OPTION://Cancel
                JOptionPane.getRootFrame().dispose();
                break;
            case JOptionPane.NO_OPTION://Delete
                accounts = sql.getAccounts();
                for(int i=0;i<accounts.size();i++){
                    if(accounts.get(i).equals(accountToRemove)){
                        jTabbedPane1.removeTabAt(i+1);//tabs starts count at 0
                    }
                }
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
    }//GEN-LAST:event_removeAccountActionPerformed

    private void putOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_putOptionActionPerformed
         String qoute = "A put option is a contract.  It is a term sercurity  insurance policy. \n" + 
                "It has a expiration date, coverage amount, deductible, and premium cost! \n" + 
                "When you sell these, you can generate all of the income for your monthly\n" + 
                 " expenses without working 40 hours every week of your life!";
        JOptionPane.showMessageDialog(this, qoute, "What is a Put Option?", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_putOptionActionPerformed
    /**
     *  get and set the total budget value
     *  is displayed ongui
     */
    private void getTotalBudget(){
        BigDecimal sum = sql.getSumTotal();
        
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
            java.util.logging.Logger.getLogger(CostBurden.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            CostBurden burden = new CostBurden();
            burden.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            burden.setLocationRelativeTo(null);
            burden.setResizable(false);
            burden.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JLabel amount;
    private javax.swing.JTextField amountBox;
    private javax.swing.JLabel budget;
    private javax.swing.JLabel daily;
    private javax.swing.JLabel date;
    private javax.swing.JTextArea dispayTextArea;
    private javax.swing.JTable displayBudget;
    public static javax.swing.JLabel displayBudgetAmount;
    private javax.swing.JLabel displayDaily;
    private javax.swing.JLabel displayHourly;
    private javax.swing.JLabel displayNumOfOpps;
    private javax.swing.JLabel displayTotal;
    private javax.swing.JLabel displayWeekly;
    private javax.swing.JLabel displayYearly;
    private javax.swing.JButton enter;
    private javax.swing.JLabel expense;
    private javax.swing.JTextField expenseBox;
    private javax.swing.JComboBox<String> expenseList;
    private javax.swing.JPanel extPanel;
    private javax.swing.JLabel hourly;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScroll_TArea;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton more;
    private javax.swing.JProgressBar needWorking;
    private javax.swing.JLabel notes;
    private javax.swing.JTextField notesBox;
    private javax.swing.JLabel oppsToday;
    private javax.swing.JButton putOption;
    private javax.swing.JButton removeAccount;
    private javax.swing.JLabel savings;
    private javax.swing.JProgressBar savingsBar;
    private javax.swing.JTextField savingsBox;
    private javax.swing.JProgressBar timeScale;
    private javax.swing.JLabel toBeFree;
    private javax.swing.JLabel total;
    private javax.swing.JToggleButton totalRequired;
    private javax.swing.JLabel weekly;
    private javax.swing.JLabel yearly;
    // End of variables declaration//GEN-END:variables
}
