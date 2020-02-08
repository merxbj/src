#include <OneWire.h> 
#include <DallasTemperature.h>
#include <LiquidCrystal.h>

#define ONE_WIRE_BUS 2
#define TMP_SENSORS 2

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
LiquidCrystal lcd(12, 11, 6, 5, 4, 3);

void updateDisplay(float currentTemp);
void welcomeDisplay();
void updateSerial(float* temps, size_t size);

void setup(void) 
{
  Serial.begin(9600); 
  Serial.println("Digital Thermomether Prototype"); 

  sensors.begin();
  
  welcomeDisplay();
}

void loop(void) 
{ 
  Serial.print(" Requesting temperatures...\t"); 
  sensors.requestTemperatures(); // Send the command to get temperature readings 
  Serial.print("DONE, "); 

  float temperature[TMP_SENSORS] = {0}; 
  temperature[0] = sensors.getTempCByIndex(0);
  temperature[1] = sensors.getTempCByIndex(1);
  
  updateSerial(temperature, TMP_SENSORS);
  
  updateDisplay(temperature, TMP_SENSORS);
  
  delay(1000);
}

void updateDisplay(float* temps, size_t size)
{
  // assumes only two temperatures
  
  lcd.setCursor(0,0);
  lcd.print("Tmp One: ");
  lcd.print(temps[0]);

  lcd.setCursor(0,1);
  lcd.print("Tmp Two: ");
  lcd.print(temps[1]);
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

void updateSerial(float* temps, size_t size)
{
  Serial.print("Temperatures: ");

  for (int i = 0; i < size; i++)
  {
    Serial.print(temps[i]);
    if (i < (size - 1))
    {
      Serial.print(", ");
    }
  }

  Serial.println("");
}

