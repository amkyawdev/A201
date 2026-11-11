package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ApiSettingsScreen(viewModel: StudioViewModel) {
    val geminiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle()
    val elevenLabsKey by viewModel.elevenLabsApiKey.collectAsStateWithLifecycle()
    val assemblyAiKey by viewModel.assemblyAiApiKey.collectAsStateWithLifecycle()
    val openAiKey by viewModel.openAiApiKey.collectAsStateWithLifecycle()

    var geminiInput by remember(geminiKey) { mutableStateOf(geminiKey) }
    var elevenLabsInput by remember(elevenLabsKey) { mutableStateOf(elevenLabsKey) }
    var assemblyAiInput by remember(assemblyAiKey) { mutableStateOf(assemblyAiKey) }
    var openAiInput by remember(openAiKey) { mutableStateOf(openAiKey) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "API & Provider Credentials (BYOK)",
            subtitle = "Encrypted On-Device Storage • User Key Configuration"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassmorphicCard(
                    glowColor = ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = MetallicGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BRING YOUR OWN KEY (BYOK) PROVIDERS",
                                style = MaterialTheme.typography.bodySmall,
                                color = MetallicGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = geminiInput,
                            onValueChange = { geminiInput = it },
                            label = { Text("Gemini API Key (Script & Recap Generation)", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gemini_key_input"),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = elevenLabsInput,
                            onValueChange = { elevenLabsInput = it },
                            label = { Text("ElevenLabs API Key (Burmese & English Voice TTS)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = assemblyAiInput,
                            onValueChange = { assemblyAiInput = it },
                            label = { Text("AssemblyAI API Key (Speech Transcription)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = openAiInput,
                            onValueChange = { openAiInput = it },
                            label = { Text("OpenAI / Whisper API Key (Optional STT)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.saveApiSettings(geminiInput, elevenLabsInput, assemblyAiInput, openAiInput)
                                Toast.makeText(context, "Credentials Encrypted & Saved Successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_api_keys_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Credentials Securely", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Active Credentials Status Card
            item {
                GlassmorphicCard(
                    glowColor = MetallicGoldGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "PROVIDER STATUS OVERVIEW",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ProviderStatusRow("Gemini API", geminiKey.isNotBlank())
                        ProviderStatusRow("ElevenLabs TTS", elevenLabsKey.isNotBlank())
                        ProviderStatusRow("AssemblyAI Speech", assemblyAiKey.isNotBlank())
                        ProviderStatusRow("OpenAI Whisper", openAiKey.isNotBlank())
                        ProviderStatusRow("Built-In Studio Engine", true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderStatusRow(name: String, isActive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isActive) SuccessGreen else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (isActive) "Active" else "Offline / Default",
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) SuccessGreen else TextMuted,
            fontSize = 11.sp
        )
    }
}
