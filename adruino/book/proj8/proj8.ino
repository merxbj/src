const int switchPin = 8;
unsigned long previousTime = 0;
int prevSwitchState = LOW;
int led = 2;
long interval = 1000;

void setup() {
  for (int pin = 2; pin < 8; pin++) {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
  }
  
  pinMode(switchPin, INPUT);

  Serial.begin(9600);

  Serial.println("Setup finalized");
}

void flash()
{
  for (int repeat = 0; repeat < 5; repeat++)
  {
    for (int pin = 2; pin < 8; pin++) {
        digitalWrite(pin, LOW);
    }
  
    delay(50);
  
    for (int pin = 2; pin < 8; pin++) {
        digitalWrite(pin, HIGH);
    }
  
    delay(200);
  }
}

void loop() {
  unsigned long currentTime = millis();
  if (currentTime - previousTime > interval) {
    previousTime = currentTime;
    
    Serial.print("Timer just ticked. Lighting up diode ");
    Serial.println(led);
    
    digitalWrite(led, HIGH);

    if (led < 8) {
      led++;
    } else if (led == 8) {
      Serial.print("Time is up! Flashing...");
      flash();
      led++;
    }
  }

  int switchState = digitalRead(switchPin);
  if (switchState != prevSwitchState) {

    Serial.println("Tilt detected! Resetting...");
    
    for (int pin = 2; pin < 8; pin++) {
      digitalWrite(pin, LOW);
    }

    led = 2;
    previousTime = currentTime;
  }

  prevSwitchState = switchState;
}
