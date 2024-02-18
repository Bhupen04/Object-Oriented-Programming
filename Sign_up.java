package test1;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Sign_up extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPasswordField passtxt;
    private JTextField emailtxt;
    private JTextField nametxt;
    private JComboBox<String> usertype;
    private connect con;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Sign_up frame = new Sign_up();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void NewScreen() {
        try {
            Sign_up frame = new Sign_up();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Sign_up() {
        con = new connect(); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 508);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Sign Up");
        lblNewLabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        lblNewLabel.setBounds(164, 57, 89, 37);
        contentPane.add(lblNewLabel);

        passtxt = new JPasswordField();
        passtxt.setBounds(205, 202, 96, 19);
        contentPane.add(passtxt);

        emailtxt = new JTextField();
        emailtxt.setBounds(205, 168, 96, 19);
        contentPane.add(emailtxt);
        emailtxt.setColumns(10);

        usertype = new JComboBox();
        usertype.setModel(new DefaultComboBoxModel(new String[] { "Student", "Instructor", "Admin" }));
        usertype.setBounds(205, 226, 95, 21);
        contentPane.add(usertype);

        JButton btnNewButton = new JButton("Confirm");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	signUp();	 
            }
        });


        btnNewButton.setFont(new Font("Arial", Font.BOLD, 12));
        btnNewButton.setBounds(157, 257, 96, 21);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel_2 = new JLabel("Email:");
        lblNewLabel_2.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2.setBounds(150, 171, 45, 13);
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_2_1 = new JLabel("Password:");
        lblNewLabel_2_1.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2_1.setBounds(131, 205, 74, 13);
        contentPane.add(lblNewLabel_2_1);

        JLabel lblNewLabel_2_1_2 = new JLabel("User:");
        lblNewLabel_2_1_2.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2_1_2.setBounds(164, 230, 74, 13);
        contentPane.add(lblNewLabel_2_1_2);

        JLabel lblNewLabel_3 = new JLabel("If you already have an account ");
        lblNewLabel_3.setBounds(131, 288, 204, 13);
        contentPane.add(lblNewLabel_3);

        JButton btnNewButton_1 = new JButton("Login");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	dispose();
                Login.NewScreen();
            }
        });
        btnNewButton_1.setBackground(UIManager.getColor("Button.shadow"));
        btnNewButton_1.setBounds(168, 311, 67, 19);
        contentPane.add(btnNewButton_1);

        nametxt = new JTextField();
        nametxt.setBounds(205, 135, 96, 19);
        contentPane.add(nametxt);
        nametxt.setColumns(10);

        JLabel lblNewLabel_2_3 = new JLabel("Name:");
        lblNewLabel_2_3.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2_3.setBounds(150, 138, 45, 13);
        contentPane.add(lblNewLabel_2_3);
    }
    private void signUp() {
        String name = nametxt.getText();
        String email = emailtxt.getText();
        String password = new String(passtxt.getPassword());
        String identity = (String) usertype.getSelectedItem();
        int level = 1; // Default level, change this according to your logic

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Prompt the user for the level
        String levelInput = JOptionPane.showInputDialog(this, "Enter your level:");
        if (levelInput != null && !levelInput.isEmpty()) {
            level = Integer.parseInt(levelInput);
        }

        try {
            Connection conn = con.connect();
            PreparedStatement pstUsers = conn.prepareStatement("INSERT INTO users(Name, Email, Password, Role) VALUES(?, ?, ?, ?)");
            pstUsers.setString(1, name);
            pstUsers.setString(2, email);
            pstUsers.setString(3, password);
            pstUsers.setString(4, identity);
            int rowsAffectedUsers = pstUsers.executeUpdate();
            pstUsers.close();

            if (identity.equals("Instructor")) {
                PreparedStatement pstInstructors = conn.prepareStatement("INSERT INTO instructors(Name, Email) VALUES(?, ?)");
                pstInstructors.setString(1, name);
                pstInstructors.setString(2, email);
                int rowsAffectedInstructors = pstInstructors.executeUpdate();
                pstInstructors.close();

                if (rowsAffectedUsers > 0 && rowsAffectedInstructors > 0) {
                    JOptionPane.showMessageDialog(this, "User Added Successfully.");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user.");
                }
            } else if (identity.equals("Student")) {
                String[] activeCourses = getActiveCourses(); // Get active courses from the database
                if (activeCourses.length > 0) {
                    String selectedCourse = (String) JOptionPane.showInputDialog(this, "Choose a course:", "Course Selection", JOptionPane.QUESTION_MESSAGE, null, activeCourses, activeCourses[0]);
                    if (selectedCourse != null) {
                        int selectedLevel = (int) JOptionPane.showInputDialog(this, "Choose a level:", "Level Selection", JOptionPane.QUESTION_MESSAGE, null, new Integer[]{4, 5, 6}, 4);

                        if (selectedLevel != 0) {
                            try {
                              
                                String query = "INSERT INTO student_course(Name, Course_ID, Level) VALUES(?, ?, ?)";
                                PreparedStatement pst = conn.prepareStatement(query);
                                pst.setString(1, name);
                                pst.setInt(2, getCourseID(selectedCourse));
                                pst.setInt(3, selectedLevel);
                                int rowsAffectedStudentCourse = pst.executeUpdate();
                                pst.close();

                                if (rowsAffectedStudentCourse > 0) {
                                    JOptionPane.showMessageDialog(this, "Course Selected Successfully.");
                                    // Store all modules of the selected course and level in student_modules
                                    storeAllModulesForStudent(selectedCourse, selectedLevel, name, conn);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Failed to select course.");
                                }
                                conn.close();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Error: Failed to store student course data in the database.",
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Level selection canceled.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Course selection canceled.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No active courses available.");
                }
            }
 else {
                // Insertion for other roles
                // Adjust this part based on your database schema and requirements
                if (rowsAffectedUsers > 0) {
                    JOptionPane.showMessageDialog(this, "User Added Successfully.");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add user.");
                }
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void clearFields() {
        nametxt.setText("");
        emailtxt.setText("");
        passtxt.setText("");
        usertype.setSelectedIndex(-1);
        nametxt.requestFocus();
    }
    private String[] getActiveCourses() {
        ArrayList<String> activeCoursesList = new ArrayList<>();

        try {
            Connection conn = con.connect();
            String query = "SELECT CourseName FROM courses WHERE Status = 'Active'"; // Corrected the status string
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("CourseName");
                activeCoursesList.add(courseName);
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve active courses from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Convert the list to an array
        String[] activeCoursesArray = new String[activeCoursesList.size()];
        activeCoursesArray = activeCoursesList.toArray(activeCoursesArray);
        
        return activeCoursesArray;
    }

    private void storeAllModulesForStudent(String selectedCourse, int selectedLevel, String studentName, Connection conn) throws SQLException {
        try {
            String query = "SELECT ModuleName, Mandatory FROM modules WHERE CourseID = ? AND Level = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, getCourseID(selectedCourse));
            pst.setInt(2, selectedLevel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String moduleName = rs.getString("ModuleName");
                boolean mandatory = rs.getBoolean("Mandatory");

                // If the level is 6 and the module is not mandatory, allow selection
                if (selectedLevel == 6 && !mandatory) {
                    // Ask the student to choose non-mandatory modules
                    int option = JOptionPane.showConfirmDialog(this, "Do you want to take module: " + moduleName + "?", "Module Selection", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // Store the selected module for the student
                        String storeQuery = "INSERT INTO student_modules (Student_ID, Module_Name) VALUES (?, ?)";
                        PreparedStatement storePst = conn.prepareStatement(storeQuery);
                        storePst.setInt(1, getStudentID(studentName)); // Assuming student ID is retrieved based on name
                        storePst.setString(2, moduleName);
                        storePst.executeUpdate();
                        storePst.close();
                    }
                } else {
                    // Automatically store mandatory modules
                    String storeQuery = "INSERT INTO student_modules (Student_ID, Module_Name) VALUES (?, ?)";
                    PreparedStatement storePst = conn.prepareStatement(storeQuery);
                    storePst.setInt(1, getStudentID(studentName)); // Assuming student ID is retrieved based on name
                    storePst.setString(2, moduleName);
                    storePst.executeUpdate();
                    storePst.close();
                }
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to store modules for the student in the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCourseID(String courseName) {
        int courseID = 0;

        try {
            Connection conn = con.connect();
            String query = "SELECT CourseID FROM courses WHERE CourseName = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, courseName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                courseID = rs.getInt("CourseID");
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve course ID from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return courseID;
    }
    private int getStudentID(String studentName) {
        int studentID = 0;

        try {
            Connection conn = con.connect();
            String query = "SELECT Student_ID FROM student_course WHERE Name = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, studentName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                studentID = rs.getInt("Student_ID");
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Failed to retrieve student ID from the database.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return studentID;
    }


}