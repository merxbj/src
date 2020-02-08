int notes[] = {262, 294, 330, 349};

void setup() {
  Serial.begin(9600);

}

void loop() {
  int keyVal = analogRead(A0);
  Serial.println(keyVal);

  if (keyVal >= 1020 && keyVal <= 1024)
  {
    tone(8, notes[0]);
  }
  else if (keyVal >= 990 && keyVal <= 1010)
  {
    tone(8, notes[1]);
  }
  else if (keyVal >= 490 && keyVal <= 530)
  {
    tone(8, notes[2]);
  }
  else if (keyVal >= 6 && keyVal <= 40)
  {
    tone(8, notes[3]);
  }
  else
  {
    noTone(8);
  }
}
