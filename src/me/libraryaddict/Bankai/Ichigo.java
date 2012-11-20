package me.libraryaddict.Bankai;

public class Ichigo {
  private String p;
  private double bankaiTime = 60;
  private boolean hallow = false;
  private double hallowTime = 0;
  private double swordAttack = 0;
  private boolean hallowed = false;
  
  Ichigo(String player) {
    p = player;
  }
  
  double getBankaiTime() {
    return bankaiTime;
  }
  
  String getPlayer() {
    return p;
  }
  
  void setBankaiTime(int bankai) {
    bankaiTime = bankai;
  }
  
  void setHallow(boolean isHallow) {
    hallow = isHallow;
    hallowed = true;
  }
  
  boolean hasHallowed() {
    return hallowed;
  }
  
  boolean getHallow() {
    return hallow;
  }
  
  double getHallowTime() {
    return hallowTime;
  }
  
  void setHallowTime(int time) {
    hallowTime = time;
  }
  
  void decreaseBankai() {
    bankaiTime += -0.05;
  }
  
  void decreaseHallow() {
    hallowTime += -0.05;
  }
  
  void decreasePowerwave() {
    swordAttack += -0.05;
  }

  double getPowerwave() {
    return swordAttack;
  }
  
  void setPowerwave(double power) {
    swordAttack = power;
  }
}
