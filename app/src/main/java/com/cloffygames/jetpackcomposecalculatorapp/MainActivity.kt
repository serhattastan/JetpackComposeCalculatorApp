package com.cloffygames.jetpackcomposecalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cloffygames.jetpackcomposecalculatorapp.ui.theme.JetpackComposeCalculatorAppTheme
import com.cloffygames.jetpackcomposecalculatorapp.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tema uygulaması ve ana uygulama composable'ını ayarlama
        setContent {
            JetpackComposeCalculatorAppTheme {
                CalculatorApp(viewModel)
            }
        }
    }
}

@Composable
fun CalculatorApp(viewModel: CalculatorViewModel) {
    // State değişkenlerini toplama
    val input by viewModel.input.collectAsState()
    val error by viewModel.error.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2D2D2D))
    ) { innerPadding ->
        // Ana kolon
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst bölüm (ekran ve tarihçe)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hesap makinesi ekranı
                Display(input, error)
                if (history.isNotEmpty()) {
                    // Geçmiş hesaplamalar
                    History(history)
                }
            }
            // Alt bölüm (düğmeler)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Buttons { symbol ->
                    viewModel.onButtonClick(symbol)
                }
            }
        }
    }
}

@Composable
fun Display(input: String, error: Boolean) {
    // Metin boyutunu ayarlama
    val textSize = if (input.length > 10) 32.sp else 48.sp
    // Arkaplan rengini hata durumuna göre ayarlama
    val backgroundColor = if (error) Color.Red else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = input,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun History(history: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .background(Color(0xFF3A3A3A), shape = RoundedCornerShape(16.dp))
    ) {
        // Geçmiş başlığı
        item {
            Text(
                text = "History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        // Geçmişteki her bir öğe için metin
        items(history.size) { index ->
            Text(
                text = history[index],
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun Buttons(onButtonClick: (String) -> Unit) {
    val buttons = listOf(
        listOf("^", "ln", "log", "!"),
        listOf("sin", "cos", "tan", "sqr"),
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "C", "+"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        buttons.forEach { row ->
            ButtonRow(row, onButtonClick)
        }
        // Eşittir düğmesi
        Button(
            onClick = { onButtonClick("=") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "=",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ButtonRow(symbols: List<String>, onButtonClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        symbols.forEach { symbol ->
            Button(
                onClick = { onButtonClick(symbol) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (symbol) {
                        "C" -> Color(0xFFF44336) // Temizle düğmesi rengi
                        "/", "*", "-", "+", "=" -> Color(0xFF2196F3) // İşlem düğmeleri rengi
                        else -> Color(0xFF757575) // Diğer düğmelerin rengi
                    }
                ),
                shape = CircleShape
            ) {
                Text(
                    text = symbol,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    // Önizleme için
    JetpackComposeCalculatorAppTheme {
        CalculatorApp(CalculatorViewModel())
    }
}