package com.example.projectalice;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import android.util.Log;
import android.graphics.Color;

import android.content.Intent;
import android.speech.RecognizerIntent;
import java.util.Locale;
import java.util.ArrayList;
import org.json.JSONObject;



import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ativa o EdgeToEdge para expandir a aplicação para preencher a tela
        EdgeToEdge.enable(this);

        // Define o layout da atividade
        setContentView(R.layout.activity_main);

        // Encontra os botões pelo ID definido no XML e adiciona-os à matriz
        Button[] arrayBotoes = new Button[8];
        arrayBotoes[0] = findViewById(R.id.botaoRele1);
        arrayBotoes[1] = findViewById(R.id.botaoRele2);
        arrayBotoes[2] = findViewById(R.id.botaoRele3);
        arrayBotoes[3] = findViewById(R.id.botaoRele4);
        arrayBotoes[4] = findViewById(R.id.botaoRele5);
        arrayBotoes[5] = findViewById(R.id.botaoRele6);
        arrayBotoes[6] = findViewById(R.id.botaoRele7);
        arrayBotoes[7] = findViewById(R.id.botaoRele8);

        atualizarEstadoBotao(arrayBotoes);

        // Encontra botão do reconhecimento de voz
        Button botaoVoz = findViewById(R.id.botaoRVoz);

        // Define um listener de clique para os botões
        arrayBotoes[0].setOnClickListener(v -> fazerRequestHttp("rele1", arrayBotoes[0]));
        arrayBotoes[1].setOnClickListener(v -> fazerRequestHttp("rele2", arrayBotoes[1]));
        arrayBotoes[2].setOnClickListener(v -> fazerRequestHttp("rele3", arrayBotoes[2]));
        arrayBotoes[3].setOnClickListener(v -> fazerRequestHttp("rele4", arrayBotoes[3]));
        arrayBotoes[4].setOnClickListener(v -> fazerRequestHttp("rele5", arrayBotoes[4]));
        arrayBotoes[5].setOnClickListener(v -> fazerRequestHttp("rele6", arrayBotoes[5]));
        arrayBotoes[6].setOnClickListener(v -> fazerRequestHttp("rele7", arrayBotoes[6]));

        botaoVoz.setOnClickListener(v -> lerAudio());


        // Define um listener de clique para o botão
        arrayBotoes[7].setOnClickListener(v -> fazerRequestHttp("rele8", arrayBotoes[7]));


        // Configura o aplicativo para preencher a área de conteúdo após o EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Defina a URL base
    private static final String ESP32_URL = "http://192.168.1.130/";

    // Função que faz o request para o ESP32
    private void fazerRequestHttp(final String nRele, final Button botaoRele) {
        new Thread(() -> {
            try {
                // Cria uma URL com a string fornecida
                URL url = new URL(ESP32_URL + nRele);
                // Abre uma conexão HTTP para a URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    // Obtém a stream de entrada da conexão
                    InputStream in = urlConnection.getInputStream();
                    // Cria um leitor de buffer para a stream de entrada
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    // Obter o resultado da solicitação
                    String result = readResponse(reader);

                    // Atualiza a IU na thread principal
                    runOnUiThread(() -> {
                        // Mostra um toast com a mensagem de sucesso e o resultado da solicitação
                        Toast.makeText(MainActivity.this, "Request bem-sucedido! Resultado: " + result, Toast.LENGTH_SHORT).show();

                        // Atualiza a cor do botão com base no estado do relé
                        if (result.equals("Rele Ligado")) {
                            botaoRele.setBackgroundColor(Color.rgb(252, 199, 50));
                        } else if (result.equals("Rele Desligado")) {
                            botaoRele.setBackgroundColor(Color.GRAY);
                        }
                    });
                } finally {
                    // Fecha a conexão após o uso
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                // Se houver uma exceção de E/S, imprime o stack trace
                Log.e("MainActivity", "Falha na solicitação.", e);
                // Notifica o usuário sobre a falha na solicitação
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Falha na solicitação.", Toast.LENGTH_SHORT).show());
            }
        }).start(); // Inicia a thread
    }

    // Método para ler a resposta do servidor
    private String readResponse(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        // Lê cada linha da resposta e adiciona ao StringBuilder
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    // Método para atualizar a cor do botão com base no estado do relé
    private void atualizarEstadoBotao(final Button[] arrayBotoes) {
        // Faz uma solicitação HTTP para obter o JSON de estado dos relés
        new Thread(() -> {
            try {
                URL url = new URL(ESP32_URL + "estado_reles.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Analise o JSON para obter os estados dos relés
                    JSONObject json = new JSONObject(response.toString());

                    // Atualiza os estado dos 8 botões
                    boolean estadoRele;
                    for (int i = 1; i <= 8; i++) {
                        estadoRele = json.getBoolean("Rele" + i);
                        atualizarCorBotaoInicial(arrayBotoes[i - 1], estadoRele);
                        Log.d("MainActivity", "Estado do Rele " + i + ": " + estadoRele);
                    }

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Erro ao atualizar estado do botão", e);
            }
        }).start();
    }

    // Método para atualizar a cor do botão com base no estado do relé
    private void atualizarCorBotaoInicial(Button botao, boolean estadoRele) {
        if (estadoRele) {
            // Se o relé estiver ligado (true), defina a cor do botão como amarelo
            botao.setBackgroundColor(Color.rgb(252, 199, 50));
        } else {
            // Se o relé estiver desligado (false), defina a cor do botão como outra cor, por exemplo, cinza
            botao.setBackgroundColor(Color.GRAY);
        }
    }

    // Método para ler o audio
    private void lerAudio() {
        // Cria um intent para o reconhecimento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Define o modelo de linguagem livre para reconhecimento de voz
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Define o idioma padrão para o reconhecimento de voz
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // Define a mensagem exibida durante o reconhecimento de voz
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale algo...");

        try {
            // Inicia método para o reconhecimento de voz e espera o resultado
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            // Trata erros ao iniciar o reconhecimento de voz
            Toast.makeText(this, "Erro ao iniciar reconhecimento de voz.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o resultado é para o reconhecimento de voz
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // Verifica se o reconhecimento de voz foi bem-sucedido e há dados retornados
            if (resultCode == RESULT_OK && data != null) {
                // Obtém os resultados do reconhecimento de voz como uma lista de strings
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // Obtém o texto reconhecido da primeira string na lista
                String voiceInput = result.get(0);

                // Aqui você pode usar o texto reconhecido, por exemplo, exibir em um toast
                Toast.makeText(this, "Texto Reconhecido: " + voiceInput, Toast.LENGTH_LONG).show();
            }
        }
    }
}