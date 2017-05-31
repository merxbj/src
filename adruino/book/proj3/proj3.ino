const int sensorPin = 0;
const float baselineTemp = 23.0;
const int led1Pin = 2;
const int led2Pin = 3;
const int led3Pin = 4;

void setup() {
  Serial.begin(9600);
  for (int pin = led1Pin; pin <= led3Pin; pin++)
  {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
  }
}

void loop() {
  int sensorVal = analogRead(sensorPin);
  
  float voltage = (sensorVal / 1024.0) * 5.0;
  char voltageString[10] = {0};
  dtostrf(voltage,5,3,voltageString);

  float temperature = (voltage - 0.5) * 100;
  char temperatureString[10] = {0};
  dtostrf(temperature,5,3,temperatureString);

  if (temperature < baselineTemp)
  {
    digitalWrite(led1Pin, LOW);
    digitalWrite(led2Pin, LOW);
    digitalWrite(led3Pin, LOW);
  }
  else if (temperature >= baselineTemp && temperature < baselineTemp + 2.0)
  {
    digitalWrite(led1Pin, HIGH);
    digitalWrite(led2Pin, LOW);
    digitalWrite(led3Pin, LOW);
  } 
  else if (temperature >= baselineTemp + 2.0 && temperature < baselineTemp + 4.0)
  {
    digitalWrite(led1Pin, HIGH);
    digitalWrite(led2Pin, HIGH);
    digitalWrite(led3Pin, LOW);
  }
  else
  {
    digitalWrite(led1Pin, HIGH);
    digitalWrite(led2Pin, HIGH);
    digitalWrite(led3Pin, HIGH);
  }

  char buffer[100] = {0};
  sprintf(buffer, "Sensor value: %d, Voltage = %s, Temperature = %s", sensorVal, voltageString, temperatureString);
  Serial.println(buffer);
  delay(1);
}
