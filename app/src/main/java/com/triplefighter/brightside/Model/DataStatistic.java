package com.triplefighter.brightside.Model;

import java.util.HashMap;
import java.util.Map;

public class DataStatistic {
    public float usage;
    public float cost;
    public float usageStat;

    public DataStatistic(){}

    public DataStatistic(float usage, float cost) {
        this.usage = usage;
        this.cost = cost;
    }

    public DataStatistic(float usage, float cost, float usageStat) {
        this.usage = usage;
        this.cost = cost;
        this.usageStat = usageStat;
    }

    public float getUsage() {
        return usage;
    }

    public float getCost() {
        return cost;
    }

    public float getUsageStat() {
        return usageStat;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usage", usage);
        result.put("cost", cost);
        result.put("usageStat", usageStat);

        return result;
    }
}
