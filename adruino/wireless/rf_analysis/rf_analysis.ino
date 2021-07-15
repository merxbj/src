#include <LiquidCrystal.h>
#include <RH_ASK.h>
#include <SPI.h>

const int rs = 9, en = 10, d4 = 5, d5 = 6, d6 = 7, d7 = 8;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

RH_ASK driver(2000, 12, 0, 0, false);
static bool rh_initialized = false;

void setup() {
  pinMode(2, OUTPUT);

  Serial.begin(9600); // Debugging only
  
  lcd.begin(16,2);
  lcd.clear();

  rh_initialized = driver.init();
  
  if (!rh_initialized) {
    Serial.println("RH_ASK initialized failed!");
  }

  lcd.setCursor(0,0);
  lcd.print("Waiting...");
}

void loop() {
  uint8_t buf[12] = {0};
  uint8_t buflen = sizeof(buf); 

  if (driver.recv(buf, &buflen)) // Non-blocking
  {
    // Message with a good checksum received, dump it.
    Serial.println((char*)buf);

    lcd.setCursor(0, 0);
    lcd.print("Msg Received:");

    lcd.setCursor(0, 1);
    lcd.print((char*)buf);
  }
}
