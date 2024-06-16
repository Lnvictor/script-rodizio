package com.br.gerador.domain;

import java.util.List;

public class Rodizio {
    private String month;
    private List<Day> days;

    public Rodizio(String month, List<Day> days) {
        this.month = month;
        this.days = days;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
