#include <OneWire.h> 
#include <DallasTemperature.h>
#include <LiquidCrystal.h>
#include <float.h>

#define ONE_WIRE_BUS 2
#define TMP_SENSORS 1

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
LiquidCrystal lcd(12, 11, 6, 5, 4, 3);

void updateDisplay(float currentTemp);
void welcomeDisplay();

float temperatureMax = FLT_MIN;
float temperatureMin = FLT_MAX;
int loopCount = 0;

void setup(void) 
{
  sensors.begin();
  welcomeDisplay();
}

void loop(void) 
{ 
  sensors.requestTemperatures(); // Send the command to get temperature readings 

  float temperature = sensors.getTempCByIndex(0);
  temperatureMax = max(temperature, temperatureMax);
  temperatureMin = min(temperature, temperatureMin);
  loopCount = (++loopCount) % 10;

  updateDisplay(temperature, temperatureMax, temperatureMin, loopCount / 5);
  
  delay(1000);
}

void updateDisplay(float temp, float tempMax, float tempMin, int currentCycle)
{
  lcd.setCursor(0,0);
  lcd.print("Temp Now: ");
  lcd.print(temp);

  lcd.setCursor(0,1);

  switch (currentCycle)
  {
    case 0:
      lcd.print("Temp Max: ");
      lcd.print(tempMax);
      break;
    case 1:
      lcd.print("Temp Min: ");
      lcd.print(tempMin);
      break;
  }
}

void welcomeDisplay()
{
  lcd.begin(16,2);
  lcd.print("Thermometer");
  lcd.setCursor(0,1);
  lcd.print("Initialized");

  delay(2000);

  lcd.clear();
}

