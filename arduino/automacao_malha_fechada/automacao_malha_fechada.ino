// GERENCIAMENTO: CAIXA D'ÁGUA
#define umidade A0
#define IN1 2
#define IN2 3

// GERENCIAMENTO: VAZAMENTO DE GÁS
#define gas A1
#define buzzer 5
#define LED 6

void setup() {
  Serial.begin(9600);
  
  configurarCaixaDAgua();
  configurarGerenciamentoVazamentoDeGas();
}

void loop() {
  gerenciarCaixaDAgua();
  gerenciarVazamentoDeGas();
}

// CONFIGURAÇÕES
void configurarGerenciamentoCaixaDAgua(){
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
}

void configurarGerenciamentoVazamentoDeGas(){
  pinMode(gas, INPUT);
  pinMode(buzzer, OUTPUT);
  pinMode(LED, OUTPUT);
}

// FUNCIONALIDADES
// caixa d'água
void gerenciarCaixaDAgua(){
  int leitura = analogRead(umidade);

  if(leitura > 550){
    ligarBomba();
  }else{
    desligarBomba();
  }
}

void ligarBomba(){
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  delay(1000);
}

void desligarBomba(){
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  delay(1000);
}

void gerenciarVazamentoDeGas(){
  int leitura = analogRead(gas);

  if(leitura > 450){
    gasLeakAlert();
  }

  delay(500);
}

void gasLeakAlert() {
  int alertFrequency1 = 1000; 
  int alertFrequency2 = 1500; 
  int alertDuration = 100;    

  for (int i = 0; i < 10; i++) {
    digitalWrite(LED, HIGH);
    tone(buzzer, alertFrequency1); 
    delay(alertDuration);
    noTone(buzzer); 
    digitalWrite(LED, LOW);
    delay(alertDuration / 2); 

    digitalWrite(LED, HIGH);
    tone(buzzer, alertFrequency2); 
    delay(alertDuration);
    noTone(buzzer); 
    digitalWrite(LED, LOW);
    delay(alertDuration / 2); 
  }
}