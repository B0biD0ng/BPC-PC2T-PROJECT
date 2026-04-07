import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase {
    private final Map<Integer, Employee> employees; // dynamická datová štruktúra
    private int nextId;

    public EmployeeDatabase() {
        this.employees = new LinkedHashMap<>();
        this.nextId = 1;
    }

    // --- Přidání / odebrání zaměstnance ---

    public Employee addEmployee(String group, String firstName, String lastName, int birthYear) {
        int id = nextId++;
        Employee emp;
        if (group.equals("1")) {
            emp = new DataAnalyst(id, firstName, lastName, birthYear);
        } else {
            emp = new SecuritySpecialist(id, firstName, lastName, birthYear);
        }
        employees.put(id, emp);
        return emp;
    }

    public boolean removeEmployee(int id) {
        if (!employees.containsKey(id)) return false;
        employees.remove(id);
        // Odstraní všechny vazby na tohoto zaměstnance
        for (Employee emp : employees.values()) {
            emp.removeCooperation(id);
        }
        return true;
    }

    // --- Vyhledání ---

    public Employee findById(int id) {
        return employees.get(id);
    }

    public Collection<Employee> getAllEmployees() {
        return employees.values();
    }

    // --- Spolupráce ---

    public String addCooperation(int empId, int colleagueId, CooperationLevel level) {
        Employee emp = employees.get(empId);
        Employee colleague = employees.get(colleagueId);
        if (emp == null) return "Zamestnanec ID " + empId + " neexistuje.";
        if (colleague == null) return "Kolega ID " + colleagueId + " neexistuje.";
        if (empId == colleagueId) return "Zamestnanec nemuze spolupracovat sam se sebou.";
        emp.addCooperation(colleagueId, level);
        return "Spoluprace pridana: " + emp.getFullName() + " -> " + colleague.getFullName() + " [" + level + "]";
    }

    // --- Abecední výpis podle skupin ---

    public void printByGroups() {
        List<Employee> analysts = employees.values().stream()
                .filter(e -> e instanceof DataAnalyst)
                .sorted(Comparator.comparing(Employee::getLastName).thenComparing(Employee::getFirstName))
                .collect(Collectors.toList());

        List<Employee> specialists = employees.values().stream()
                .filter(e -> e instanceof SecuritySpecialist)
                .sorted(Comparator.comparing(Employee::getLastName).thenComparing(Employee::getFirstName))
                .collect(Collectors.toList());

        System.out.println("\n=== Datovi analytici (" + analysts.size() + ") ===");
        if (analysts.isEmpty()) System.out.println("  (zadni)");
        else analysts.forEach(e -> System.out.println("  " + e));

        System.out.println("\n=== Bezpecnostni specialisti (" + specialists.size() + ") ===");
        if (specialists.isEmpty()) System.out.println("  (zadni)");
        else specialists.forEach(e -> System.out.println("  " + e));
    }

    // --- Statistiky ---

    public void printStats() {
        if (employees.isEmpty()) {
            System.out.println("Databaze je prazdna.");
            return;
        }

        // Převažující kvalita spolupráce
        Map<CooperationLevel, Integer> total = new EnumMap<>(CooperationLevel.class);
        for (CooperationLevel lvl : CooperationLevel.values()) total.put(lvl, 0);
        for (Employee emp : employees.values()) {
            for (Cooperation c : emp.getCooperations()) {
                total.merge(c.getLevel(), 1, Integer::sum);
            }
        }
        CooperationLevel dominant = total.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        // Zaměstnanec s nejvíce vazbami
        Employee mostConnected = employees.values().stream()
                .max(Comparator.comparingInt(e -> e.getCooperations().size()))
                .orElse(null);

        System.out.println("\n=== Statistiky ===");
        System.out.println("Celkem zamestnancu: " + employees.size());
        System.out.println("Prevazujici kvalita spoluprace: " + (dominant != null ? dominant : "N/A"));
        System.out.printf("  dobra: %d, prumerna: %d, spatna: %d%n",
                total.get(CooperationLevel.GOOD),
                total.get(CooperationLevel.AVERAGE),
                total.get(CooperationLevel.POOR));
        if (mostConnected != null) {
            System.out.printf("Nejvice vazeb: %s (ID %d) - %d spolupracovniku%n",
                    mostConnected.getFullName(), mostConnected.getId(),
                    mostConnected.getCooperations().size());
        }
    }

    // --- Počty ve skupinách ---

    public void printGroupCounts() {
        long analysts = employees.values().stream().filter(e -> e instanceof DataAnalyst).count();
        long specialists = employees.values().stream().filter(e -> e instanceof SecuritySpecialist).count();
        System.out.println("\n=== Pocty ve skupinach ===");
        System.out.println("Datovi analytici:        " + analysts);
        System.out.println("Bezpecnostni specialisti: " + specialists);
    }

    // --- Správa ID ---

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getNextId() {
        return nextId;
    }

    public void loadEmployee(Employee emp) {
        employees.put(emp.getId(), emp);
        if (emp.getId() >= nextId) nextId = emp.getId() + 1;
    }
}
