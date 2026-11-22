package impl;

import java.util.Map;
import java.time.Duration;

class Report {
    private Duration timeSpent;
    private Map<Record, Integer> duplicates;
    private Map<Integer, Integer> floors;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time processing: " + timeSpent.toMillis()/1000.f + "s\n");
        sb.append("Duplicates:\n");
        duplicates.forEach((k, v) -> sb.append("\t" + k + " repeated " + v + " times\n"));
        sb.append("Floors:\n");
        floors.forEach((k, v) -> sb.append("\t" + k + " floors buildings is " + v + " amount\n"));
        return sb.toString();
    }

    public Report(Duration timeSpent, Map<Record, Integer> duplicates, Map<Integer, Integer> floors) {
        this.timeSpent = timeSpent;
        this.duplicates = duplicates;
        this.floors = floors;
    }
}
