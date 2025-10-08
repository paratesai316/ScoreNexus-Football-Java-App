package com.scorenexus.model;

public class Player {
    private String name;
    private int number;
    private boolean isSubstitute;
    private int goals = 0;
    private int assists = 0;
    private int yellowCards = 0;
    private boolean hasRedCard = false;
    private long minutesPlayed = 0;
    
    private boolean wasSubstitutedOut = false;
    private final boolean wasOriginallySubstitute;


    public Player(String name, int number, boolean isSubstitute) {
        this.name = name;
        this.number = number;
        this.isSubstitute = isSubstitute;
        this.wasOriginallySubstitute = isSubstitute;
    }

    public String getName() { return name; }
    public int getNumber() { return number; }
    public boolean isSubstitute() { return isSubstitute; }
    public void setSubstitute(boolean substitute) { isSubstitute = substitute; }
    public int getGoals() { return goals; }
    public void addGoal() { this.goals++; }
    public int getAssists() { return assists; }
    public void addAssist() { this.assists++; }
    public int getYellowCards() { return yellowCards; }
    public void addYellowCard() { this.yellowCards++; }
    public boolean hasRedCard() { return hasRedCard; }
    public void setHasRedCard(boolean hasRedCard) { this.hasRedCard = hasRedCard; }
    public long getMinutesPlayed() { return minutesPlayed; }
    public void setMinutesPlayed(long minutesPlayed) { this.minutesPlayed = minutesPlayed; }
    
    public boolean wasSubstitutedOut() { return wasSubstitutedOut; }
    public void setWasSubstitutedOut(boolean wasSubstitutedOut) { this.wasSubstitutedOut = wasSubstitutedOut; }
    public boolean wasSubstitutedIn() {
        return wasOriginallySubstitute && !isSubstitute;
    }
}