import java.util.Set;

public class DataAnalyst extends Employee {

    public DataAnalyst(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public String getGroupName() {
        return "Datovy analytik";
    }

    /**
     * Dovednost: najde spolupracovníka, se kterým má nejvíce společných kolegů.
     */
    @Override
    public String useSkill(EmployeeDatabase db) {
        Set<Integer> myColleagues = getColleagueIds();
        if (myColleagues.isEmpty()) {
            return getFullName() + " nema zadne spolupracovniky.";
        }

        Employee bestMatch = null;
        int bestCount = -1;

        for (Employee emp : db.getAllEmployees()) {
            if (emp.getId() == this.id) continue;
            if (!myColleagues.contains(emp.getId())) continue;

            Set<Integer> theirColleagues = emp.getColleagueIds();
            int common = 0;
            for (int cid : myColleagues) {
                if (theirColleagues.contains(cid)) common++;
            }

            if (common > bestCount) {
                bestCount = common;
                bestMatch = emp;
            }
        }

        if (bestMatch == null || bestCount == 0) {
            return getFullName() + ": zadny spolupracovnik nema spolecne kolegy.";
        }

        return String.format(
            "%s: nejvice spolecnych spolupracovniku ma s %s (ID %d) - celkem %d spolecnych.",
            getFullName(), bestMatch.getFullName(), bestMatch.getId(), bestCount
        );
    }
}
