package test1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Administrator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable studentTable;
    private JTable instructorTable;
    private JTable moduleTable;
    private JTable courseTable;
    private connect con;

    public static void NewScreen() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Administrator frame = new Administrator();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Administrator() {
        con = new connect();
        con.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        setTitle("Administrator Panel");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contentPane.add(buttonsPanel, BorderLayout.NORTH);

        JButton addCourseButton = new JButton("Add Course");
        buttonsPanel.add(addCourseButton);

        JButton addModuleButton = new JButton("Add Module");
        buttonsPanel.add(addModuleButton);

        JButton assignModuleButton = new JButton("Assign Module to Instructor");
        buttonsPanel.add(assignModuleButton);
        
        JButton deleteInstructorButton = new JButton("Delete Instructor");

        buttonsPanel.add(deleteInstructorButton);



        deleteInstructorButton.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                deleteInstructor();

            }

        });



        JButton cancelCourseButton = new JButton("Cancel Course");
        buttonsPanel.add(cancelCourseButton);

        JButton deleteCourseButton = new JButton("Delete Course");
        buttonsPanel.add(deleteCourseButton);
        
        JButton deleteModuleButton = new JButton("Delete Module"); 
        buttonsPanel.add(deleteModuleButton);    
        deleteModuleButton.addActionListener(new ActionListener() { // ActionListener for Delete Module Button

            public void actionPerformed(ActionEvent e) {

                deleteModule(); // Method to delete a module

            }

        });



        JButton generateReportButton = new JButton("Generate Report");
        buttonsPanel.add(generateReportButton);
        
        generateReportButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

            Report report = new Report();

            report.setVisible(true);

                //generateReport();

            }

        });

        JButton btnLogOut = new JButton("Log Out");
        btnLogOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login.NewScreen();
            }
        });
        buttonsPanel.add(btnLogOut);

        addCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCourse();
            }
        });

        addModuleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addModule();
            }
        });

        assignModuleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignModuleToInstructor();
            }
        });

        cancelCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelCourse();
            }
        });

        deleteCourseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCourse();
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        JPanel tablesPanel = new JPanel(new GridLayout(1, 3));
        contentPane.add(tablesPanel, BorderLayout.CENTER);

        studentTable = new JTable();
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder("Students"));
        tablesPanel.add(studentScrollPane);

        instructorTable = new JTable();
        JScrollPane instructorScrollPane = new JScrollPane(instructorTable);
        instructorScrollPane.setBorder(BorderFactory.createTitledBorder("Instructors"));
        tablesPanel.add(instructorScrollPane);

        moduleTable = new JTable();
        JScrollPane moduleScrollPane = new JScrollPane(moduleTable);
        moduleScrollPane.setBorder(BorderFactory.createTitledBorder("Modules"));
        tablesPanel.add(moduleScrollPane);

        courseTable = new JTable();
        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        courseScrollPane.setBorder(BorderFactory.createTitledBorder("Courses"));
        tablesPanel.add(courseScrollPane);

        populateTables();
    }

    private void populateTables() {
        populateStudentsTable();
        populateInstructorsTable();
        populateModulesTable();
        populateCoursesTable();
    }

 private void populateCoursesTable() {
     try {
         Connection conn = con.connect();
         String query = "SELECT CourseID, CourseName FROM courses";
         PreparedStatement pst = conn.prepareStatement(query);
         ResultSet rs = pst.executeQuery();

         DefaultTableModel courseModel = new DefaultTableModel();
         courseModel.addColumn("Course ID");
         courseModel.addColumn("Course Name");

         while (rs.next()) {
             courseModel.addRow(new Object[]{
                     rs.getInt("CourseID"),
                     rs.getString("CourseName")
             });
         }

         courseTable.setModel(courseModel);

         rs.close();
         pst.close();
         conn.close();
     } catch (SQLException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
     }
 }

    private void populateStudentsTable() {
        try {
            Connection conn = con.connect();
            String query = "SELECT UserID, Name, Email FROM users WHERE Role = 'Student'";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel studentModel = new DefaultTableModel();
            studentModel.addColumn("Student ID");
            studentModel.addColumn("Name");
            studentModel.addColumn("Email");

            while (rs.next()) {
                studentModel.addRow(new Object[]{
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Email")
                });
            }

            studentTable.setModel(studentModel);

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateInstructorsTable() {
        try {
            Connection conn = con.connect();
            String query = "SELECT InstructorID, Name, Email FROM instructors"; // Adjusted query to select from the 'instructors' table
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel instructorModel = new DefaultTableModel();
            instructorModel.addColumn("Instructor ID");
            instructorModel.addColumn("Name");
            instructorModel.addColumn("Email");

            while (rs.next()) {
                instructorModel.addRow(new Object[]{
                        rs.getInt("InstructorID"), // Adjusted column name
                        rs.getString("Name"),
                        rs.getString("Email")
                });
            }

            instructorTable.setModel(instructorModel);

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void populateModulesTable() {
        try {
            Connection conn = con.connect();
            String query = "SELECT m.ModuleID, m.ModuleName, m.Level, c.CourseName " +
                    "FROM modules m " +
                    "JOIN courses c ON m.CourseID = c.CourseID";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel moduleModel = new DefaultTableModel();
            moduleModel.addColumn("Module ID");
            moduleModel.addColumn("Module Name");
            moduleModel.addColumn("Level");
            moduleModel.addColumn("Course");

            while (rs.next()) {
                moduleModel.addRow(new Object[]{
                        rs.getInt("ModuleID"),
                        rs.getString("ModuleName"),
                        rs.getInt("Level"),
                        rs.getString("CourseName")
                });
            }

            moduleTable.setModel(moduleModel);

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCourse() {
        String courseName = JOptionPane.showInputDialog(this, "Enter Course Name:");
        if (courseName != null && !courseName.isEmpty()) {
            try {
                Connection conn = con.connect();
                PreparedStatement pst = conn.prepareStatement("INSERT INTO courses(CourseName) VALUES(?)");
                pst.setString(1, courseName);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Course Added Successfully.");
                    populateTables();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add course.");
                }
                pst.close();
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addModule() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (int) courseTable.getValueAt(selectedRow, 0);

            String moduleName = JOptionPane.showInputDialog(this, "Enter Module Name:");
            String moduleLevel = JOptionPane.showInputDialog(this, "Enter Module Level (4, 5, 6):");
            String isMandatoryStr = JOptionPane.showInputDialog(this, "Is the Module Mandatory? (true/false):");
            boolean isMandatory = Boolean.parseBoolean(isMandatoryStr);

            if (moduleName != null && !moduleName.isEmpty() && moduleLevel != null && !moduleLevel.isEmpty()) {
                try {
                    Connection conn = con.connect();
                    PreparedStatement pst = conn.prepareStatement("INSERT INTO modules(CourseID, ModuleName, Level, Mandatory) VALUES (?, ?, ?, ?)");
                    pst.setInt(1, courseId);
                    pst.setString(2, moduleName);
                    pst.setString(3, moduleLevel);
                    pst.setBoolean(4, isMandatory);

                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Module Added Successfully.");
                        populateTables();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add module.");
                    }

                    pst.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Module Name, Description, Level, and Mandatory Status are required.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to add module.");
        }
    }
    private void assignModuleToInstructor() {
        // Get the selected row from the instructor table
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow != -1) {
            int instructorID = (int) instructorTable.getValueAt(selectedRow, 0);
            String instructorName = (String) instructorTable.getValueAt(selectedRow, 1);

            // Check if the instructor already has 4 modules assigned
            int assignedModulesCount = countAssignedModules(instructorID);
            if (assignedModulesCount >= 4) {
                JOptionPane.showMessageDialog(this, "Instructor " + instructorName + " already has 4 modules assigned.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Retrieve the list of available modules
                Object[] modules = retrieveAvailableModules();

                // Display a dialog to select modules
                if (modules != null && modules.length > 0) {
                    // Store selected module in instructor_modules table
                    Object selectedModule = showModuleSelectionDialog(modules);
                    if (selectedModule != null) {
                        // Parse the selected module string to extract module ID
                        String moduleInfo = (String) selectedModule;
                        int moduleID = Integer.parseInt(moduleInfo.split(":")[0].trim());

                        boolean success = storeModuleAssignment(instructorID, moduleID);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Module assigned to Instructor " + instructorName + " successfully.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to assign module to Instructor " + instructorName + ".", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No module selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No modules available.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an instructor to assign module.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] retrieveAvailableModules() {
        ArrayList<String> modules = new ArrayList<>();

        try {
            Connection conn = new connect().connect();
            String query = "SELECT ModuleID, ModuleName FROM modules";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int moduleID = rs.getInt("ModuleID");
                String moduleName = rs.getString("ModuleName");
                modules.add(moduleID + ": " + moduleName);
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Convert ArrayList<String> to Object[]
        return modules.toArray(new Object[0]);
    }

    private Object showModuleSelectionDialog(Object[] modules) {
        Object[] objectModules = Arrays.copyOf(modules, modules.length, Object[].class);
        return JOptionPane.showInputDialog(this, "Select modules to assign:", "Module Selection", JOptionPane.QUESTION_MESSAGE, null, objectModules, null);
    }

    private boolean storeModuleAssignment(int instructorID, int moduleID) {
        try {
            Connection conn = new connect().connect();
            String query = "INSERT INTO instructor_modules (InstructorID, ModuleID) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, instructorID);
            pst.setInt(2, moduleID);
            int rowsAffected = pst.executeUpdate();
            pst.close();
            conn.close();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    private int countAssignedModules(int instructorID) {
        int count = 0;
        try {
            Connection conn = con.connect();
            String query = "SELECT COUNT(*) FROM instructor_modules WHERE InstructorID = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, instructorID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;
    }

    private void cancelCourse() {
        // Logic to cancel a course
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (int) courseTable.getValueAt(selectedRow, 0);
            String courseName = (String) courseTable.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel course '" + courseName + "'?");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = con.connect();
                    PreparedStatement pst = conn.prepareStatement("UPDATE courses SET Status = 'Cancelled' WHERE CourseID = ?");
                    pst.setInt(1, courseId);
                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Course Cancelled Successfully.");
                        populateTables();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to cancel course.");
                    }
                    pst.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to cancel.");
        }
    }

    private void deleteCourse() {
        // Logic to delete a course
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (int) courseTable.getValueAt(selectedRow, 0);
            String courseName = (String) courseTable.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete course '" + courseName + "'?");
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = con.connect();
                    PreparedStatement pst = conn.prepareStatement("DELETE FROM courses WHERE CourseID = ?");
                    pst.setInt(1, courseId);
                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Course Deleted Successfully.");
                        populateTables();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete course.");
                    }
                    pst.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.");
        }
    }

    private void generateReport() {
        // Logic to generate a report
        String studentIdentifier = JOptionPane.showInputDialog(this, "Enter Student ID or Name:");
        if (studentIdentifier != null && !studentIdentifier.isEmpty()) {
            try {
                Connection conn = con.connect();
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM enrollment INNER JOIN student_courses ON enrollment.CourseID = student_courses.CourseID WHERE student_courses.StudentID = ?");
                pst.setString(1, studentIdentifier);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    StringBuilder report = new StringBuilder();
                    report.append("Report for Student: ").append(studentIdentifier).append("\n\n");
                    report.append("Courses Enrolled:\n");
                    do {
                        String courseName = rs.getString("CourseName");
                        String grade = rs.getString("Grade");
                        report.append("- ").append(courseName).append(": ").append(grade).append("\n");
                    } while (rs.next());
                    JOptionPane.showMessageDialog(this, report.toString(), "Student Report", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
                rs.close();
                pst.close();
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Student ID or Name is required.");
        }
    }
    private void deleteInstructor() {

        int selectedRow = instructorTable.getSelectedRow();

        if (selectedRow != -1) {

            int instructorID = (int) instructorTable.getValueAt(selectedRow, 0);

            String instructorName = (String) instructorTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete instructor '" + instructorName + "'?");

            if (confirm == JOptionPane.YES_OPTION) {

                try {

                    Connection conn = con.connect();

                    PreparedStatement pst = conn.prepareStatement("DELETE FROM instructors WHERE InstructorID = ?");

                    pst.setInt(1, instructorID);

                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {

                        JOptionPane.showMessageDialog(this, "Instructor Deleted Successfully.");

                        populateInstructorsTable(); // Refresh instructor table

                    } else {

                        JOptionPane.showMessageDialog(this, "Failed to delete instructor.");

                    }

                    pst.close();

                    conn.close();

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

                }

            }

        } else {

            JOptionPane.showMessageDialog(this, "Please select an instructor to delete.");

        }

    }
    private void deleteModule() {

        int selectedRow = moduleTable.getSelectedRow();

        if (selectedRow != -1) {

            int moduleID = (int) moduleTable.getValueAt(selectedRow, 0);

            String moduleName = (String) moduleTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete module '" + moduleName + "'?");

            if (confirm == JOptionPane.YES_OPTION) {

                try {

                    Connection conn = con.connect();

                    PreparedStatement pst = conn.prepareStatement("DELETE FROM modules WHERE ModuleID = ?");

                    pst.setInt(1, moduleID);

                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {

                        JOptionPane.showMessageDialog(this, "Module Deleted Successfully.");

                        populateTables();

                    } else {

                        JOptionPane.showMessageDialog(this, "Failed to delete module.");

                    }

                    pst.close();

                    conn.close();

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

                }

            }

        } else {

            JOptionPane.showMessageDialog(this, "Please select a module to delete.");

        }

    }
   
}
