#include <Servo.h>

Servo servo;

const int piezo = A0;
const int switchPin = 2;
const int yellowLed = 3;
const int greenLed = 4;
const int redLed = 5;

const int quietKnock = 10;
const int loudKnock = 100;

bool locked = false;
int numberOfKnocks = 0;

void setup() {
  servo.attach(9);
  pinMode(yellowLed, OUTPUT);
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  pinMode(switchPin, INPUT);
  Serial.begin(9600);

  digitalWrite(greenLed, HIGH);
  servo.write(0);

  Serial.println("The box is unlocked!");
}

void loop() {
  
  if (!locked)
  {
    int switchVal = digitalRead(switchPin);
    if (switchVal == HIGH)
    {
      locked = true;
      numberOfKnocks = 0;
      digitalWrite(greenLed, LOW);
      digitalWrite(redLed, HIGH);
      servo.write(90);
      Serial.println("The box is locked!");
      delay(1000);
    }
  }

  if (locked)
  {
    int knockVal = analogRead(piezo);
    if (numberOfKnocks < 3 && knockVal > 0)
    {
      if (checkForKnock(knockVal))
      {
        numberOfKnocks++;
      }
      Serial.print(3 - numberOfKnocks);
      Serial.println(" more knocks to go");
    }

    if (numberOfKnocks >= 3)
    {
      locked = false;
      servo.write(0);
      delay(20);
      digitalWrite(greenLed, HIGH);
      digitalWrite(redLed, LOW);
      Serial.println("The box is unlocked!");
    }
  }
}

bool checkForKnock(int value)
{
  if (value >=0 && value <= 5)
  {
    return false;
  }
  else if (value > quietKnock && value < loudKnock)
  {
    digitalWrite(yellowLed, HIGH);
    delay(50);
    digitalWrite(yellowLed, LOW);
    Serial.print("Valid knock of value ");
    Serial.println(value);

    return true;
  }
  else
  {
    Serial.print("Bad knock value ");
    Serial.println(value);
    return false;
  }
}

