#include <OneWire.h> 
#include <DallasTemperature.h>
#include <LiquidCrystal.h>

#define ONE_WIRE_BUS 2

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
LiquidCrystal lcd(12, 11, 6, 5, 4, 3);

const float TEMPERATURE_NOT_INITIALIZED = -500.0;
float previousTemp = TEMPERATURE_NOT_INITIALIZED;

void updateDisplay(float currentTemp);
void welcomeDisplay();

void setup(void) 
{

  Serial.begin(9600); 
  Serial.println("Dallas Temperature IC Control Library Demo"); 

  sensors.begin(); 
  
  welcomeDisplay();
}

void loop(void) 
{ 
  Serial.print(" Requesting temperatures...\t"); 
  sensors.requestTemperatures(); // Send the command to get temperature readings 
  Serial.print("DONE, "); 

  float currentTemp = sensors.getTempCByIndex(0);
  
  Serial.print("Temperature is: ");
  Serial.println(currentTemp);

  updateDisplay(currentTemp);
  
  previousTemp = currentTemp;
  delay(1000);
}

void updateDisplay(float currentTemp)
{
  lcd.setCursor(0,0);

  if (previousTemp != TEMPERATURE_NOT_INITIALIZED)
  {
    lcd.print("Tmp Prev: ");
    lcd.print(previousTemp);
  }

  lcd.setCursor(0,1);
  lcd.print("Tmp Curr: ");
  lcd.print(currentTemp);
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

