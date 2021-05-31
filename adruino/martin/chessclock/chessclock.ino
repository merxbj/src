#include <LiquidCrystal.h>

#define MS_IN_SECOND 1000UL
#define MS_IN_MINUTE (MS_IN_SECOND * 60UL)
#define MS_IN_HOUR (MS_IN_MINUTE * 60UL)

static bool button_pushed = false;

static bool stopwatch_enabled = false;
static bool p1_active = false;
static bool p2_active = false;
static unsigned long elapsed_ms_p1 = 15 * MS_IN_MINUTE;
static unsigned long elapsed_ms_p2 = 15 * MS_IN_MINUTE;
static unsigned long last_ms = 0;

const int rs = 9, en = 10, d4 = 5, d5 = 6, d6 = 7, d7 = 8;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

void setup() {
  pinMode(2, OUTPUT);
  pinMode(3, INPUT);
  pinMode(4, INPUT);
  pinMode(11, OUTPUT);

  lcd.begin(16,2);
  updateDisplay();
}

void loop() {

  processInput();

  updateStopwatch();

  updateDisplay();

  updateLed();
}

void processInput()
{
  int start = digitalRead(3);
  int reset = digitalRead(4);

  if (start == HIGH && !button_pushed) {
    button_pushed = true;

    if (!stopwatch_enabled) {
      stopwatch_enabled = true;
      p1_active = true;
      p2_active = false;
      last_ms = 0;
    } else {
      elapsed_ms_p1 += (p1_active ? 5 : 0) * MS_IN_SECOND;
      elapsed_ms_p2 += (p2_active ? 5 : 0) * MS_IN_SECOND;

      p1_active = !p1_active;
      p2_active = !p2_active;
    }
  } else if (reset == HIGH && !button_pushed) {
    button_pushed = true;

    stopwatch_enabled = false;
    elapsed_ms_p1 = 15 * MS_IN_MINUTE;
    elapsed_ms_p2 = 15 * MS_IN_MINUTE;
  } else if (start == LOW && reset == LOW && button_pushed) {
    button_pushed = false;
  }
}

void updateStopwatch()
{
  if (stopwatch_enabled) {
    unsigned long current_ms = millis();
    
    // ignore first update, until we have fresh reference time in last_ms
    if (last_ms != 0) {
      if (p1_active) {
        elapsed_ms_p1 -= (current_ms - last_ms);
        if (elapsed_ms_p1 < 0) {
          elapsed_ms_p1 = 0;
        }
      } else {
        elapsed_ms_p2 -= (current_ms - last_ms);
        if (elapsed_ms_p2 < 0) {
          elapsed_ms_p2 = 0;
        }
      }
    }

    last_ms = current_ms;
  }
}

void updateDisplay() {
    unsigned long remainder;
    unsigned int hours;
    unsigned int minutes;
    unsigned int seconds;

    breakDownMilis(elapsed_ms_p1, hours, minutes, seconds, remainder);
    char buf_p1[20] = {0};
    sprintf(buf_p1, "%1u:%02u:%02u.%03u", hours, minutes, seconds, remainder);

    breakDownMilis(elapsed_ms_p2, hours, minutes, seconds, remainder);
    char buf_p2[20] = {0};
    sprintf(buf_p2, "%1u:%02u:%02u.%03u", hours, minutes, seconds, remainder);
    
    lcd.setCursor(0, 0);
    lcd.print(buf_p1);
    lcd.setCursor(0, 1);
    lcd.print(buf_p2);
}

void breakDownMilis(unsigned long milis, unsigned int& hours, unsigned int& minutes, unsigned int& seconds, unsigned long& remainder) {
    remainder = milis;
    
    hours = remainder / MS_IN_HOUR;
    remainder -= (hours * MS_IN_HOUR);
    
    minutes = remainder / MS_IN_MINUTE;
    remainder -= (minutes * MS_IN_MINUTE);

    seconds = remainder / MS_IN_SECOND;
    remainder -= (seconds * MS_IN_SECOND);
}

void updateLed() {
  if (stopwatch_enabled) {
    digitalWrite(2, p1_active ? HIGH : LOW);
    digitalWrite(11, p1_active ? LOW : HIGH);
  } else {
    digitalWrite(2, LOW);
    digitalWrite(11, LOW);
  }
}
