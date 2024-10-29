// MÓDULO BLUETOOTH
#include <SoftwareSerial.h>

#define RX_Pin 2
#define TX_Pin 3

SoftwareSerial bluetoothSerial(RX_Pin, TX_Pin);

// PORTÃO
#include <Servo.h>

Servo motorPortao;

// ILUMINAÇÃO
bool isLuzAcesa = false;

#define luz 5

void setup() {
  bluetoothSerial.begin(9600);
  Serial.begin(9600); 

  configurarMotorPortao();
  configurarIluminacao();
}

void loop() {
  if (bluetoothSerial.available()) {
    char comando = bluetoothSerial.read();

    if(comando == 'A'){
      abrirPortao();
      ligarLuz();
    }

    if(comando == 'B'){
      fecharPortao();
    }

    if(comando == 'C'){
      inverterEstadoDaLuz();
    }
  }
}

// CONFIGURAÇÕES
void configurarMotorPortao(){
  motorPortao.attach(4);
  motorPortao.write(0);
}

void configurarIluminacao(){
  pinMode(luz, OUTPUT);
}

// FUNCIONALIDADES
// portão
void abrirPortao(){
  motorPortao.write(90);
}

void fecharPortao(){
  motorPortao.write(0);
}

// iluminação
void ligarLuz(){
  isLuzAcesa = true;
  digitalWrite(luz, HIGH);
}

void desligarLuz(){
  isLuzAcesa = false;
  digitalWrite(luz, LOW);
}

void inverterEstadoDaLuz(){
  if(isLuzAcesa == true){
    desligarLuz();
  }else{
    ligarLuz();
  }
}