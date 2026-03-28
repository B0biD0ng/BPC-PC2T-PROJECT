package model;

import java.util.List;
import java.util.Map;

public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(String firstName, String lastName, int birthYear) {
        super(firstName, lastName, birthYear);
    }

    public SecuritySpecialist(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public String getGroupName() {
        return "SecuritySpecialist";
    }

    /**
     * Skill: Vypočíta rizikové skóre tohto špecialistu.
     *
     * Vlastný algoritmus:
     *  - Základné skóre = počet kolegov * 10
     *  - Za každú POOR   spoluprácu: +15 (veľké riziko konfliktu)
     *  - Za každú AVERAGE spoluprácu: +5
     *  - Za každú GOOD   spoluprácu: -5  (dobré vzťahy znižujú riziko)
     *  - Minimálne skóre = 0
     *
     *  Kategórie:
     *    0–20  : Nízke riziko  (LOW)
     *    21–50 : Stredné riziko (MEDIUM)
     *    51+   : Vysoké riziko (HIGH)
     */
    @Override
    public void runSkill(Map<Integer, Employee> allEmployees) {
        System.out.println("\n=== SKILL: SecuritySpecialist – Rizikové skóre ===");

        List<Collaboration> collabs = getCollaborations();
        int totalColleagues = collabs.size();

        int score = totalColleagues * 10;

        int poorCount = 0, avgCount = 0, goodCount = 0;
        for (Collaboration c : collabs) {
            switch (c.getLevel()) {
                case POOR    -> { score += 15; poorCount++; }
                case AVERAGE -> { score += 5;  avgCount++; }
                case GOOD    -> { score -= 5;  goodCount++; }
            }
        }
        score = Math.max(0, score);

        String category;
        if (score <= 20) {
            category = "🟢 NÍZKE";
        } else if (score <= 50) {
            category = "🟡 STREDNÉ";
        } else {
            category = "🔴 VYSOKÉ";
        }

        System.out.printf("Zamestnanec : %s %s (ID:%d)%n",
                getFirstName(), getLastName(), getId());
        System.out.printf("Kolegov     : %d  (dobrá:%d  priemerná:%d  špatná:%d)%n",
                totalColleagues, goodCount, avgCount, poorCount);
        System.out.printf("Rizikové skóre : %d  →  %s RIZIKO%n", score, category);
        System.out.println("================================================\n");
    }
}
