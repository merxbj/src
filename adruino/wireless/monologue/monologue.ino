#include <LiquidCrystal.h>
#include <RH_ASK.h>
#include <SPI.h>

const int rs = 9, en = 10, d4 = 5, d5 = 6, d6 = 7, d7 = 8;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

RH_ASK rh(2000, 0, 12, 0, false);

void setup() {
  pinMode(2, OUTPUT);
  
  lcd.begin(16,2);
  
  rh.init();
}

void loop() {

  digitalWrite(2, HIGH);
  
  char buf[20] = "Hello World!";

  rh.send((uint8_t*)buf, strlen(buf));
  rh.waitPacketSent();

  digitalWrite(2, LOW);

  delay(1000);
}
