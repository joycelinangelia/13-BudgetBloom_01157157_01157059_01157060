import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calendar extends JFrame {

    private JPanel calendarPanel;
    private JLabel monthYearLabel;
    private JComboBox<String> monthComboBox;
    private JTextField yearTextField;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel totalLabel;
    private List<Transaction> transactions;

    public Calendar() {
        transactions = new ArrayList<>();

        setTitle("Budget Bloom");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top section
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Serif", Font.BOLD, 24));
        topPanel.add(monthYearLabel);

        JPanel controlPanel = new JPanel();
        monthComboBox = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        controlPanel.add(monthComboBox);

        monthComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCalendar();
            }
        });

        yearTextField = new JTextField(5);
        controlPanel.add(yearTextField);

        yearTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!yearTextField.getText().isEmpty()) {
                    updateCalendar();
                }
            }
        });

        topPanel.add(controlPanel);

        // Income, Expenses, Total section
        JPanel financialPanel = new JPanel(new GridLayout(1, 6));
        JLabel incomeTextLabel = new JLabel("Income:", SwingConstants.RIGHT);
        incomeLabel = new JLabel("0.00");
        JLabel expenseTextLabel = new JLabel("Expenses:", SwingConstants.RIGHT);
        expenseLabel = new JLabel("0.00");
        JLabel totalTextLabel = new JLabel("Total:", SwingConstants.RIGHT);
        totalLabel = new JLabel("0.00");

        financialPanel.add(incomeTextLabel);
        financialPanel.add(incomeLabel);
        financialPanel.add(expenseTextLabel);
        financialPanel.add(expenseLabel);
        financialPanel.add(totalTextLabel);
        financialPanel.add(totalLabel);

        topPanel.add(financialPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Calendar section
        calendarPanel = new JPanel(new GridLayout(6, 7));
        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        // Add '+' button
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Serif", Font.BOLD, 24));
        addButton.setPreferredSize(new Dimension(50, 50));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLogWindow();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        add(mainPanel);

        // Set current month and year
        LocalDate now = LocalDate.now();
        monthComboBox.setSelectedIndex(now.getMonthValue() - 1);
        yearTextField.setText(String.valueOf(now.getYear()));

        updateCalendar();
    }

    private void updateCalendar() {
        int month = monthComboBox.getSelectedIndex() + 1;
        String yearText = yearTextField.getText();

        if (yearText.isEmpty()) {
            return; // Do nothing if year is empty
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year format. Please enter a valid year.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update calendar title
        YearMonth yearMonth = YearMonth.of(year, month);
        String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        monthYearLabel.setText("Budget Bloom");

        // Calculate income, expenses, and total
        double income = 0;
        double expenses = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year && transaction.getDate().getMonthValue() == month) {
                if (transaction.isIncome()) {
                    income += transaction.getAmount();
                } else {
                    expenses += transaction.getAmount();
                }
            }
        }
        double total = income - expenses;
        incomeLabel.setText(String.format("%.2f", income));
        expenseLabel.setText(String.format("%.2f", expenses));
        totalLabel.setText(String.format("%.2f", total));

        // Clear and populate calendar grid
        calendarPanel.removeAll();
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setBorder(new LineBorder(Color.BLACK));
            calendarPanel.add(dayLabel);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        // Adjust to make Sunday = 0
        firstDayOfWeek = (firstDayOfWeek == 7) ? 0 : firstDayOfWeek;

        // Fill initial empty cells
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // Fill the calendar with days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.LEFT);
            dayLabel.setBorder(new LineBorder(Color.BLACK));
            calendarPanel.add(dayLabel);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void openLogWindow() {
        LogWindow logWindow = new LogWindow(this);
        logWindow.setVisible(true);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateCalendar();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calendar calendar = new Calendar();
            calendar.setVisible(true);
        });
    }
}

// Simple implementation of LogWindow class for completeness
class LogWindow extends JFrame {
    public LogWindow(Calendar calendar) {
        setTitle("Log Transaction");
        setSize(300, 200);
        setLocationRelativeTo(calendar);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Log transaction details here");
        add(label, BorderLayout.CENTER);

        // Add more components and logic as needed for logging transactions
    }
}
