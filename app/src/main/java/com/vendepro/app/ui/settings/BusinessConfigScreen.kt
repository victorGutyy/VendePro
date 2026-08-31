package com.vendepro.app.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vendepro.app.R
import com.vendepro.app.data.model.BusinessConfig
import com.vendepro.app.ui.VendeProViewModel
import com.vendepro.app.ui.theme.Lime
import com.vendepro.app.ui.theme.OnBackgroundMuted
import com.vendepro.app.ui.theme.OnLime
import com.vendepro.app.ui.theme.Outline
import com.vendepro.app.ui.theme.Pink

@Composable
fun BusinessConfigScreen(
    onBack: () -> Unit,
    viewModel: VendeProViewModel = viewModel(),
) {
    val context = LocalContext.current
    val config by viewModel.businessConfig.collectAsStateWithLifecycle()

    var businessName by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var nequi by remember { mutableStateOf("") }
    var daviplata by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        val current = config
        if (!loaded && current != null) {
            businessName = current.businessName
            sellerName = current.sellerName
            contact = current.contactNumber
            nequi = current.nequiAccount
            daviplata = current.daviplataAccount
            loaded = true
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Volver", tint = Lime, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text("CONFIGURACIÓN", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground)
            }

            ConfigField(businessName, { businessName = it }, "Nombre del emprendimiento")
            ConfigField(sellerName, { sellerName = it }, "Nombre del vendedor")
            ConfigField(contact, { contact = it }, "# de contacto / WhatsApp", keyboardType = KeyboardType.Phone)
            ConfigField(nequi, { nequi = it }, "# Cuenta Nequi", keyboardType = KeyboardType.Phone, accent = Pink)
            ConfigField(daviplata, { daviplata = it }, "# Cuenta Daviplata", keyboardType = KeyboardType.Phone)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(54.dp)
                    .background(Lime)
                    .clickable {
                        viewModel.saveConfig(
                            BusinessConfig(
                                businessName = businessName.trim(),
                                sellerName = sellerName.trim(),
                                contactNumber = contact.trim(),
                                nequiAccount = nequi.trim(),
                                daviplataAccount = daviplata.trim(),
                            ),
                        )
                        Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("GUARDAR CONFIGURACIÓN", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp), color = OnLime)
            }
        }
    }
}

@Composable
private fun ConfigField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    accent: androidx.compose.ui.graphics.Color = Lime,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        shape = RoundedCornerShape(0.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accent,
            unfocusedBorderColor = Outline,
            focusedLabelColor = accent,
            unfocusedLabelColor = OnBackgroundMuted,
            cursorColor = accent,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}
