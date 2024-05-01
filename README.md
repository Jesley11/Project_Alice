# Project Alice

Project Alice é um aplicativo móvel desenvolvido para controlar uma placa ESP32, permitindo o controle de relés através de solicitações HTTP. Além disso, o aplicativo utiliza o sintetizador de voz do Google para controle por voz.

## Funcionalidades

- **Controle de Relés**: Controle os relés da sua placa ESP32 remotamente através de solicitações HTTP.
- **Controle por Voz**: Utilize comandos de voz para controlar os relés.

## Requisitos

- Placa ESP32
- Android 7.0 ou Superior

## Como Usar

1. Instale o aplicativo em seu dispositivo móvel.
2. Conecte o ESP e seu dispositivo movel na mesma rede WiFi.
3. Use os botões na interface do aplicativo ou comandos de voz para controlar os relés.

## Configuração

1. Clone o repositório:

   ```
   git clone https://github.com/Jesley11/Project_Alice.git
   ```

2. Abra o projeto no Android Studio.

3. No arquivo `MainActivity.java`, defina o IP da sua placa ESP32:

   ```java
   private static final String ESP32_URL = "http://192.168.0.0";
   ```

4. Compile e execute o aplicativo em seu dispositivo Android.

## Contribuindo

Contribuições são bem-vindas! Se você quiser melhorar o projeto, sinta-se à vontade para enviar um pull request.

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).
