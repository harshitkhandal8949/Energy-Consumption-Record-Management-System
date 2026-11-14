
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EnergyManagementSystem extends JFrame {

    private static final String URL =
            "jdbc:mysql://localhost:3306/energy_management";
    private static final String USER = "root";
    private static final String PASSWORD = "yogesh@2004";

    private JTextField nameField, meterField, monthField;
    private JTextField unitsField, rateField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalRecords, totalUnits, totalCost;

    private final Color navy = new Color(20, 36, 66);
    private final Color blue = new Color(37, 99, 235);
    private int selectedId = -1;

    public EnergyManagementSystem() {
        setTitle("Energy Consumption Record Management System");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(new Color(245, 247, 251));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        main.add(createDashboard(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.add(createForm(), BorderLayout.WEST);
        content.add(createRecordsPanel(), BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        loadRecords();
        updateDashboard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("⚡ Energy Manager");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel subtitle = new JLabel("ENERGY RECORD MANAGEMENT");
        subtitle.setForeground(new Color(190, 210, 240));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(180, 0));
        side.setBackground(new Color(27, 43, 73));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(25, 12, 20, 12));

        JLabel heading = new JLabel("MAIN MENU");
        heading.setForeground(new Color(175, 195, 225));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 10, 18, 0));
        side.add(heading);

        side.add(menuButton("Dashboard", () -> updateDashboard()));
        side.add(Box.createVerticalStrut(10));
        side.add(menuButton("All Records", () -> loadRecords()));
        side.add(Box.createVerticalStrut(10));
        side.add(menuButton("New Record", () -> clearForm()));

        side.add(Box.createVerticalGlue());

        JLabel footer = new JLabel(
                "<html><center>Energy Management<br>Java + MySQL</center></html>"
        );
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(footer);

        return side;
    }

    private JButton menuButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(160, 42));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(43, 61, 95));
        b.setBorderPainted(false);
        b.addActionListener(e -> action.run());
        return b;
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        totalRecords = new JLabel("0");
        totalUnits = new JLabel("0");
        totalCost = new JLabel("₹0.00");

        panel.add(statCard("Total Records", totalRecords,
                new Color(37, 99, 235)));
        panel.add(statCard("Units Consumed (kWh)", totalUnits,
                new Color(5, 150, 105)));
        panel.add(statCard("Estimated Cost", totalCost,
                new Color(217, 119, 6)));

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return panel;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(15, 18, 15, 12)
        ));

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.GRAY);
        heading.setFont(new Font("SansSerif", Font.PLAIN, 13));

        value.setForeground(accent);
        value.setFont(new Font("SansSerif", Font.BOLD, 25));

        card.add(heading);
        card.add(Box.createVerticalStrut(10));
        card.add(value);
        return card;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(265, 0));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 240)),
                BorderFactory.createEmptyBorder(15, 14, 15, 14)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(5, 0, 5, 0);

        JLabel heading = new JLabel("Energy Record Form");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.gridy = 0;
        form.add(heading, g);

        nameField = addField(form, g, 1, "Consumer Name");
        meterField = addField(form, g, 3, "Meter Number");
        monthField = addField(form, g, 5, "Month (e.g. Oct-2026)");
        unitsField = addField(form, g, 7, "Units Consumed (kWh)");
        rateField = addField(form, g, 9, "Rate per Unit (₹)");

        JButton add = actionButton("Add Record", blue);
        JButton update = actionButton("Update Selected", new Color(5, 150, 105));
        JButton delete = actionButton("Delete Selected", new Color(220, 60, 60));
        JButton clear = actionButton("Clear Form", new Color(100, 116, 139));

        g.gridy = 11;
        form.add(add, g);
        g.gridy = 12;
        form.add(update, g);
        g.gridy = 13;
        form.add(delete, g);
        g.gridy = 14;
        form.add(clear, g);

        add.addActionListener(e -> saveRecord());
        update.addActionListener(e -> updateRecord());
        delete.addActionListener(e -> deleteRecord());
        clear.addActionListener(e -> clearForm());

        return form;
    }

    private JTextField addField(JPanel panel, GridBagConstraints g,
                                int y, String label) {
        g.gridy = y;
        JLabel l = new JLabel(label);
        l.setForeground(new Color(60, 70, 90));
        panel.add(l, g);

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(220, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 222, 235)),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        g.gridy = y + 1;
        panel.add(field, g);
        return field;
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);

        JLabel title = new JLabel("Consumption Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        top.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(145, 32));
        JButton find = actionButton("Search", blue);
        JButton refresh = actionButton("Refresh", new Color(100, 116, 139));

        search.add(searchField, BorderLayout.CENTER);
        search.add(find, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actions.setOpaque(false);
        actions.add(search);
        actions.add(refresh);

        top.add(actions, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Consumer", "Meter", "Month",
                "Units (kWh)", "Rate (₹)", "Cost (₹)"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {40, 130, 90, 100, 90, 75, 90};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        find.addActionListener(e -> searchRecords());
        refresh.addActionListener(e -> {
            searchField.setText("");
            loadRecords();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                meterField.setText(model.getValueAt(row, 2).toString());
                monthField.setText(model.getValueAt(row, 3).toString());
                unitsField.setText(model.getValueAt(row, 4).toString());
                rateField.setText(model.getValueAt(row, 5).toString());
            }
        });

        return panel;
    }

    private JButton actionButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 34));
        return b;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()
                || meterField.getText().trim().isEmpty()
                || monthField.getText().trim().isEmpty()
                || unitsField.getText().trim().isEmpty()
                || rateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return false;
        }

        try {
            double units = Double.parseDouble(unitsField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());

            if (!Double.isFinite(units) || !Double.isFinite(rate)
                    || units < 0 || rate < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Units and rate must be valid non-negative numbers."
            );
            return false;
        }

        return true;
    }

    private void saveRecord() {
        if (!validateForm()) return;

        String sql = "INSERT INTO energy_records "
                + "(consumer_name, meter_number, record_month, "
                + "units_consumed, rate_per_unit, total_cost) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = connect();
             PreparedStatement p = c.prepareStatement(sql)) {

            double units = Double.parseDouble(unitsField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());

            p.setString(1, nameField.getText().trim());
            p.setString(2, meterField.getText().trim());
            p.setString(3, monthField.getText().trim());
            p.setDouble(4, units);
            p.setDouble(5, rate);
            p.setDouble(6, units * rate);
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Record added successfully.");
            clearForm();
            loadRecords();
            updateDashboard();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a record first.");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE energy_records SET consumer_name=?, "
                + "meter_number=?, record_month=?, units_consumed=?, "
                + "rate_per_unit=?, total_cost=? WHERE id=?";

        try (Connection c = connect();
             PreparedStatement p = c.prepareStatement(sql)) {

            double units = Double.parseDouble(unitsField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());

            p.setString(1, nameField.getText().trim());
            p.setString(2, meterField.getText().trim());
            p.setString(3, monthField.getText().trim());
            p.setDouble(4, units);
            p.setDouble(5, rate);
            p.setDouble(6, units * rate);
            p.setInt(7, selectedId);
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Record updated.");
            clearForm();
            loadRecords();
            updateDashboard();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a record first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete the selected record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection c = connect();
             PreparedStatement p = c.prepareStatement(
                     "DELETE FROM energy_records WHERE id=?")) {

            p.setInt(1, selectedId);
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Record deleted.");
            clearForm();
            loadRecords();
            updateDashboard();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadRecords() {
        fetchRecords("");
    }

    private void searchRecords() {
        fetchRecords(searchField.getText().trim());
    }

    private void fetchRecords(String query) {
        model.setRowCount(0);

        String sql = "SELECT * FROM energy_records "
                + "WHERE consumer_name LIKE ? OR meter_number LIKE ? "
                + "ORDER BY id DESC";

        try (Connection c = connect();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, "%" + query + "%");
            p.setString(2, "%" + query + "%");

            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("consumer_name"),
                            rs.getString("meter_number"),
                            rs.getString("record_month"),
                            rs.getDouble("units_consumed"),
                            rs.getDouble("rate_per_unit"),
                            rs.getDouble("total_cost")
                    });
                }
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateDashboard() {
        String sql = "SELECT COUNT(*) AS records, "
                + "COALESCE(SUM(units_consumed),0) AS units, "
                + "COALESCE(SUM(total_cost),0) AS cost FROM energy_records";

        try (Connection c = connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            if (rs.next()) {
                totalRecords.setText(rs.getString("records"));
                totalUnits.setText(String.format(
                        "%.2f", rs.getDouble("units")
                ));
                totalCost.setText(String.format(
                        "₹%.2f", rs.getDouble("cost")
                ));
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        nameField.setText("");
        meterField.setText("");
        monthField.setText("");
        unitsField.setText("");
        rateField.setText("");
        selectedId = -1;

        if (table != null) table.clearSelection();
    }

    private void showError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage()
                        + "\nCheck MySQL, database name, username and password.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {}

            new EnergyManagementSystem().setVisible(true);
        });
    }
}
