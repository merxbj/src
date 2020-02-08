import processing.serial.*;

Serial arduino;
PImage logo;
int bgcolor = 0;

void settings() {
  logo = loadImage("http://static.arduino.org/images/arduino_official_Logo.png");
  size(logo.width, logo.height);
}

void setup() {
  colorMode(HSB, 255);
  println("Available serial ports:");
  println((Object[])Serial.list());
  
  arduino = new Serial(this, Serial.list()[0], 9600);
}


void draw() {
  if (arduino.available() > 0) {
    bgcolor = arduino.read();
    println(bgcolor);
  }
  background(bgcolor, 255, 255);
  image(logo, 0, 0);
}