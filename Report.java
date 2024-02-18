package test1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Report extends JFrame {

    private JTable reportTable;
    private JTextField studentIDField;
    private JButton generateReportButton;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Report frame = new Report();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Report() {
        setTitle("Report Generator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        // Panel for student ID input and button
        JPanel inputPanel = new JPanel(new FlowLayout());
        studentIDField = new JTextField(10);
        inputPanel.add(new JLabel("Enter Student ID: "));
        inputPanel.add(studentIDField);

        generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        inputPanel.add(generateReportButton);

        contentPane.add(inputPanel, BorderLayout.NORTH);

        reportTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(reportTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setContentPane(contentPane);
        setVisible(true);
    }

    public void generateReport() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Student Name");
        model.addColumn("Module");
        model.addColumn("Grade");

        try {
            Connection conn = new connect().connect();

            // Retrieve student name based on student ID
            String studentNameQuery = "SELECT Name FROM student_course WHERE Student_ID = ?";
            PreparedStatement studentNameStmt = conn.prepareStatement(studentNameQuery);
            studentNameStmt.setString(1, studentIDField.getText());
            ResultSet studentNameResult = studentNameStmt.executeQuery();

            String studentName = "";
            if (studentNameResult.next()) {
                studentName = studentNameResult.getString("Name");
            }

            // Prepare query to retrieve modules and grades for the student
            String query = "SELECT Module_Name, Grade " +
                    "FROM student_modules " +
                    "WHERE Student_ID = ? AND Grade IS NOT NULL";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, studentIDField.getText());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        studentName,
                        rs.getString("Module_Name"),
                        rs.getString("Grade")
                });
            }

            reportTable.setModel(model);

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}