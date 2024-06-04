package com.example.candycrush;


public class Score {
    private String nom, theme;
    private int  temps, score;
    public Score(String nom, String theme, int temps, int score){
        this.nom=nom;
        this.theme=theme;
        this.temps=temps;
        this.score=score;
    }

    public String getNom(){
        return nom;
    }

    public String getTheme(){
        return theme;
    }


    public int getTemps(){
        return temps;
    }

    public void setScore(int score){
        this.score=score;
    }

    public int getScore(){
        return score;
    }
}
