package co.kr.molegame;

public class Obstacle {
    private String step1 = null;
    private String step2 = null;
    private String step3 = null;

    public Obstacle(String step1){
        this.step1 = step1;
    }
    public Obstacle(String step1, String step2){
        this.step1 = step1;
        this.step2 = step2;
    }
    public Obstacle(String step1, String step2, String step3){
        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;
    }

    public String getStep1() {
        return step1;
    }

    public void setStep1(String step1) {
        this.step1 = step1;
    }

    public String getStep2() {
        return step2;
    }

    public void setStep2(String step2) {
        this.step2 = step2;
    }

    public String getStep3() {
        return step3;
    }

    public void setStep3(String step3) {
        this.step3 = step3;
    }
}
