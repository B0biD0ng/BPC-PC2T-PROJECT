import java.util.Scanner;

public class Main {

    private static final EmployeeDatabase db = new EmployeeDatabase();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Nacteni databaze ze souboru pri spusteni
        try {
            if (FileManager.loadAll(db)) {
                System.out.println("Data nactena z databaze.");
            } else {
                System.out.println("Databaze nenalezena, zaciname s prazdnou.");
            }
        } catch (Exception e) {
            System.out.println("Chyba pri nacteni databaze: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1" -> addEmployee();
                case "2" -> addCooperation();
                case "3" -> removeEmployee();
                case "4" -> findEmployee();
                case "5" -> useSkill();
                case "6" -> db.printByGroups();
                case "7" -> db.printStats();
                case "8" -> db.printGroupCounts();
                case "9" -> saveOneEmployee();
                case "10" -> loadOneEmployee();
                case "0" -> running = false;
                default -> System.out.println("Neplatna volba.");
            }
        }

        // Ulozeni databaze pri ukonceni
        try {
            FileManager.saveAll(db);
        } catch (Exception e) {
            System.out.println("Chyba pri ukladani: " + e.getMessage());
        }
        System.out.println("Program ukoncen.");
    }

    // --- Menu ---

    private static void printMenu() {
        System.out.println("""
            \n========== DATABASE ZAMESTNANCU ==========
             1) Pridat zamestnance
             2) Pridat spolupraci
             3) Odebrat zamestnance
             4) Vyhledat zamestnance dle ID
             5) Pouzit dovednost zamestnance
             6) Abecedni vypis ve skupinach
             7) Statistiky
             8) Pocty zamestnancu ve skupinach
             9) Ulozit zamestnance do souboru
            10) Nacist zamestnance ze souboru
             0) Ukoncit (a ulozit vse)
            ==========================================
            Volba: """);
    }

    // --- Implementace možností ---

    private static void addEmployee() {
        System.out.println("Skupina: 1) Datovy analytik  2) Bezpecnostni specialista");
        System.out.print("Volba skupiny: ");
        String group = sc.nextLine().trim();
        if (!group.equals("1") && !group.equals("2")) {
            System.out.println("Neplatna skupina.");
            return;
        }
        System.out.print("Jmeno: ");
        String first = sc.nextLine().trim();
        System.out.print("Prijmeni: ");
        String last = sc.nextLine().trim();
        System.out.print("Rok narozeni: ");
        int year;
        try {
            year = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Neplatny rok.");
            return;
        }
        Employee emp = db.addEmployee(group, first, last, year);
        System.out.println("Zamestnanec pridan: " + emp);
    }

    private static void addCooperation() {
        System.out.print("ID zamestnance: ");
        int empId = readInt(); if (empId < 0) return;
        System.out.print("ID kolegy: ");
        int colId = readInt(); if (colId < 0) return;
        System.out.println("Uroven spoluprace: 1) dobra  2) prumerna  3) spatna");
        System.out.print("Volba: ");
        String lvlStr = sc.nextLine().trim();
        CooperationLevel level = switch (lvlStr) {
            case "1" -> CooperationLevel.GOOD;
            case "2" -> CooperationLevel.AVERAGE;
            case "3" -> CooperationLevel.POOR;
            default -> null;
        };
        if (level == null) {
            System.out.println("Neplatna uroven.");
            return;
        }
        System.out.println(db.addCooperation(empId, colId, level));
    }

    private static void removeEmployee() {
        System.out.print("ID zamestnance k odebrani: ");
        int id = readInt(); if (id < 0) return;
        if (db.removeEmployee(id)) {
            System.out.println("Zamestnanec ID " + id + " byl odebran.");
        } else {
            System.out.println("Zamestnanec nenalezen.");
        }
    }

    private static void findEmployee() {
        System.out.print("ID zamestnance: ");
        int id = readInt(); if (id < 0) return;
        Employee emp = db.findById(id);
        if (emp == null) {
            System.out.println("Zamestnanec nenalezen.");
            return;
        }
        System.out.println("\n" + emp);
        System.out.println("Spolupracovnici:");
        if (emp.getCooperations().isEmpty()) {
            System.out.println("  (zadni)");
        } else {
            for (Cooperation c : emp.getCooperations()) {
                Employee col = db.findById(c.getColleagueId());
                String name = col != null ? col.getFullName() : "[smazan]";
                System.out.printf("  -> %s (ID %d) [%s]%n", name, c.getColleagueId(), c.getLevel());
            }
        }
    }

    private static void useSkill() {
        System.out.print("ID zamestnance: ");
        int id = readInt(); if (id < 0) return;
        Employee emp = db.findById(id);
        if (emp == null) {
            System.out.println("Zamestnanec nenalezen.");
            return;
        }
        System.out.println("\nDovednost (" + emp.getGroupName() + "):");
        System.out.println(emp.useSkill(db));
    }

    private static void saveOneEmployee() {
        System.out.print("ID zamestnance k ulozeni: ");
        int id = readInt(); if (id < 0) return;
        Employee emp = db.findById(id);
        if (emp == null) {
            System.out.println("Zamestnanec nenalezen.");
            return;
        }
        try {
            FileManager.saveEmployee(emp);
        } catch (Exception e) {
            System.out.println("Chyba pri ukladani: " + e.getMessage());
        }
    }

    private static void loadOneEmployee() {
        System.out.print("Nazev souboru (napr. employee_1.csv): ");
        String filename = sc.nextLine().trim();
        try {
            Employee emp = FileManager.loadEmployee(filename);
            db.loadEmployee(emp);
            System.out.println("Zamestnanec nacten: " + emp);
        } catch (Exception e) {
            System.out.println("Chyba pri nacteni: " + e.getMessage());
        }
    }

    // --- Pomocné ---

    private static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Neplatne cislo.");
            return -1;
        }
    }
}
