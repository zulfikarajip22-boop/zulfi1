package com.example.zulfi1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uas.data.KasirDatabase
import com.example.uas.data.KasirEntity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/* ---------- THEME COLORS ---------- */
val FarmDark = Color(0xFF1A1C1E)
val FarmGold = Color(0xFFD4AF37)
val FarmSoftWhite = Color(0xFFF8F9FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = FarmDark,
                    secondary = FarmGold,
                    surface = Color.White
                )
            ) {
                Scaffold(
                    containerColor = FarmSoftWhite,

                    ) { innerPadding ->
                    KasirAyamScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .systemBarsPadding()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)



@Composable
fun KasirAyamScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val dao = remember { KasirDatabase.getInstance(context).kasirDao() }
    val scope = rememberCoroutineScope()
    val riwayat by dao.getAll().collectAsState(initial = emptyList())

    var editId by remember { mutableStateOf<Int?>(null) }

    var namaPembeli by remember { mutableStateOf("") }
    var jumlahAyam by remember { mutableStateOf("") }
    var hargaAyam by remember { mutableStateOf("") }
    var totalHarga by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text("Kasir Penjualan", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        /* ---------- INPUT ---------- */
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ModernTextField(namaPembeli, { namaPembeli = it }, "Nama Pembeli", Icons.Default.AccountCircle)
                ModernTextField(jumlahAyam, { jumlahAyam = it }, "Jumlah (ekor)", Icons.Default.ShoppingCart, KeyboardType.Number)
                ModernTextField(hargaAyam, { hargaAyam = it }, "Harga per Ekor", Icons.Default.Info, KeyboardType.Number)

                Button(
                    onClick = {
                        val j = jumlahAyam.toIntOrNull() ?: 0
                        val h = hargaAyam.toIntOrNull() ?: 0
                        totalHarga = j * h
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmDark)
                ) {
                    Text("HITUNG TOTAL")
                }
            }
        }

        /* ---------- STRUK ---------- */
        Surface(shape = RoundedCornerShape(24.dp), color = FarmDark) {
            Column(Modifier.padding(20.dp)) {

                ReceiptRow("Customer", namaPembeli.ifEmpty { "-" })
                ReceiptRow("Jumlah", "${jumlahAyam.ifEmpty { "0" }} ekor")
                ReceiptRow("Harga", formatRupiah(hargaAyam.toIntOrNull() ?: 0))

                Spacer(Modifier.height(10.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(10.dp))

                ReceiptRow("TOTAL", formatRupiah(totalHarga))

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        val nama = namaPembeli.trim()
                        val j = jumlahAyam.toIntOrNull() ?: 0
                        val h = hargaAyam.toIntOrNull() ?: 0
                        val total = j * h

                        if (nama.isNotEmpty() && j > 0 && h > 0) {
                            scope.launch {
                                dao.insert(
                                    KasirEntity(
                                        id = editId ?: 0,
                                        namaPembeli = nama,
                                        jumlahAyam = j,
                                        hargaAyam = h,
                                        totalHarga = total
                                    )
                                )

                                namaPembeli = ""
                                jumlahAyam = ""
                                hargaAyam = ""
                                totalHarga = 0
                                editId = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGold)
                ) {
                    Text("BAYAR", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        /* ---------- RIWAYAT ---------- */
        Text("RIWAYAT PENJUALAN", fontWeight = FontWeight.Bold, color = FarmGold)

        LazyColumn(
            modifier = Modifier.height(220.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(riwayat) { item ->
                Surface(shape = RoundedCornerShape(14.dp), color = Color.White) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.namaPembeli, fontWeight = FontWeight.Bold)
                            Text("${item.jumlahAyam} ekor • ${formatRupiah(item.totalHarga)}")
                        }
                        Row {
                            IconButton(onClick = {
                                namaPembeli = item.namaPembeli
                                jumlahAyam = item.jumlahAyam.toString()
                                hargaAyam = item.hargaAyam.toString()
                                totalHarga = item.totalHarga
                                editId = item.id
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                scope.launch { dao.delete(item) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- COMPONENT ---------- */

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.LightGray)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun formatRupiah(number: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(number).replace("Rp", "Rp ")
}
