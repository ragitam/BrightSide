package com.triplefighter.brightside.Model;

import java.util.HashMap;
import java.util.Map;

public class DataStatistic {
    private float usage;
    private int cost;

    public DataStatistic(){}

    public DataStatistic(float usage, int cost) {
        this.usage = usage;
        this.cost = cost;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usage", usage);
        result.put("cost", cost);

        return result;
    }
}
