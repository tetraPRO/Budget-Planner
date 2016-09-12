import java.awt.event.ItemEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.proteanit.sql.DbUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author capphil1
 */
public class CostBurden extends javax.swing.JFrame {

    SQLData sql;
    boolean isXpanelOpen = false;
    boolean isFirstPage = false;
    boolean isTotalReqOn = false;
    
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
        setRequired();
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
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Sets the date label to todays date.
     */
    private void setDateLabel(){
        GregorianCalendar gcal = new GregorianCalendar();
        int year = gcal.get(Calendar.YEAR);
        int month = gcal.get(Calendar.MONTH);
        int day = gcal.get(Calendar.DAY_OF_MONTH);
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
        GregorianCalendar gcal = new GregorianCalendar();
        int day = gcal.get(Calendar.DAY_OF_MONTH);
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
        ArrayList<String> accounts = sql.getAccounts();
        for(int i =0; i<accounts.size();i++){
            expenseList.addItem(accounts.get(i));
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Set jlabel to the required amount of capital needed to trade
     * on every jpanel 
     */
    private void setRequired(){
        String budgetAmount = displayBudgetAmount.getText();
        if(budgetAmount.isEmpty()){
            //requiredCapital.setText("");
        }else{
            BigDecimal budgetA= new BigDecimal(budgetAmount);
            BigDecimal quoteint = budgetA.divide(new BigDecimal(".05"), 2, RoundingMode.HALF_UP);
            //requiredCapital.setText(getCurrencyDisplay(quoteint));
           
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Takes bigdecimal amount in and formats it to a currency display
     * @param amount bigdecimal 
     * @return string formated as a currency
     */
    private String getCurrencyDisplay(BigDecimal amount){
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(amount);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //get one-by-one each amount of every expense and get the required capital for each and add up
    /**
     * 
     */
    private void setFrameSize(){
        if(isXpanelOpen){//it s already open
            extPanel.setVisible(false);//make invisible
             this.setSize(370, 521);//set to smaller size
            isXpanelOpen = false;//close panel
        }else{//panel must be closed
            extPanel.setVisible(true);//make visible
              this.setSize(725, 521);//set to larger size
            isXpanelOpen = true;//open panel
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Create a tabbed pane for every account in the budget
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
        ArrayList<String> accounts = sql.getAccounts();
        int i = 0;
        while(i < accounts.size()){
            JPanel nextPanel = new TabbedPanel();
            jTabbedPane1.addTab( accounts.get(i), nextPanel);
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
        expenseList = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        budget = new javax.swing.JLabel();
        displayBudgetAmount = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        outOfRatRace = new javax.swing.JProgressBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        savingsBox = new javax.swing.JTextField();
        savings = new javax.swing.JLabel();
        enter = new javax.swing.JButton();
        totalRequired = new javax.swing.JToggleButton();
        toBeFree = new javax.swing.JLabel();
        whereToLook = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Receipt Challenge");

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

        budget.setText("You have budgeted:");

        displayBudgetAmount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        outOfRatRace.setToolTipText("Percent of Total Required Capital to be FREE of the Rat Race!");
        outOfRatRace.setStringPainted(true);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

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

        whereToLook.setText("Where to Look");
        whereToLook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                whereToLookActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(toBeFree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 113, Short.MAX_VALUE)
                        .addComponent(savings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(savingsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(enter))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(totalRequired, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(whereToLook)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(whereToLook)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                .addComponent(toBeFree, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalRequired)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(savings)
                    .addComponent(savingsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enter))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Savings", jPanel1);

        javax.swing.GroupLayout extPanelLayout = new javax.swing.GroupLayout(extPanel);
        extPanel.setLayout(extPanelLayout);
        extPanelLayout.setHorizontalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(timeScale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addComponent(outOfRatRace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(budget, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(displayBudgetAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expenseList, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        extPanelLayout.setVerticalGroup(
            extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extPanelLayout.createSequentialGroup()
                .addComponent(timeScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expenseList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(extPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(budget, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(displayBudgetAmount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outOfRatRace, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        expenseList.addItemListener((ItemEvent ie) -> {
            //add amount to displayBudgetAmount...
            if(ie.getStateChange()   == ItemEvent.SELECTED){
                BigDecimal amountOfBudget = sql.getAmountOfBudget(expenseList.getSelectedItem().toString());
                displayBudgetAmount.setText(amountOfBudget.toString());
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(yearly)
                            .addComponent(total)
                            .addComponent(weekly)
                            .addComponent(daily)
                            .addComponent(hourly))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(displayYearly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(displayWeekly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(displayDaily, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(displayHourly, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(displayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScroll_TArea)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(displayTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(total))
                                .addGap(9, 9, 9)
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
                                    .addComponent(hourly))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(extPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
        );

        extPanel.setVisible(false);

        pack();
    }// </editor-fold>                        

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
        
        loadAccounts();//load new account into combo box
        //create a tabbedpane for each account of budget.
       createTabs(bill);
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

    private void totalRequiredActionPerformed(java.awt.event.ActionEvent evt) {                                              
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
    }                                             

    private void enterActionPerformed(java.awt.event.ActionEvent evt) {                                      
        BigDecimal quoteint = sql.getSumTotal().divide(new  BigDecimal(".05"), 2, RoundingMode.HALF_UP);
        BigDecimal diff = quoteint.subtract(new BigDecimal(savingsBox.getText()));
        BigDecimal percent = BigDecimal.ONE.subtract(diff.divide(quoteint, 2, RoundingMode.HALF_UP));
        BigDecimal timesHundred = percent.multiply(new BigDecimal("100"));
        outOfRatRace.setValue(timesHundred.intValue());
        savingsBox.setText("");
    }                                     
    /**
     *  Deletes selected records from table
     * @param evt 
     */
    private void removeAccountActionPerformed(java.awt.event.ActionEvent evt) {                                              
        JPanel deletePanel = new DeleteRecord();
        String[] buttons = {"Delete"};
         int showConfirmDialog = JOptionPane.showOptionDialog(this,  deletePanel,"Delete Record" , 
                JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, buttons[0]);
         String accountToRemove = DeleteRecord.accountsBox.getSelectedItem().toString();
         sql.deleteExpense(accountToRemove);
         displayBudget.setModel(DbUtils.resultSetToTableModel(sql.updateTable("Budget")));
         getTotalBudget();         
    }                                             

    private void whereToLookActionPerformed(java.awt.event.ActionEvent evt) {                                            
                            
             //try jsoup parser...
         try{ 
             URL url = new URL("http://www.barchart.com/options/highvol");
            Document doc = Jsoup.parse(url, 8000);
            Elements links =doc.getElementsByTag("table"); System.out.println(links.size());
            Element table = links.get(3);System.out.println(table);
            
         }catch(IOException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }                                           
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

    // Variables declaration - do not modify                     
    private javax.swing.JButton add;
    private javax.swing.JLabel amount;
    private javax.swing.JTextField amountBox;
    private javax.swing.JLabel budget;
    private javax.swing.JLabel daily;
    private javax.swing.JLabel date;
    private javax.swing.JTextArea dispayTextArea;
    private javax.swing.JTable displayBudget;
    private javax.swing.JLabel displayBudgetAmount;
    private javax.swing.JLabel displayDaily;
    private javax.swing.JLabel displayHourly;
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
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton more;
    private javax.swing.JLabel notes;
    private javax.swing.JTextField notesBox;
    private javax.swing.JProgressBar outOfRatRace;
    private javax.swing.JButton removeAccount;
    private javax.swing.JLabel savings;
    private javax.swing.JTextField savingsBox;
    private javax.swing.JProgressBar timeScale;
    private javax.swing.JLabel toBeFree;
    private javax.swing.JLabel total;
    private javax.swing.JToggleButton totalRequired;
    private javax.swing.JLabel weekly;
    private javax.swing.JButton whereToLook;
    private javax.swing.JLabel yearly;
    // End of variables declaration                   
}
