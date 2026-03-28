package io;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Spravuje zálohu dát v SQLite databáze.
 *
 * Schéma:
 *   employees   (id, group_name, first_name, last_name, birth_year)
 *   collaborations (employee_id, colleague_id, colleague_name, level)
 *
 * POZNÁMKA: SQLite JDBC driver (sqlite-jdbc) musí byť v classpath.
 * Stiahnuť napr. z: https://github.com/xerial/sqlite-jdbc/releases
 * Príkaz kompilácie: javac -cp .;sqlite-jdbc-*.jar ...
 * Príkaz spustenia:  java  -cp .;sqlite-jdbc-*.jar main.Main
 */
public class SQLManager {

    private static final String DB_FILE = "employees.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    // ── Pripojenie ─────────────────────────────────────────────────────────────

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ── Inicializácia tabuliek ─────────────────────────────────────────────────

    public static void initDatabase() {
        String createEmployees = """
                CREATE TABLE IF NOT EXISTS employees (
                    id          INTEGER PRIMARY KEY,
                    group_name  TEXT NOT NULL,
                    first_name  TEXT NOT NULL,
                    last_name   TEXT NOT NULL,
                    birth_year  INTEGER NOT NULL
                );
                """;
        String createCollabs = """
                CREATE TABLE IF NOT EXISTS collaborations (
                    employee_id    INTEGER NOT NULL,
                    colleague_id   INTEGER NOT NULL,
                    colleague_name TEXT NOT NULL,
                    level          TEXT NOT NULL,
                    FOREIGN KEY(employee_id) REFERENCES employees(id)
                );
                """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createEmployees);
            stmt.execute(createCollabs);
        } catch (SQLException e) {
            System.err.println("SQL init chyba: " + e.getMessage());
        }
    }

    // ── Uloženie všetkých dát ─────────────────────────────────────────────────

    public static void saveAll(Iterable<Employee> employees) {
        String deleteEmps   = "DELETE FROM employees";
        String deleteCollabs= "DELETE FROM collaborations";
        String insertEmp    = "INSERT INTO employees VALUES (?,?,?,?,?)";
        String insertCollab = "INSERT INTO collaborations VALUES (?,?,?,?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {
                st.execute(deleteEmps);
                st.execute(deleteCollabs);
            }

            try (PreparedStatement psEmp    = conn.prepareStatement(insertEmp);
                 PreparedStatement psCollab = conn.prepareStatement(insertCollab)) {

                for (Employee e : employees) {
                    psEmp.setInt   (1, e.getId());
                    psEmp.setString(2, e.getGroupName());
                    psEmp.setString(3, e.getFirstName());
                    psEmp.setString(4, e.getLastName());
                    psEmp.setInt   (5, e.getBirthYear());
                    psEmp.addBatch();

                    for (Collaboration c : e.getCollaborations()) {
                        psCollab.setInt   (1, e.getId());
                        psCollab.setInt   (2, c.getColleagueId());
                        psCollab.setString(3, c.getColleagueFullName());
                        psCollab.setString(4, c.getLevel().name());
                        psCollab.addBatch();
                    }
                }
                psEmp.executeBatch();
                psCollab.executeBatch();
            }

            conn.commit();
            System.out.println("Dáta uložené do SQL databázy (" + DB_FILE + ").");

        } catch (SQLException e) {
            System.err.println("SQL uloženie zlyhalo: " + e.getMessage());
        }
    }

    // ── Načítanie všetkých dát ────────────────────────────────────────────────

    public static List<Employee> loadAll() {
        List<Employee> list = new ArrayList<>();

        String queryEmps = "SELECT * FROM employees ORDER BY id";
        String queryCollabs = "SELECT * FROM collaborations WHERE employee_id = ?";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryEmps)) {

            while (rs.next()) {
                int id           = rs.getInt("id");
                String group     = rs.getString("group_name");
                String firstName = rs.getString("first_name");
                String lastName  = rs.getString("last_name");
                int birthYear    = rs.getInt("birth_year");

                Employee emp;
                switch (group) {
                    case "DataAnalyst"        -> emp = new DataAnalyst(id, firstName, lastName, birthYear);
                    case "SecuritySpecialist" -> emp = new SecuritySpecialist(id, firstName, lastName, birthYear);
                    default -> {
                        System.err.println("Neznáma skupina: " + group + " – preskakujem.");
                        continue;
                    }
                }

                // Načítaj spolupráce tohto zamestnanca
                try (PreparedStatement ps = conn.prepareStatement(queryCollabs)) {
                    ps.setInt(1, id);
                    try (ResultSet crs = ps.executeQuery()) {
                        while (crs.next()) {
                            int colId       = crs.getInt("colleague_id");
                            String colName  = crs.getString("colleague_name");
                            CollaborationLevel lvl = CollaborationLevel.fromString(crs.getString("level"));
                            emp.getCollaborations().add(new Collaboration(colId, colName, lvl));
                        }
                    }
                }

                list.add(emp);
            }

            System.out.println("Načítaných " + list.size() + " zamestnancov z SQL databázy.");

        } catch (SQLException e) {
            System.err.println("SQL načítanie zlyhalo: " + e.getMessage());
        }

        return list;
    }
}
