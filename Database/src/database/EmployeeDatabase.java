package database;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase {

    // Hlavná dátová štruktúra: HashMap pre O(1) lookup podľa ID
    private final Map<Integer, Employee> employees = new HashMap<>();

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public void addEmployee(Employee e) {
        employees.put(e.getId(), e);
        System.out.println("Zamestnanec pridaný: " + e);
    }

    /**
     * Odoberie zamestnanca a vymaže všetky jeho väzby u ostatných.
     */
    public boolean removeEmployee(int id) {
        Employee removed = employees.remove(id);
        if (removed == null) {
            System.out.println("Zamestnanec s ID " + id + " neexistuje.");
            return false;
        }
        // Odstrán väzby u všetkých ostatných
        for (Employee e : employees.values()) {
            e.removeCollaborationWith(id);
        }
        System.out.println("Zamestnanec " + removed.getFirstName() + " " + removed.getLastName() + " bol odstránený.");
        return true;
    }

    public Employee findById(int id) {
        return employees.get(id);
    }

    // ── Collaboration ─────────────────────────────────────────────────────────

    public boolean addCollaboration(int employeeId, int colleagueId, CollaborationLevel level) {
        Employee emp = employees.get(employeeId);
        Employee col = employees.get(colleagueId);

        if (emp == null) { System.out.println("Zamestnanec ID " + employeeId + " neexistuje."); return false; }
        if (col == null) { System.out.println("Kolega ID " + colleagueId + " neexistuje."); return false; }
        if (employeeId == colleagueId) { System.out.println("Zamestnanec nemôže byť sám sebe kolegom."); return false; }

        emp.addCollaboration(new Collaboration(colleagueId,
                col.getFirstName() + " " + col.getLastName(), level));
        System.out.println("Spolupráca pridaná.");
        return true;
    }

    // ── Výpisy ────────────────────────────────────────────────────────────────

    /**
     * Abecedný výpis podľa priezviska v skupinách.
     */
    public void printAlphabetical() {
        Map<String, List<Employee>> groups = new TreeMap<>();
        for (Employee e : employees.values()) {
            groups.computeIfAbsent(e.getGroupName(), k -> new ArrayList<>()).add(e);
        }
        System.out.println("\n=== Abecedný výpis zamestnancov ===");
        for (Map.Entry<String, List<Employee>> entry : groups.entrySet()) {
            System.out.println("\n[ " + entry.getKey() + " ]");
            entry.getValue().stream()
                    .sorted(Comparator.comparing(Employee::getLastName)
                            .thenComparing(Employee::getFirstName))
                    .forEach(System.out::println);
        }
        System.out.println();
    }

    /**
     * Počet zamestnancov v skupinách.
     */
    public void printGroupCounts() {
        Map<String, Long> counts = employees.values().stream()
                .collect(Collectors.groupingBy(Employee::getGroupName, Collectors.counting()));
        System.out.println("\n=== Počet zamestnancov v skupinách ===");
        counts.forEach((group, count) ->
                System.out.printf("  %-22s : %d%n", group, count));
        System.out.printf("  %-22s : %d%n", "SPOLU", employees.size());
        System.out.println();
    }

    /**
     * Štatistiky: prevažujúca kvalita spolupráce + zamestnanec s najviac väzbami.
     */
    public void printStatistics() {
        System.out.println("\n=== Štatistiky databázy ===");

        if (employees.isEmpty()) {
            System.out.println("Databáza je prázdna.");
            return;
        }

        // Celkové počty úrovní
        long poor = 0, avg = 0, good = 0;
        int maxLinks = -1;
        Employee mostConnected = null;

        for (Employee e : employees.values()) {
            int links = e.getCollaborations().size();
            if (links > maxLinks) {
                maxLinks = links;
                mostConnected = e;
            }
            for (Collaboration c : e.getCollaborations()) {
                switch (c.getLevel()) {
                    case POOR    -> poor++;
                    case AVERAGE -> avg++;
                    case GOOD    -> good++;
                }
            }
        }

        String dominant;
        if (good >= avg && good >= poor)      dominant = "DOBRÁ";
        else if (avg >= poor)                  dominant = "PRIEMERNÁ";
        else                                   dominant = "ŠPATNÁ";

        System.out.printf("Spolupráce  –  dobrá: %d  priemerná: %d  špatná: %d%n", good, avg, poor);
        System.out.printf("Prevažujúca kvalita : %s%n", dominant);
        if (mostConnected != null) {
            System.out.printf("Najviac väzieb      : %s %s (ID:%d, väzieb: %d)%n",
                    mostConnected.getFirstName(), mostConnected.getLastName(),
                    mostConnected.getId(), maxLinks);
        }
        System.out.println();
    }

    // ── Skill ─────────────────────────────────────────────────────────────────

    public void runSkill(int id) {
        Employee e = employees.get(id);
        if (e == null) {
            System.out.println("Zamestnanec neexistuje.");
            return;
        }
        e.runSkill(employees);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Map<Integer, Employee> getAllEmployees() {
        return Collections.unmodifiableMap(employees);
    }

    public boolean isEmpty() {
        return employees.isEmpty();
    }

    /**
     * Vloží zamestnanca priamo (bez výpisu) – používa sa pri načítaní z SQL / súboru.
     */
    public void loadEmployee(Employee e) {
        employees.put(e.getId(), e);
    }

    /** Vymaže celú databázu (pred novým načítaním z SQL). */
    public void clear() {
        employees.clear();
    }
}
