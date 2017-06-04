const int switchPin = 2;
const int motorPin = 9;

void setup() {
  pinMode(switchPin, INPUT);
  pinMode(motorPin, OUTPUT);
}

void loop() {
  digitalWrite(motorPin, digitalRead(switchPin));
}
