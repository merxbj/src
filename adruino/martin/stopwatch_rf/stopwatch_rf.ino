#include <LiquidCrystal.h>
#include <RH_ASK.h>
#include <SPI.h>


static bool button_pushed = false;

static bool stopwatch_enabled = false;
static unsigned long elapsed_ms = 0;
static unsigned long last_ms = 0;

#define MS_IN_SECOND 1000UL
#define MS_IN_MINUTE (MS_IN_SECOND * 60UL)
#define MS_IN_HOUR (MS_IN_MINUTE * 60UL)

const int rs = 9, en = 10, d4 = 5, d5 = 6, d6 = 7, d7 = 8;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

RH_ASK rh(2000, 0, 12, 0, false);
static bool rh_initialized = false;

void setup() {
  pinMode(2, OUTPUT);
  pinMode(3, INPUT);
  pinMode(4, INPUT);
  pinMode(11, OUTPUT);

  lcd.begin(16,2);
  lcd.print("Stopwatch:");
  updateDisplay();

  rh_initialized = rh.init();
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
      last_ms = 0;
    } else {
      stopwatch_enabled = false;
    }
  } else if (reset == HIGH && !button_pushed) {
    button_pushed = true;

    stopwatch_enabled = false;

    transmit();
    
    elapsed_ms = 0;
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
      elapsed_ms += (current_ms - last_ms);
    }

    last_ms = current_ms;
  }
}

void formatElapsedTime(char* buf, const size_t buf_size) {
  unsigned long milliseconds = elapsed_ms;
  
  unsigned int hours = milliseconds / MS_IN_HOUR;
  milliseconds -= (hours * MS_IN_HOUR);
  
  unsigned int minutes = milliseconds / MS_IN_MINUTE;
  milliseconds -= (minutes * MS_IN_MINUTE);

  unsigned int seconds = milliseconds / MS_IN_SECOND;
  milliseconds -= (seconds * MS_IN_SECOND);
  
  sprintf(buf, "%1u:%02u:%02u.%03lu", hours, minutes, seconds, milliseconds);
  buf[buf_size - 1] = 0;
}

void updateDisplay() {
  char buf[20] = {0};
  formatElapsedTime(buf, sizeof(buf));
  lcd.setCursor(0, 1);
  lcd.print(buf);
}

void updateLed() {
  if (stopwatch_enabled) {
    digitalWrite(2, HIGH);
  } else {
    digitalWrite(2, LOW);
  }
}

void transmit() {

  if (!rh_initialized) {
    return;
  }

  digitalWrite(11, HIGH);
  
  char buf[20] = {0};
  formatElapsedTime(buf, sizeof(buf));

  rh.send((uint8_t*)buf, strlen(buf));
  rh.waitPacketSent();
  
  digitalWrite(11, LOW);
}
