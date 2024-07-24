package com.cloffygames.jetpackcomposecalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import com.cloffygames.jetpackcomposecalculatorapp.model.calculateResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// CalculatorViewModel sınıfı, hesap makinesi uygulamasının durumunu yönetir.
class CalculatorViewModel : ViewModel() {
    // Kullanıcı girişini saklamak için MutableStateFlow kullanılıyor.
    private val _input = MutableStateFlow("")
    // input, dış dünyaya sadece okunabilir olarak sunuluyor.
    val input: StateFlow<String> = _input

    // Hata durumunu saklamak için MutableStateFlow kullanılıyor.
    private val _error = MutableStateFlow(false)
    // error, dış dünyaya sadece okunabilir olarak sunuluyor.
    val error: StateFlow<Boolean> = _error

    // Hesaplama geçmişini saklamak için MutableStateFlow kullanılıyor.
    private val _history = MutableStateFlow(listOf<String>())
    // history, dış dünyaya sadece okunabilir olarak sunuluyor.
    val history: StateFlow<List<String>> = _history

    // Düğme tıklamaları bu fonksiyonla işleniyor.
    fun onButtonClick(symbol: String) {
        // Eğer bir hata durumu varsa ve "C" haricinde bir düğmeye basılmışsa,
        // girişi temizle ve hata durumunu sıfırla.
        if (_error.value && symbol != "C") {
            _input.value = ""
            _error.value = false
        }
        when (symbol) {
            "=" -> {
                // Eşittir düğmesine basıldığında sonucu hesapla.
                val result = calculateResult(_input.value)
                // Eğer sonuç "Error" ise hata durumunu aktif hale getir.
                if (result == "Error") {
                    _error.value = true
                } else {
                    // Aksi takdirde, geçmişe sonucu ekle.
                    _history.value = _history.value + "${_input.value} = $result"
                }
                // Sonucu giriş alanına yerleştir.
                _input.value = result
            }
            "C" -> {
                // C düğmesine basıldığında girişi ve hata durumunu sıfırla.
                _input.value = ""
                _error.value = false
            }
            else -> {
                // Diğer durumlarda (sayısal ve işlem düğmeleri) giriş uzunluğu 20 karakterden azsa, sembolü girişe ekle.
                if (_input.value.length < 20) {
                    _input.value += symbol
                }
            }
        }
    }
}
