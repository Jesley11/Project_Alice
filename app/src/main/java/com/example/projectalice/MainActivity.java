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

        // Encontra o botão pelo ID definido no XML
        Button botaoRele1 = findViewById(R.id.botaoRele1);

        // Define um listener de clique para o botão
        botaoRele1.setOnClickListener(v -> {
            // Mostra um toast indicando que o rele 1 foi acionado
            //Toast.makeText(getApplicationContext(), "Rele 1 Acionado!", Toast.LENGTH_SHORT).show();
            // Faz uma solicitação HTTP para o ESP32 para acionar o rele2
            fazerRequestHttp("rele1", botaoRele1);
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
                            botaoRele.setBackgroundColor(Color.YELLOW);
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
}