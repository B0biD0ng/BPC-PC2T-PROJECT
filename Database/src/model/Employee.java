package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Employee {

    private static int idCounter = 1;

    private int id;
    private String firstName;
    private String lastName;
    private int birthYear;
    private List<Collaboration> collaborations;

    // ── Constructors ───────────────────────────────────────────────────────────

    /** Použit pri tvorbe nového zamestnanca (ID sa pridelí automaticky). */
    public Employee(String firstName, String lastName, int birthYear) {
        this.id = idCounter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.collaborations = new ArrayList<>();
    }

    /** Použit pri načítaní zo súboru / SQL (ID je už známe). */
    public Employee(int id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.collaborations = new ArrayList<>();
        if (id >= idCounter) idCounter = id + 1;
    }

    // ── Abstract skill ─────────────────────────────────────────────────────────

    /**
     * Každá podtrieda implementuje vlastnú schopnosť.
     * @param allEmployees mapa všetkých zamestnancov (niektoré skill-y ju potrebujú)
     */
    public abstract void runSkill(Map<Integer, Employee> allEmployees);

    /** Vráti názov skupiny (DataAnalyst / SecuritySpecialist). */
    public abstract String getGroupName();

    // ── Collaboration management ───────────────────────────────────────────────

    public void addCollaboration(Collaboration c) {
        for (Collaboration existing : collaborations) {
            if (existing.getColleagueId() == c.getColleagueId()) {
                System.out.println("Spolupráca s týmto zamestnancom už existuje.");
                return;
            }
        }
        collaborations.add(c);
    }

    public boolean removeCollaborationWith(int colleagueId) {
        return collaborations.removeIf(c -> c.getColleagueId() == colleagueId);
    }

    public List<Collaboration> getCollaborations() {
        return collaborations;
    }

    // ── Basic info ─────────────────────────────────────────────────────────────

    public void printBasicInfo() {
        System.out.println("─────────────────────────────────────────");
        System.out.printf("ID       : %d%n", id);
        System.out.printf("Meno     : %s %s%n", firstName, lastName);
        System.out.printf("Nar.     : %d%n", birthYear);
        System.out.printf("Skupina  : %s%n", getGroupName());
        System.out.printf("Kolegov  : %d%n", collaborations.size());
        if (!collaborations.isEmpty()) {
            // Štatistika kvality
            long good = collaborations.stream().filter(c -> c.getLevel() == CollaborationLevel.GOOD).count();
            long avg  = collaborations.stream().filter(c -> c.getLevel() == CollaborationLevel.AVERAGE).count();
            long poor = collaborations.stream().filter(c -> c.getLevel() == CollaborationLevel.POOR).count();
            System.out.printf("  Dobrá / Priemerná / Špatná: %d / %d / %d%n", good, avg, poor);
            System.out.println("Zoznam spoluprác:");
            collaborations.forEach(System.out::println);
        }
        System.out.println("─────────────────────────────────────────");
    }

    // ── File save/load ────────────────────────────────────────────────────────

    /**
     * Uloží zamestnanca do textového súboru.
     * Formát:
     *   GROUP|id|firstName|lastName|birthYear
     *   COLLAB|colleagueId|colleagueFullName|LEVEL
     *   ...
     *   END
     */
    public void saveToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.printf("%s|%d|%s|%s|%d%n",
                    getGroupName(), id, firstName, lastName, birthYear);
            for (Collaboration c : collaborations) {
                pw.printf("COLLAB|%d|%s|%s%n",
                        c.getColleagueId(), c.getColleagueFullName(), c.getLevel().name());
            }
            pw.println("END");
            System.out.println("Zamestnanec uložený do: " + filename);
        } catch (IOException e) {
            System.err.println("Chyba pri ukladaní: " + e.getMessage());
        }
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getBirthYear() { return birthYear; }

    public static int getIdCounter() { return idCounter; }
    public static void setIdCounter(int value) { idCounter = value; }

    @Override
    public String toString() {
        return String.format("[%s] ID:%-4d %s %s (nar. %d)",
                getGroupName(), id, firstName, lastName, birthYear);
    }
}
