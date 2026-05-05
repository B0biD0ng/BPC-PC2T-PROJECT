import java.util.Map;

public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public String getGroupName() {
        return "Bezpecnostni specialista";
    }

    private double calculateRiskScore() {
        if (cooperations.isEmpty()) return 0.0;

        Map<CooperationLevel, Integer> stats = getCooperationStats();
        int weightedSum = stats.get(CooperationLevel.POOR) * 3
                        + stats.get(CooperationLevel.AVERAGE) * 2
                        + stats.get(CooperationLevel.GOOD) * 1;

        double avgWeight = (double) weightedSum / cooperations.size();
        double score = avgWeight * Math.log10(cooperations.size() + 1) * 10;
        return Math.round(score * 100.0) / 100.0;
    }

    @Override
    public String useSkill(EmployeeDatabase db) {
        double score = calculateRiskScore();
        Map<CooperationLevel, Integer> stats = getCooperationStats();

        String riskLevel;
        if (score == 0) riskLevel = "žádné riziko";
        else if (score < 5) riskLevel = "nízké";
        else if (score < 15) riskLevel = "střední";
        else riskLevel = "vysoké";

        return String.format(
            "%s | Rizikové skóre: %.2f | Riziko: %s | " +
            "Spolupráce: %d celkem (dobrá: %d, průměrná: %d, špatná: %d)",
            getFullName(), score, riskLevel,
            cooperations.size(),
            stats.get(CooperationLevel.GOOD),
            stats.get(CooperationLevel.AVERAGE),
            stats.get(CooperationLevel.POOR)
        );
    }
}
