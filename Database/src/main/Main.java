package main;

import database.EmployeeDatabase;
import io.FileManager;
import io.SQLManager;
import java.util.List;
import java.util.Scanner;
import model.*;

public class Main {

    private static final EmployeeDatabase db = new EmployeeDatabase();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        // ── Načítanie z SQL pri štarte ─────────────────────────────────────────
        SQLManager.initDatabase();
        List<Employee> loaded = SQLManager.loadAll();
        if (!loaded.isEmpty()) {
            int maxId = 0;
            for (Employee e : loaded) {
                db.loadEmployee(e);
                if (e.getId() > maxId) maxId = e.getId();
            }
            Employee.setIdCounter(maxId + 1);
            System.out.println("Databáza načítaná z SQL.\n");
        } else {
            System.out.println("SQL databáza je prázdna, štartujem s prázdnou databázou.\n");
        }

        // ── Hlavné menu ────────────────────────────────────────────────────────
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Voľba: ");

            switch (choice) {
                case 1  -> addEmployee();
                case 2  -> addCollaboration();
                case 3  -> removeEmployee();
                case 4  -> findById();
                case 5  -> runSkill();
                case 6  -> db.printAlphabetical();
                case 7  -> db.printStatistics();
                case 8  -> db.printGroupCounts();
                case 9  -> saveToFile();
                case 10 -> loadFromFile();
                case 0  -> running = false;
                default -> System.out.println("Neplatná voľba.");
            }
        }

        // ── Uloženie do SQL pri ukončení ───────────────────────────────────────
        System.out.println("\nUkladám dáta do SQL databázy...");
        SQLManager.saveAll(db.getAllEmployees().values());
        System.out.println("Dovidenia!");
    }

    // ── Menu ───────────────────────────────────────────────────────────────────

    private static void printMenu() {
        System.out.println("""
                
                ╔══════════════════════════════════╗
                ║    DATABÁZA ZAMESTNANCOV         ║
                ╠══════════════════════════════════╣
                ║  1. Pridať zamestnanca           ║
                ║  2. Pridať spoluprácu            ║
                ║  3. Odstrániť zamestnanca        ║
                ║  4. Vyhľadať podľa ID            ║
                ║  5. Spustiť skill zamestnanc     ║
                ║  6. Abecedný výpis               ║
                ║  7. Štatistiky                   ║
                ║  8. Počty v skupinách            ║
                ║  9. Uložiť zamestnanca do súboru ║
                ║ 10. Načítať zamestnanca zo súboru║
                ║  0. Ukončiť program              ║
                ╚══════════════════════════════════╝""");
    }

    // ── Akcie ──────────────────────────────────────────────────────────────────

    private static void addEmployee() {
        System.out.println("Skupiny: 1. DataAnalyst  2. SecuritySpecialist");
        int groupChoice = readInt("Skupina (1/2): ");
        if (groupChoice != 1 && groupChoice != 2) {
            System.out.println("Neplatná skupina."); return;
        }
        System.out.print("Meno: ");
        String firstName = sc.nextLine().trim();
        System.out.print("Priezvisko: ");
        String lastName = sc.nextLine().trim();
        int birthYear = readInt("Rok narodenia: ");

        Employee emp = (groupChoice == 1)
                ? new DataAnalyst(firstName, lastName, birthYear)
                : new SecuritySpecialist(firstName, lastName, birthYear);

        db.addEmployee(emp);
    }

    private static void addCollaboration() {
        int empId = readInt("ID zamestnanca: ");
        int colId = readInt("ID kolegu: ");
        System.out.println("Úroveň: 1. Špatná (POOR)  2. Priemerná (AVERAGE)  3. Dobrá (GOOD)");
        int lvlChoice = readInt("Úroveň (1/2/3): ");
        CollaborationLevel level = switch (lvlChoice) {
            case 1 -> CollaborationLevel.POOR;
            case 2 -> CollaborationLevel.AVERAGE;
            case 3 -> CollaborationLevel.GOOD;
            default -> { System.out.println("Neplatná úroveň."); yield null; }
        };
        if (level == null) return;
        db.addCollaboration(empId, colId, level);
    }

    private static void removeEmployee() {
        int id = readInt("ID zamestnanca na odstránenie: ");
        db.removeEmployee(id);
    }

    private static void findById() {
        int id = readInt("ID zamestnanca: ");
        Employee e = db.findById(id);
        if (e == null) {
            System.out.println("Zamestnanec neexistuje.");
        } else {
            e.printBasicInfo();
        }
    }

    private static void runSkill() {
        int id = readInt("ID zamestnanca: ");
        db.runSkill(id);
    }

    private static void saveToFile() {
        int id = readInt("ID zamestnanca: ");
        Employee e = db.findById(id);
        if (e == null) { System.out.println("Zamestnanec neexistuje."); return; }
        System.out.print("Názov súboru (napr. emp_1.txt): ");
        String filename = sc.nextLine().trim();
        if (filename.isEmpty()) filename = "employee_" + id + ".txt";
        FileManager.saveEmployee(e, filename);
    }

    private static void loadFromFile() {
        System.out.print("Názov súboru: ");
        String filename = sc.nextLine().trim();
        Employee e = FileManager.loadEmployee(filename);
        if (e != null) {
            db.loadEmployee(e);
            System.out.println("Zamestnanec pridaný do databázy: " + e);
        }
    }

    // ── Pomocné metódy ─────────────────────────────────────────────────────────

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Zadaj celé číslo.");
            }
        }
    }
}
