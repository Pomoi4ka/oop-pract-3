package impl;

import java.util.Map;
import java.util.HashMap;
import java.time.Duration;

class Report {
    public Duration timeSpent;
    public Map<Record, Integer> duplicates;
    public Map<Integer, Integer> floors;

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

    public Report() {
        duplicates = new HashMap<>();
        floors = new HashMap<>();
    }
}
