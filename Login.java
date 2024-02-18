package test1;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    private JComboBox<String> Id;
    private connect con;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void NewScreen() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Login() {
        con = new connect();
        con.connect();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 332, 391);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel_1 = new JLabel("Login");
        lblNewLabel_1.setFont(new Font("Arial Black", Font.BOLD, 20));
        lblNewLabel_1.setBounds(119, 48, 71, 37);
        contentPane.add(lblNewLabel_1);
        
        textField = new JTextField();
        textField.setBounds(119, 95, 96, 19);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(119, 134, 96, 19);
        contentPane.add(passwordField);

        Id = new JComboBox();
        Id.setFont(new Font("Arial", Font.BOLD, 12));
        Id.setModel(new DefaultComboBoxModel(new String[] { "Student", "Instructor", "Admin" }));
        Id.setBounds(119, 173, 96, 21);
        contentPane.add(Id);

        JButton btnNewButton = new JButton("Confirm");
        btnNewButton.setFont(new Font("Arial", Font.BOLD, 14));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validateUser();
            }
        });
        btnNewButton.setBounds(119, 214, 96, 21);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel_2 = new JLabel("Email:");
        lblNewLabel_2.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2.setBounds(79, 96, 57, 16);
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_2_1 = new JLabel("Password:");
        lblNewLabel_2_1.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2_1.setBounds(53, 135, 64, 16);
        contentPane.add(lblNewLabel_2_1);

        JLabel lblNewLabel_2_2 = new JLabel("Identity:");
        lblNewLabel_2_2.setFont(new Font("Arial", Font.BOLD, 12));
        lblNewLabel_2_2.setBounds(66, 175, 57, 16);
        contentPane.add(lblNewLabel_2_2);

        JLabel lblNewLabel_3 = new JLabel("If you don't have an account please");
        lblNewLabel_3.setBounds(79, 258, 208, 13);
        contentPane.add(lblNewLabel_3);

        JButton btnNewButton_1 = new JButton("Signup");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sign_up.NewScreen();
            }
        });
        btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 10));
        btnNewButton_1.setBounds(125, 281, 73, 19);
        contentPane.add(btnNewButton_1);
        
        JLabel lblNewLabel = new JLabel("Welcome");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel.setBounds(20, 20, 79, 19);
        contentPane.add(lblNewLabel);
    }

    private void validateUser() {
        try {
            Connection conn = con.connect();
            String query = "SELECT u.Role, sc.Student_ID " +
                           "FROM users u " +
                           "LEFT JOIN student_course sc ON u.Name = sc.Name " +
                           "WHERE u.Email=? AND u.Password=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, textField.getText()); // Set the email from the text field
            pst.setString(2, new String(passwordField.getPassword())); // Set the password from the password field
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String identity = rs.getString("Role");
                if ("Student".equals(identity) && "Student".equals(Id.getSelectedItem())) {
                    int studentID = rs.getInt("Student_ID");
                    Student.NewScreen(studentID); // Pass the studentID to the new screen
                    dispose();
                    return; // Exit the method after handling student login
                }
            } 
            // If not a student or no records found, proceed to check for instructor or admin login
            rs.close();
            pst.close();
            
            // Proceed with existing validation for instructor and admin
            conn = con.connect(); // Reconnect to the database
            query = "SELECT u.Role, i.InstructorID " +
                    "FROM users u " +
                    "LEFT JOIN instructors i ON u.Email = i.Email " +
                    "WHERE u.Email=? AND u.Password=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, textField.getText()); // Set the email from the text field
            pst.setString(2, new String(passwordField.getPassword())); // Set the password from the password field
            rs = pst.executeQuery();
            if (rs.next()) {
                String identity = rs.getString("Role");
                if ("Instructor".equals(identity) && "Instructor".equals(Id.getSelectedItem())) {
                    int instructorID = rs.getInt("InstructorID");
                    Instructor.NewScreen(instructorID); // Pass the instructorID to the new screen
                    dispose();
                    return; // Exit the method after handling instructor login
                } else if ("Admin".equals(identity) && "Admin".equals(Id.getSelectedItem())) {
                    Administrator.NewScreen();
                    dispose();
                    return; // Exit the method after handling admin login
                }
            }
            // If no valid user found
            JOptionPane.showMessageDialog(null, "Invalid Email, Password, or Identity");
            rs.close();
            pst.close();
            conn.close();
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }
    }
}