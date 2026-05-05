import java.io.*;
import java.util.*;


public class FileManager {

    
    private static final String DB_FILE = "database.csv";

   
    public static void saveEmployee(Employee emp) throws IOException {
        String filename = "employee_" + emp.getId() + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            
            writer.println(emp.toCsv());
        }
        System.out.println("Zamestnanec ulozeny do textoveho suboru: " + filename);
    }

    
    public static Employee loadEmployee(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                
                return Employee.fromCsv(line);
            }
        }
        return null;
    }

    
    public static void saveAll(EmployeeDatabase db) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DB_FILE))) {
            
            writer.println("NEXT_ID:" + db.getNextId());
            
            
            for (Employee emp : db.getAllEmployees()) {
                writer.println(emp.toCsv());
            }
        }
        System.out.println("Vsetky data boli uspesne ulozene do: " + DB_FILE);
    }

    
    public static boolean loadAll(EmployeeDatabase db) throws IOException {
        File f = new File(DB_FILE);
        if (!f.exists()) {
            System.out.println("Databazovy subor neexistuje. Vytvori sa nova databaza.");
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))) {
            String line = reader.readLine();
            
            
            if (line != null && line.startsWith("NEXT_ID:")) {
                int nextId = Integer.parseInt(line.split(":")[1]);
                db.setNextId(nextId);
            }

            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Employee emp = Employee.fromCsv(line);
                    db.loadEmployee(emp);
                }
            }
        }
        System.out.println("Data boli uspesne nacitane z: " + DB_FILE);
        return true;
    }

    
    static class DatabaseSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private final List<Employee> employees;
        private final int nextId;

        DatabaseSnapshot(List<Employee> employees, int nextId) {
            this.employees = employees;
            this.nextId = nextId;
        }

        List<Employee> getEmployees() { return employees; }
        int getNextId() { return nextId; }
    }
}