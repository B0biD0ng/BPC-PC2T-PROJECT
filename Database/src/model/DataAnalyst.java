package model;

import java.util.*;

public class DataAnalyst extends Employee {

    public DataAnalyst(String firstName, String lastName, int birthYear) {
        super(firstName, lastName, birthYear);
    }

    public DataAnalyst(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public String getGroupName() {
        return "DataAnalyst";
    }

    /**
     * Skill: Nájde spolupracovníka, s ktorým má tento analytik
     * NAJVIAC SPOLOČNÝCH kolegov.
     *
     * Algoritmus:
     *  1. Zoberieme množinu ID všetkých kolegov tohto analytika.
     *  2. Pre každého kolegu (ktorý je tiež v databáze) vypočítame prienik
     *     jeho kolegov s našimi kolegami.
     *  3. Vypíšeme toho s najväčším prienikom.
     */
    @Override
    public void runSkill(Map<Integer, Employee> allEmployees) {
        System.out.println("\n=== SKILL: DataAnalyst – Spoločných kolegov ===");

        List<Collaboration> myCollabs = getCollaborations();
        if (myCollabs.isEmpty()) {
            System.out.println("Tento analytik nemá žiadnych kolegov.");
            return;
        }

        // Množina ID mojich kolegov
        Set<Integer> myColleagueIds = new HashSet<>();
        for (Collaboration c : myCollabs) {
            myColleagueIds.add(c.getColleagueId());
        }

        int bestCount = -1;
        Employee bestColleague = null;

        for (Collaboration c : myCollabs) {
            Employee colleague = allEmployees.get(c.getColleagueId());
            if (colleague == null) continue;

            // Počet spoločných kolegov
            int common = 0;
            for (Collaboration cc : colleague.getCollaborations()) {
                if (myColleagueIds.contains(cc.getColleagueId())
                        && cc.getColleagueId() != getId()) {
                    common++;
                }
            }

            if (common > bestCount) {
                bestCount = common;
                bestColleague = colleague;
            }
        }

        if (bestColleague == null || bestCount <= 0) {
            System.out.println("Žiadny spoločný kolega nebol nájdený.");
        } else {
            System.out.printf("Kolega s najviac spoločnými kolegami: %s %s (ID:%d)%n",
                    bestColleague.getFirstName(), bestColleague.getLastName(), bestColleague.getId());
            System.out.printf("Počet spoločných kolegov: %d%n", bestCount);
        }
        System.out.println("===============================================\n");
    }
}
