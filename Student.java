package test1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Student extends JFrame {

    private JPanel contentPane;
    private int studentID;

    public static void NewScreen(int studentID) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Student frame = new Student(studentID);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Student(int studentID) {
        this.studentID = studentID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        JPanel coursePanel = new JPanel();
        tabbedPane.addTab("Courses", null, coursePanel, null);
        coursePanel.setLayout(new BorderLayout(0, 0));

        JTable courseTable = new JTable();
        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        coursePanel.add(courseScrollPane, BorderLayout.CENTER);

        JPanel modulePanel = new JPanel();
        tabbedPane.addTab("Modules", null, modulePanel, null);
        modulePanel.setLayout(new BorderLayout(0, 0));

        JTable moduleTable = new JTable();
        JScrollPane moduleScrollPane = new JScrollPane(moduleTable);
        modulePanel.add(moduleScrollPane, BorderLayout.CENTER);

        JPanel instructorPanel = new JPanel();
        tabbedPane.addTab("Instructors", null, instructorPanel, null);
        instructorPanel.setLayout(new BorderLayout(0, 0));

        JTable instructorTable = new JTable();
        JScrollPane instructorScrollPane = new JScrollPane(instructorTable);
        instructorPanel.add(instructorScrollPane, BorderLayout.CENTER);

        JPanel gradePanel = new JPanel();
        tabbedPane.addTab("Grades", null, gradePanel, null);
        gradePanel.setLayout(new BorderLayout(0, 0));

        JTable gradeTable = new JTable();
        JScrollPane gradeScrollPane = new JScrollPane(gradeTable);
        gradePanel.add(gradeScrollPane, BorderLayout.CENTER);

        displayCourseInfo(courseTable);
        displayModuleInfo(moduleTable);
        displayInstructors(instructorTable);
        displayGrades(gradeTable);
    }

    private void displayCourseInfo(JTable table) {
        try {
            Connection conn = new connect().connect();
            String query = "SELECT CourseName FROM courses WHERE CourseID IN " +
                    "(SELECT Course_ID FROM student_course WHERE Student_ID = ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, studentID);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Course Name");

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("CourseName")});
            }

            table.setModel(model);

            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayInstructors(JTable table) {
        try {
            Connection conn = new connect().connect();
            String query = "SELECT m.ModuleName, i.Name AS InstructorName " +
                    "FROM instructor_modules im " +
                    "JOIN instructors i ON im.InstructorID = i.InstructorID " +
                    "JOIN modules m ON im.ModuleID = m.ModuleID " +
                    "WHERE m.ModuleName IN (SELECT Module_Name FROM student_modules WHERE Student_ID = ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, studentID);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Module Name");
            model.addColumn("Instructor Name");

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("ModuleName"), rs.getString("InstructorName")});
            }

            table.setModel(model);

            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayModuleInfo(JTable table) {
        try {
            Connection conn = new connect().connect();
            String query = "SELECT Module_Name FROM student_modules WHERE Student_ID = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, studentID);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Module Name");

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("Module_Name")});
            }

            table.setModel(model);

            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void displayGrades(JTable table) {
        try {
            Connection conn = new connect().connect();
            String query = "SELECT Module_Name, Grade FROM student_modules WHERE Student_ID = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, studentID);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Module Name");
            model.addColumn("Grade");

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("Module_Name"), rs.getString("Grade")});
            }

            table.setModel(model);

            pst.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}