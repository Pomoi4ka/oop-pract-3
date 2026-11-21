package impl;

import java.util.Map;
import java.util.HashMap;
import java.time.Duration;

class Report {
    public Duration timeSpent;
    public Map<Record, Integer> duplicates;
    public Map<Integer, Integer> floors;

    public Report() {
        duplicates = new HashMap<>();
        floors = new HashMap<>();
    }
}
