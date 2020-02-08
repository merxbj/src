const int controlPin1 = 2;
const int controlPin2 = 3;
const int enablePin = 9;
const int directionSwitchPin = 4;
const int onOffSwitchStateSwitchPin = 5;
const int potPin = A0;

int previousOnOffSwitchState = 0;
int previousDirectionSwitchState = 0;

int motorEnabled = 0;
int motorDirection = 1;

void setup() {
    pinMode(controlPin1, OUTPUT);
    pinMode(controlPin2, OUTPUT);
    pinMode(enablePin, OUTPUT);
    pinMode(directionSwitchPin, INPUT);
    pinMode(onOffSwitchStateSwitchPin, INPUT);

    digitalWrite(enablePin, LOW);
}

void loop() {
  int onOffSwitchState = digitalRead(onOffSwitchStateSwitchPin);
  
  delay(1);
  
  int directionSwitchState = digitalRead(directionSwitchPin);

  int motorSpeed = analogRead(potPin) / 4;

  if ((onOffSwitchState != previousOnOffSwitchState) && (onOffSwitchState == HIGH))
  {
    motorEnabled = !motorEnabled;
  }

  if ((directionSwitchState != previousDirectionSwitchState) && (directionSwitchState == HIGH))
  {
    motorDirection = !motorDirection;
  }

  if (motorDirection == 1)
  {
    digitalWrite(controlPin1, HIGH);
    digitalWrite(controlPin2, LOW);
  }
  else
  {
    digitalWrite(controlPin1, LOW);
    digitalWrite(controlPin2, HIGH);
  }

  if (motorEnabled == 1)
  {
    analogWrite(enablePin, motorSpeed);
  }
  else
  {
    analogWrite(enablePin, 0);
  }

  previousOnOffSwitchState = onOffSwitchState;
  previousDirectionSwitchState = directionSwitchState;
}
