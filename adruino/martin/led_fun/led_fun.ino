#include <LiquidCrystal.h>

static bool led_enabled = false;
static long last_ms = 0;
static int delay_ms = 1000;
static bool button_pushed = false;

const int rs = 9, en = 10, d4 = 5, d5 = 6, d6 = 7, d7 = 8;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

void setup() {
  pinMode(2, OUTPUT);
  pinMode(3, INPUT);
  pinMode(4, INPUT);

  lcd.begin(16,2);
  lcd.print("Current Delay:");
  updateDisplay(0); // force update
}

void loop() {

  int last_delay_ms = delay_ms;
  
  processInput();

  updateDisplay(last_delay_ms);

  blink();
}

void processInput()
{
  int slower = digitalRead(3);
  int faster = digitalRead(4);

  if (slower == HIGH && button_pushed == false) {
    button_pushed = true;

    if (delay_ms < 500) {
      delay_ms += 100;
    }
    else {
      delay_ms += 500;
    }
  }
  else if (faster == HIGH && button_pushed == false) {
    button_pushed = true;

    if (delay_ms <= 500) {
      delay_ms -= 100;
      if (delay_ms <= 0) {
        delay_ms = 100;
      }
    }
    else {
      delay_ms -= 500;
    }
  }
  else if (slower == LOW && faster == LOW && button_pushed == true) {
    button_pushed = false;
  }
}

void updateDisplay(int last_delay_ms) {
  if (last_delay_ms != delay_ms) {
    char buf[10] = {0};
    sprintf(buf, "%4dms", delay_ms);
    lcd.setCursor(0, 1);
    lcd.print(buf);
  }
}

void blink() {
  long current_ms = millis();
  if (current_ms - last_ms >= delay_ms) {
    last_ms = current_ms;
    
    if (led_enabled == true) {
      led_enabled = false;
      digitalWrite(2, LOW);
    }
    else {
      led_enabled = true;
      digitalWrite(2, HIGH);
    }
  }
}
