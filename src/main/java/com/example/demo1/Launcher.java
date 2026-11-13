package com.example.demo1;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Launcher extends Application {
    public void start(Stage stage) {

        //den her er tilføjet for at vise student navnene i comboboxen
        TextField studentNameField = new TextField("Student Name");
        ComboBox<String> StudentComboBox = new ComboBox<>();
        List<String> studentNames = studentNames();
        StudentComboBox.getItems().addAll(studentNames);

        //den her er tilføjet for at vise aktivitet navnene i comboboxen
        TextField activityNameField = new TextField("Activity Name");
        ComboBox<String> ActivityComboBox = new ComboBox<>();
        List<String> activityNames = ActivityNames();
        ActivityComboBox.getItems().addAll(activityNames);

        TextField groupNameField = new TextField("Group Name");
        ListView<String> Visgruppe = new ListView<>();
        Visgruppe.setPlaceholder(new Label("Vælg en studerende og aktivitet for at vælge en gruppe."));


        StudentComboBox.setOnAction(event -> {
            String selectedStudent = StudentComboBox.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                studentNameField.setText(selectedStudent);
            }
            loadGroups(StudentComboBox.getValue(), ActivityComboBox.getValue(), Visgruppe);
        });

        ActivityComboBox.setOnAction(event -> {
            String selectedActivity = ActivityComboBox.getSelectionModel().getSelectedItem();
            if (selectedActivity != null) {
                activityNameField.setText(selectedActivity);
            }
            loadGroups(StudentComboBox.getValue(), ActivityComboBox.getValue(), Visgruppe);
        });

        // linje 54-71 er for tekst til gruppe opretelse
        TextField Gruppeoprettelse = new TextField("Gruppe Oprettelse");
        TextArea TitleforNytprojekt = new TextArea();
        TitleforNytprojekt.setPromptText("Group title");

        TextField Activity = new TextField("Activity");
        ComboBox<String> ActivityAfProjektet = new ComboBox<>();
        List<String> activityNamesForProjektOprettelse = ActivityNames();
        ActivityAfProjektet.getItems().addAll(activityNamesForProjektOprettelse);

        TextField StudentForProjektOprettelse = new TextField("Student");
        ComboBox<String> StudentComboBoxForProjektOprettelse = new ComboBox<>();
        List<String> studentNamesForProjektOprettelse = studentNames();
        StudentComboBoxForProjektOprettelse.getItems().addAll(studentNamesForProjektOprettelse);

        TextField Supervisor = new TextField("Supervisor");
        ComboBox<String> SupervisorProjektopretelseComboBox = new ComboBox<>();
        List<String> SupervisorProjektNames = SupervisorNames();
        SupervisorProjektopretelseComboBox.getItems().addAll(SupervisorProjektNames);

        // her for en knap der loader alle gruppe dannelser der er oprettet. AI generet
        Button createGroupButton = new Button("Create group");
        createGroupButton.setOnAction(event -> {
            gruppeoprettelse(
                    StudentComboBoxForProjektOprettelse.getValue(),
                    SupervisorProjektopretelseComboBox.getValue(),
                    TitleforNytprojekt.getText(),
                    ActivityAfProjektet.getValue());
            loadGroups(StudentComboBox.getValue(), ActivityComboBox.getValue(), Visgruppe);
        });


        // tilknyt grupper
        TextField StudentTilknyt = new TextField("Navn");
        ComboBox<String> StudentComboBoxTilknyt = new ComboBox<>();
        studentNamesForProjektOprettelse = studentNames();
        StudentComboBoxTilknyt.getItems().addAll(studentNamesForProjektOprettelse);

        TextField Activityfortilknyt= new TextField("Activity");
        ComboBox<String> ActivityAfProjektetTilknyt = new ComboBox<>();
        List<String> activityNamesForProjektTilknyt = ActivityNames();
        ActivityAfProjektetTilknyt.getItems().addAll(activityNamesForProjektTilknyt);






        VBox Student = new VBox(studentNameField, StudentComboBox, activityNameField, ActivityComboBox, groupNameField,
                Visgruppe, Gruppeoprettelse, TitleforNytprojekt,
                StudentForProjektOprettelse,StudentComboBoxForProjektOprettelse, Supervisor,
                SupervisorProjektopretelseComboBox, createGroupButton, StudentTilknyt ,StudentComboBoxTilknyt, Activityfortilknyt,ActivityAfProjektetTilknyt);
        stage.setTitle("Gruppe dannelse");
        stage.setScene(new Scene(Student, 1200, 1200));
        stage.show();


    }
    public List<String> studentNames() {
        String url = "jdbc:sqlite:softwaredeleopment.sqlite";
        Connection conn = null;
        List<String> studentNames = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Student");
            while (rs.next()) {
                String studentName = rs.getString("name");
                studentNames.add(studentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return studentNames;
    }

    public List<String> ActivityNames() {
        String url = "jdbc:sqlite:softwaredeleopment.sqlite";
        Connection conn = null;
        List<String> activityNames = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Project_Activity");
            while (rs.next()) {
                String activityName = rs.getString("name");
                activityNames.add(activityName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return activityNames;
    }

    private void loadGroups(String studentName, String activityName, ListView<String> groupList) {
        groupList.getItems().clear();
        if (studentName == null || activityName == null) {
            return;
        }
        // AI generet
        String url = "jdbc:sqlite:softwaredeleopment.sqlite";
        List<String> groupNames = new ArrayList<>();
        String sql = "SELECT pg.title " +
                "FROM SIGN_UP su " +
                "JOIN STUDENT s ON s.student_id = su.student_id " +
                "JOIN PROJECT_ACTIVITY pa ON pa.activity_id = su.activity_id " +
                "JOIN PROJECT_GROUP pg ON pg.group_id = su.group_id " +
                "WHERE s.name = ? AND pa.name = ? " +
                "ORDER BY pg.title";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentName);
            stmt.setString(2, activityName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groupNames.add(rs.getString("title"));
            }
            groupList.getItems().setAll(groupNames);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "ingen gruppe er opdateret: " + e.getMessage()).show();
        }
    }

    public List<String> SupervisorNames() {
        String url = "jdbc:sqlite:softwaredeleopment.sqlite";
        Connection conn = null;
        List<String> supervisorNames = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Supervisor");
            while (rs.next()) {
                String supervisorName = rs.getString("name");
                supervisorNames.add(supervisorName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return supervisorNames;
    }


    //AI generet
    public void gruppeoprettelse(String studentName, String supervisorName, String title, String activityName) {
        if (studentName == null || supervisorName == null || activityName == null
                || title == null || title.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a student, supervisor, and activity, and enter a group title.").showAndWait();
            return;
        }

        String url = "jdbc:sqlite:softwaredeleopment.sqlite";
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            try {
                long studentId = findId(conn, "SELECT student_id FROM STUDENT WHERE name = ?", studentName, "Student");
                long supervisorId = findId(conn, "SELECT supervisor_id FROM SUPERVISOR WHERE name = ?",
                        supervisorName, "Supervisor");
                long activityId = findId(conn, "SELECT activity_id FROM PROJECT_ACTIVITY WHERE name = ?",
                        activityName, "Activity");

                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT 1 FROM SIGN_UP WHERE student_id = ? AND activity_id = ?")) {
                    stmt.setLong(1, studentId);
                    stmt.setLong(2, activityId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("The selected student is already assigned to a group for this activity.");
                        }
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO PROJECT_GROUP (title, activity_id, supervisor_id) VALUES (?, ?, ?)")) {
                    stmt.setString(1, title.trim());
                    stmt.setLong(2, activityId);
                    stmt.setLong(3, supervisorId);
                    stmt.executeUpdate();
                }

                long groupId;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    rs.next();
                    groupId = rs.getLong(1);
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO SIGN_UP (student_id, activity_id, group_id) VALUES (?, ?, ?)")) {
                    stmt.setLong(1, studentId);
                    stmt.setLong(2, activityId);
                    stmt.setLong(3, groupId);
                    stmt.executeUpdate();
                }
                conn.commit();
                new Alert(Alert.AlertType.INFORMATION, "Group created successfully.").showAndWait();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Could not create group: " + e.getMessage()).showAndWait();
        }
    }

    private long findId(Connection conn, String sql, String name, String entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException(entity + " not found: " + name);
    }



    public void Tilknytgruppe() {





    }



    public static void main(String[] args) {
        launch(args);
    }


}