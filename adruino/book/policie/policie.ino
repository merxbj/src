const int redPin = 2;
const int bluePin = 3;

void setup() {
  // put your setup code here, to run once:
  pinMode(redPin, OUTPUT);
  pinMode(bluePin, OUTPUT);

  digitalWrite(redPin, LOW);
  digitalWrite(bluePin, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:

  digitalWrite(redPin, HIGH);
  digitalWrite(bluePin, LOW);

  delay(500);

  digitalWrite(redPin, LOW);
  digitalWrite(bluePin, HIGH);

  delay(500);
}
