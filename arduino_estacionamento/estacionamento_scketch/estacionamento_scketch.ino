#include <SoftwareSerial.h>

#define bluetooth_rx 2
#define bluetooth_tx 3
#define pino_trigger 4
#define pino_echo 5

long duration;
int distance;

SoftwareSerial Bluetooth(bluetooth_rx, bluetooth_tx); 

void setup() {
  pinMode(pino_trigger, OUTPUT);
  pinMode(pino_echo, INPUT);

  Serial.begin(9600);
  Bluetooth.begin(9600);
}

void loop() {
  if (Bluetooth.available()) {
    char data = Bluetooth.read();
    Serial.print("Received from Bluetooth: ");
    Serial.println(data);
    // Optionally, send a response back
    Bluetooth.print('B'); // Echo the received character
  }

  // Send data from Serial Monitor to Bluetooth
  /*if (Serial.available()) {
    char data = Serial.read();
    Bluetooth.print(data);
  }*/

  //Le as informacoes do sensor, em cm
  // Garante que o pino Trig está desligado
  digitalWrite(pino_trigger, LOW);
  delayMicroseconds(2);

  // Envia um pulso de 10 microssegundos no pino Trig
  digitalWrite(pino_trigger, HIGH);
  delayMicroseconds(10);
  digitalWrite(pino_trigger, LOW);

  // Leitura do pino Echo e mede a duração do pulso
  duration = pulseIn(pino_echo, HIGH);

  // Calcula a distância (duração / 2) / 29.1
  distance = duration * 0.034 / 2;

  // Imprime a distância no monitor serial
  Serial.print("Distance: ");
  Serial.print(distance);
  Serial.println(" cm");

  // Pausa de 500 milissegundos
  delay(500);
}
