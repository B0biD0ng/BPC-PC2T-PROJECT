package io;

import model.*;

import java.io.*;

public class FileManager {

    /**
     * Uloží zamestnanca do textového súboru.
     */
    public static void saveEmployee(Employee e, String filename) {
        e.saveToFile(filename);
    }

    /**
     * Načíta zamestnanca zo textového súboru.
     *
     * Formát súboru:
     *   DataAnalyst|1|Jana|Nováková|1990
     *   COLLAB|3|Petr Svoboda|GOOD
     *   COLLAB|5|Eva Malá|POOR
     *   END
     */
    public static Employee loadEmployee(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine();
            if (header == null) throw new IOException("Prázdny súbor.");

            String[] parts = header.split("\\|");
            if (parts.length < 5) throw new IOException("Neplatný formát hlavičky.");

            String group     = parts[0];
            int id           = Integer.parseInt(parts[1].trim());
            String firstName = parts[2].trim();
            String lastName  = parts[3].trim();
            int birthYear    = Integer.parseInt(parts[4].trim());

            Employee emp;
            switch (group) {
                case "DataAnalyst"       -> emp = new DataAnalyst(id, firstName, lastName, birthYear);
                case "SecuritySpecialist"-> emp = new SecuritySpecialist(id, firstName, lastName, birthYear);
                default -> throw new IOException("Neznáma skupina: " + group);
            }

            String line;
            while ((line = br.readLine()) != null && !line.equals("END")) {
                if (line.startsWith("COLLAB|")) {
                    String[] cp = line.split("\\|");
                    if (cp.length < 4) continue;
                    int colId       = Integer.parseInt(cp[1].trim());
                    String colName  = cp[2].trim();
                    CollaborationLevel lvl = CollaborationLevel.fromString(cp[3].trim());
                    emp.addCollaboration(new Collaboration(colId, colName, lvl));
                }
            }

            System.out.println("Zamestnanec načítaný zo súboru: " + filename);
            return emp;

        } catch (IOException e) {
            System.err.println("Chyba pri načítaní súboru: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Chybný formát čísla v súbore: " + e.getMessage());
            return null;
        }
    }
}
