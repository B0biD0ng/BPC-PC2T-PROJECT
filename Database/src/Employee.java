import java.io.*;
import java.util.*;

public abstract class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String firstName;
    protected String lastName;
    protected int birthYear;
    protected List<Cooperation> cooperations; 

    public Employee(int id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.cooperations = new ArrayList<>();
    }

  
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getBirthYear() { return birthYear; }
    public String getFullName() { return firstName + " " + lastName; }
    public List<Cooperation> getCooperations() { return cooperations; }

    public abstract String getGroupName();
    public abstract String useSkill(EmployeeDatabase db);

    
    public void addCooperation(int colleagueId, CooperationLevel level) {
        for (Cooperation c : cooperations) {
            if (c.getColleagueId() == colleagueId) {
                c.setLevel(level);
                return;
            }
        }
        cooperations.add(new Cooperation(colleagueId, level));
    }

    public void removeCooperation(int colleagueId) {
        cooperations.removeIf(c -> c.getColleagueId() == colleagueId);
    }

    public Set<Integer> getColleagueIds() {
        Set<Integer> ids = new HashSet<>();
        for (Cooperation c : cooperations) ids.add(c.getColleagueId());
        return ids;
    }

    public Map<CooperationLevel, Integer> getCooperationStats() {
        Map<CooperationLevel, Integer> stats = new EnumMap<>(CooperationLevel.class);
        for (CooperationLevel lvl : CooperationLevel.values()) stats.put(lvl, 0);
        for (Cooperation c : cooperations) stats.merge(c.getLevel(), 1, Integer::sum);
        return stats;
    }

    public CooperationLevel getDominantLevel() {
        Map<CooperationLevel, Integer> stats = getCooperationStats();
        return stats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    
    public String toCsv() {
        String type = (this instanceof DataAnalyst) ? "1" : "2";
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";").append(firstName).append(";").append(lastName).append(";")
          .append(birthYear).append(";").append(type).append(";");
        
        for (int i = 0; i < cooperations.size(); i++) {
            Cooperation c = cooperations.get(i);
            sb.append(c.getColleagueId()).append(":").append(c.getLevel().name());
            if (i < cooperations.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    public static Employee fromCsv(String line) {
        String[] parts = line.split(";", -1);
        int id = Integer.parseInt(parts[0]);
        String fn = parts[1];
        String ln = parts[2];
        int year = Integer.parseInt(parts[3]);
        String type = parts[4];

        Employee emp = type.equals("1") 
                ? new DataAnalyst(id, fn, ln, year) 
                : new SecuritySpecialist(id, fn, ln, year);

        if (parts.length > 5 && !parts[5].isEmpty()) {
            for (String cp : parts[5].split(",")) {
                String[] pair = cp.split(":");
                emp.addCooperation(Integer.parseInt(pair[0]), CooperationLevel.valueOf(pair[1]));
            }
        }
        return emp;
    }

    @Override
    public String toString() {
        Map<CooperationLevel, Integer> stats = getCooperationStats();
        return String.format(
            "ID: %d | %s | Rok nar.: %d | Skupina: %s | Spoluprace: %d (dobra: %d, prumerna: %d, spatna: %d)",
            id, getFullName(), birthYear, getGroupName(), cooperations.size(),
            stats.get(CooperationLevel.GOOD),
            stats.get(CooperationLevel.AVERAGE),
            stats.get(CooperationLevel.POOR)
        );
    }
}
