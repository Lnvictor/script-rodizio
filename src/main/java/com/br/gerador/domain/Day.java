package com.br.gerador.domain;

public class Day {
    private String weekDay;
    private String ordinalDay;
    private boolean isSaunday;
    private Organista cultOraganista;
    private Organista youngOraganista;

    public Day(String weekDay, boolean isSaunday, Organista cultOraganista, Organista youngOraganista, String ordinalDay) {
        this.ordinalDay = ordinalDay;
        this.weekDay = weekDay;
        this.isSaunday = isSaunday;
        this.cultOraganista = cultOraganista;
        this.youngOraganista = youngOraganista;
    }


    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public boolean isSaunday() {
        return isSaunday;
    }

    public void setSaunday(boolean saunday) {
        isSaunday = saunday;
    }

    public Organista getCultOraganista() {
        return cultOraganista;
    }

    public void setCultOraganista(Organista cultOraganista) {
        this.cultOraganista = cultOraganista;
    }

    public Organista getYoungOraganista() {
        return youngOraganista;
    }

    public void setYoungOraganista(Organista youngOraganista) {
        this.youngOraganista = youngOraganista;
    }

    public String getOrdinalDay() {
        return ordinalDay;
    }

    public void setOrdinalDay(String ordinalDay) {
        this.ordinalDay = ordinalDay;
    }
}
