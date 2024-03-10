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

import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        // Define um listener de clique para o botão
        arrayBotoes[0].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele1", arrayBotoes[0]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[1].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele2", arrayBotoes[1]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[2].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele3", arrayBotoes[2]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[3].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele4", arrayBotoes[3]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[4].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele5", arrayBotoes[4]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[5].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele6", arrayBotoes[5]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[6].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele7", arrayBotoes[6]);
        });

        // Define um listener de clique para o botão
        arrayBotoes[7].setOnClickListener(v -> {
            // Mostra um toast indicando que o rele foi acionado
            Toast.makeText(getApplicationContext(), "Rele Acionado!", Toast.LENGTH_SHORT).show();
            fazerRequestHttp("rele8", arrayBotoes[7]);
        });

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
                            botaoRele.setBackgroundColor(Color.rgb(252, 199,50));
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
            botao.setBackgroundColor(Color.rgb(252, 199,50));
        } else {
            // Se o relé estiver desligado (false), defina a cor do botão como outra cor, por exemplo, cinza
            botao.setBackgroundColor(Color.GRAY);
        }
    }
}