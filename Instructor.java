package test1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Instructor extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private int instructorID; // Removed static keyword

    // Updated NewScreen method to accept instructorID as parameter
    public static void NewScreen(int instructorID) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Instructor frame = new Instructor(instructorID);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Instructor(int instructorID) { // Removed static keyword from constructor
        this.instructorID = instructorID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTitle = new JLabel("Instructor Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(lblTitle);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        JPanel modulePanel = new JPanel();
        modulePanel.setLayout(new BorderLayout());
        modulePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblAssignedModules = new JLabel("Assigned Modules:");
        lblAssignedModules.setFont(new Font("Arial", Font.BOLD, 14));
        modulePanel.add(lblAssignedModules, BorderLayout.NORTH);

        DefaultListModel<String> moduleListModel = new DefaultListModel<>();
        JList<String> moduleList = new JList<>(moduleListModel);
        moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane moduleScrollPane = new JScrollPane(moduleList);
        modulePanel.add(moduleScrollPane, BorderLayout.CENTER);
        contentPane.add(modulePanel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionsPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnShowStudents = new JButton("Show Students");
        btnShowStudents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedModule = moduleList.getSelectedValue();
                if (selectedModule != null) {
                    displayStudentList(selectedModule); // Pass the module name as a String
                } else {
                    JOptionPane.showMessageDialog(Instructor.this, "Please select a module.");
                }
            }
        });
        actionsPanel.add(btnShowStudents);

        JButton btnAssignGrade = new JButton("Assign Grade");
        btnAssignGrade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedModule = moduleList.getSelectedValue();
                if (selectedModule != null) {
                    // Prompt the user to enter the student ID and grade
                    String studentID = JOptionPane.showInputDialog(Instructor.this, "Enter Student ID:");
                    String grade = JOptionPane.showInputDialog(Instructor.this, "Enter Grade:");

                    // Check if the user entered a student ID and a grade
                    if (studentID != null && grade != null) {
                        // Assign the grade
                        assignGrade(studentID, selectedModule, grade);
                    } else {
                        JOptionPane.showMessageDialog(Instructor.this, "Please enter both Student ID and Grade.");
                    }
                } else {
                    JOptionPane.showMessageDialog(Instructor.this, "Please select a module.");
                }
            }
        });
        actionsPanel.add(btnAssignGrade);


        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login.NewScreen();
            }
        });
        actionsPanel.add(btnLogout);

        contentPane.add(actionsPanel, BorderLayout.CENTER);

        displayAssignedModules();
    }
    private void displayAssignedModules() {
        try {
            Connection conn = new connect().connect();
            String query = "SELECT m.ModuleName " +
                           "FROM modules m " +
                           "JOIN instructor_modules im ON m.ModuleID = im.ModuleID " +
                           "WHERE im.InstructorID = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, instructorID);
            ResultSet rs = pst.executeQuery();

            DefaultListModel<String> moduleListModel = new DefaultListModel<>();
            while (rs.next()) {
                String moduleName = rs.getString("ModuleName");
                moduleListModel.addElement(moduleName);
            }

            // Retrieve the existing module list component
            JPanel modulePanel = (JPanel) contentPane.getComponent(1);
            JScrollPane moduleScrollPane = (JScrollPane) modulePanel.getComponent(1);
            JList<String> moduleList = (JList<String>) moduleScrollPane.getViewport().getView();

            // Update the existing module list model with new data
            moduleList.setModel(moduleListModel);

            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve assigned modules from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private String getModuleName(int moduleID) throws SQLException {
        String moduleName = "";
        Connection conn = new connect().connect();
        String query = "SELECT ModuleName FROM modules WHERE ModuleID = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, moduleID);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            moduleName = rs.getString("ModuleName");
        }
        rs.close();
        pst.close();
        conn.close();
        return moduleName;
    }

    private void displayStudentList(String moduleName) {
        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        try {
            Connection conn = new connect().connect();
            // Retrieve the module ID using the module name
            int moduleID = getModuleID(moduleName);
            String query = "SELECT sc.Name " +
                           "FROM student_modules sm " +
                           "INNER JOIN student_course sc ON sm.Student_ID = sc.Student_ID " +
                           "WHERE sm.Module_Name = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, moduleName); // Use the module name as the parameter
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                studentListModel.addElement(rs.getString("Name"));
            }
            JPanel contentPanel = (JPanel) contentPane.getComponent(2);
            JScrollPane studentScrollPane = null;
            Component[] components = contentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JScrollPane && comp.getName() != null && comp.getName().equals("studentScrollPane")) {
                    studentScrollPane = (JScrollPane) comp;
                    break;
                }
            }
            if (studentScrollPane != null) {
                JList<String> studentList = new JList<>(studentListModel);
                studentScrollPane.setViewportView(studentList);
            } else {
                studentScrollPane = new JScrollPane(new JList<>(studentListModel));
                studentScrollPane.setName("studentScrollPane");
                contentPanel.add(studentScrollPane);
            }
            contentPanel.revalidate();
            contentPanel.repaint();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve student list from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void assignGrade(String studentID, String moduleName, String grade) {
        try {
            Connection conn = new connect().connect();
            int moduleID = getModuleID(moduleName); // Get the module ID based on the module name
            if (moduleID != 0) { // Check if the module ID is valid
                String query = "UPDATE student_modules SET Grade = ? WHERE Student_ID = ? AND Module_Name = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, grade);
                pst.setString(2, studentID);
                pst.setString(3, moduleName);
                int rowsAffected = pst.executeUpdate();
                pst.close();
                conn.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Grade assigned successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to assign grade. Please check the student ID and module name.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid module name. Please select a valid module.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to assign grade in the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getModuleID(String moduleName) {
        int moduleID = 0;
        try {
            Connection conn = new connect().connect();
            String query = "SELECT ModuleID FROM modules WHERE ModuleName = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, moduleName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                moduleID = rs.getInt("ModuleID");
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve module ID from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return moduleID;
    }


}