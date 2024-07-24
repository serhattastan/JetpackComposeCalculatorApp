package com.cloffygames.jetpackcomposecalculatorapp.model

import java.util.Locale
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.abs
import kotlin.math.pow

// Bu fonksiyon verilen matematiksel ifadeyi hesaplar ve sonucu döner.
fun calculateResult(input: String): String {
    return try {
        // İfade değerlendirilir.
        val result = eval(input)
        // Sonuç tam sayı mı kontrol edilir.
        if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            // Sonuç bilimsel notasyon gerektiriyorsa formatlanır.
            if (abs(result) >= 1e6 || abs(result) < 1e-3) {
                String.format(Locale.getDefault(), "%.3e", result) // Bilimsel notasyon
            } else {
                String.format(Locale.getDefault(), "%.3f", result)
            }
        }
    } catch (e: Exception) {
        // Hata durumunda "Error" döner.
        "Error"
    }
}

// Bu fonksiyon verilen matematiksel ifadeyi değerlendirir ve sonucu döner.
fun eval(expr: String): Double {
    // İfade değerlendirici nesne tanımlanır.
    return object : Any() {
        var pos = -1 // İfade içindeki pozisyon.
        var ch = 0 // Mevcut karakter.

        // Bir sonraki karaktere geçiş yapar.
        fun nextChar() {
            ch = if (++pos < expr.length) expr[pos].code else -1
        }

        // Belirli bir karakteri yutma işlemi.
        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            return if (ch == charToEat) {
                nextChar()
                true
            } else {
                false
            }
        }

        // İfadeyi değerlendirir.
        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expr.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        // Toplama ve çıkarma işlemlerini değerlendirir.
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                x = when {
                    eat('+'.code) -> x + parseTerm()
                    eat('-'.code) -> x - parseTerm()
                    else -> return x
                }
            }
        }

        // Çarpma ve bölme işlemlerini değerlendirir.
        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                x = when {
                    eat('*'.code) -> x * parseFactor()
                    eat('/'.code) -> x / parseFactor()
                    else -> return x
                }
            }
        }

        // Faktörleri (sayilar, parantezler ve fonksiyonlar) değerlendirir.
        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor() // Unary artı
            if (eat('-'.code)) return -parseFactor() // Unary eksi

            var x: Double
            val startPos = pos
            when {
                eat('('.code) -> { // Parantezler
                    x = parseExpression()
                    eat(')'.code)
                }
                ch in '0'.code..'9'.code || ch == '.'.code -> { // Sayılar
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = expr.substring(startPos, pos).toDouble()
                }
                ch in 'a'.code..'z'.code -> { // Fonksiyonlar
                    while (ch in 'a'.code..'z'.code) nextChar()
                    val func = expr.substring(startPos, pos)
                    x = parseFactor()
                    x = when (func) {
                        "sqrt" -> Math.sqrt(x)
                        "sin" -> Math.sin(Math.toRadians(x))
                        "cos" -> Math.cos(Math.toRadians(x))
                        "tan" -> Math.tan(Math.toRadians(x))
                        "ln" -> ln(x)
                        "log" -> log10(x)
                        "!" -> factorial(x.toInt())
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                }
                else -> throw RuntimeException("Unexpected: " + ch.toChar())
            }

            // Üs işlemini değerlendirir.
            if (eat('^'.code)) {
                val exponent = parseFactor()
                x = if (x < 0 && exponent % 1 != 0.0) {
                    throw RuntimeException("Negative base with non-integer exponent")
                } else {
                    x.pow(exponent)
                }
            }

            return x
        }

        // Faktoriyel hesaplama fonksiyonu
        fun factorial(n: Int): Double {
            if (n < 0) throw RuntimeException("Invalid input for factorial")
            if (n == 0) return 1.0
            var result = 1.0
            for (i in 1..n) {
                result *= i
            }
            return result
        }
    }.parse()
}