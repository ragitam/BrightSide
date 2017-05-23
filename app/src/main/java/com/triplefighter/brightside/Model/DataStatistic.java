package com.triplefighter.brightside.Model;

import java.util.HashMap;
import java.util.Map;

public class DataStatistic {
    public float usage;
    public float cost;

    public DataStatistic(){}

    public DataStatistic(float usage, float cost) {
        this.usage = usage;
        this.cost = cost;
    }

    public float getUsage() {
        return usage;
    }

    public float getCost() {
        return cost;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usage", usage);
        result.put("cost", cost);

        return result;
    }
}
