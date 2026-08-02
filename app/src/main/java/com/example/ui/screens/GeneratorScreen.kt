package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.BarcodeViewModel
import com.example.utils.BarcodeGenerator
import com.example.utils.ShareAndCopyUtils

data class ColorPreset(
    val name: String,
    val fgColor: Color,
    val bgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(viewModel: BarcodeViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Data Type selector (URL, TEXT, PHONE, WIFI, EMAIL)
    var selectedDataType by remember { mutableStateOf("URL") }

    // Input fields state
    var urlInput by remember { mutableStateOf("https://google.com") }
    var textInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }

    // Wifi fields
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiEncryption by remember { mutableStateOf("WPA") }

    // Email fields
    var emailAddress by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }

    // Format selection
    var selectedFormat by remember { mutableStateOf(BarcodeGenerator.Format.QR_CODE) }
    var formatDropdownExpanded by remember { mutableStateOf(false) }

    // Color customization
    val colorPresets = remember {
        listOf(
            ColorPreset("كلاسيكي", Color.Black, Color.White),
            ColorPreset("أزرق جليدي", Color(0xFFA8C7FA), Color(0xFF111318)),
            ColorPreset("ذهبي فاخر", Color(0xFFFFB703), Color(0xFF1E1E24)),
            ColorPreset("زمردي ناصع", Color(0xFF0D9488), Color(0xFFF8FAFC)),
            ColorPreset("أرجواني", Color(0xFF7000FF), Color(0xFF151D2A))
        )
    }
    var selectedColorPreset by remember { mutableStateOf(colorPresets[0]) }

    // Logo Overlay toggle for QR
    var addLogoOverlay by remember { mutableStateOf(true) }

    // App logo bitmap reference
    val appLogoBitmap = remember {
        try {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_icon)
        } catch (e: Exception) {
            null
        }
    }

    // Generated Barcode Bitmap
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var rawEncodedContent by remember { mutableStateOf("") }

    // Compute raw encoded content based on type
    LaunchedEffect(
        selectedDataType, urlInput, textInput, phoneInput,
        wifiSsid, wifiPassword, wifiEncryption,
        emailAddress, emailSubject, emailBody,
        selectedFormat, selectedColorPreset, addLogoOverlay
    ) {
        val content = when (selectedDataType) {
            "URL" -> urlInput
            "TEXT" -> textInput
            "PHONE" -> if (phoneInput.isNotBlank()) "tel:$phoneInput" else ""
            "WIFI" -> if (wifiSsid.isNotBlank()) "WIFI:S:$wifiSsid;T:$wifiEncryption;P:$wifiPassword;;" else ""
            "EMAIL" -> if (emailAddress.isNotBlank()) "MATMSG:TO:$emailAddress;SUB:$emailSubject;BODY:$emailBody;;" else ""
            else -> textInput
        }

        rawEncodedContent = content
        if (content.isNotBlank()) {
            generatedBitmap = BarcodeGenerator.generateBarcode(
                content = content,
                format = selectedFormat,
                width = 600,
                height = 600,
                foregroundColor = selectedColorPreset.fgColor.toArgb(),
                backgroundColor = selectedColorPreset.bgColor.toArgb(),
                centerLogo = if (addLogoOverlay && selectedFormat == BarcodeGenerator.Format.QR_CODE) appLogoBitmap else null
            )
        } else {
            generatedBitmap = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
            .padding(top = 16.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "منشئ الأكواد Adam Barcode",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "أنشئ وخصص جميع أنواع الأكواد بنقرة واحدة",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Data Type Selection Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val types = listOf(
                Pair("URL", Icons.Default.Link),
                Pair("TEXT", Icons.Default.TextFields),
                Pair("PHONE", Icons.Default.Phone),
                Pair("WIFI", Icons.Default.Wifi),
                Pair("EMAIL", Icons.Default.AlternateEmail)
            )

            types.forEach { (typeKey, icon) ->
                val isSelected = selectedDataType == typeKey
                Surface(
                    onClick = { selectedDataType = typeKey },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .testTag("type_chip_$typeKey")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = typeKey,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (typeKey) {
                                "URL" -> "رابط"
                                "TEXT" -> "نص"
                                "PHONE" -> "هاتف"
                                "WIFI" -> "واي فاي"
                                "EMAIL" -> "إيميل"
                                else -> typeKey
                            },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields based on DataType
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedDataType) {
                    "URL" -> {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("أدخل رابط الموقع (URL)") },
                            placeholder = { Text("https://example.com") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_url")
                        )
                    }
                    "TEXT" -> {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text("أدخل النص المراد ترميزه") },
                            placeholder = { Text("أكتب أي نص هنا...") },
                            leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_text")
                        )
                    }
                    "PHONE" -> {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("رقم الهاتف") },
                            placeholder = { Text("+966500000000") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_phone")
                        )
                    }
                    "WIFI" -> {
                        Column {
                            OutlinedTextField(
                                value = wifiSsid,
                                onValueChange = { wifiSsid = it },
                                label = { Text("اسم الشبكة (SSID)") },
                                modifier = Modifier.fillMaxWidth().testTag("input_wifi_ssid")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                label = { Text("كلمة المرور") },
                                modifier = Modifier.fillMaxWidth().testTag("input_wifi_pass")
                            )
                        }
                    }
                    "EMAIL" -> {
                        Column {
                            OutlinedTextField(
                                value = emailAddress,
                                onValueChange = { emailAddress = it },
                                label = { Text("البريد الإلكتروني") },
                                modifier = Modifier.fillMaxWidth().testTag("input_email_addr")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = emailSubject,
                                onValueChange = { emailSubject = it },
                                label = { Text("الموضوع") },
                                modifier = Modifier.fillMaxWidth().testTag("input_email_sub")
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barcode Format Dropdown Selector
        ExposedDropdownMenuBox(
            expanded = formatDropdownExpanded,
            onExpandedChange = { formatDropdownExpanded = !formatDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedFormat.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("صيغة الكود (Format)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatDropdownExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("format_dropdown")
            )

            ExposedDropdownMenu(
                expanded = formatDropdownExpanded,
                onDismissRequest = { formatDropdownExpanded = false }
            ) {
                BarcodeGenerator.Format.values().forEach { format ->
                    DropdownMenuItem(
                        text = { Text(format.displayName) },
                        onClick = {
                            selectedFormat = format
                            formatDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customization Card (Colors & Center Logo)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "التخصيص والألوان",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Color Presets Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colorPresets.forEach { preset ->
                        val isSelected = selectedColorPreset == preset
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(preset.bgColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColorPreset = preset }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(preset.fgColor)
                            )
                        }
                    }
                }

                if (selectedFormat == BarcodeGenerator.Format.QR_CODE) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { addLogoOverlay = !addLogoOverlay }
                    ) {
                        Checkbox(
                            checked = addLogoOverlay,
                            onCheckedChange = { addLogoOverlay = it }
                        )
                        Text(
                            text = "إضافة شعار Adam Xit في منتصف الـ QR",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Barcode Preview Display Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = selectedColorPreset.bgColor),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (generatedBitmap != null) {
                    Image(
                        bitmap = generatedBitmap!!.asImageBitmap(),
                        contentDescription = "Generated Barcode",
                        modifier = Modifier
                            .size(230.dp)
                            .padding(8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "أدخل بيانات لإنشاء الكود",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row (Save PNG, Share PNG, Save History)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    generatedBitmap?.let { bmp ->
                        val uri = ShareAndCopyUtils.saveBitmapToDevice(context, bmp)
                        if (uri != null) {
                            Toast.makeText(context, "تم حفظ الصورة كـ PNG بنجاح!", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = generatedBitmap != null,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .testTag("save_png_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تنزيل", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    generatedBitmap?.let { bmp ->
                        ShareAndCopyUtils.shareBitmap(context, bmp, "مشاركة كود ${selectedFormat.displayName}")
                    }
                },
                enabled = generatedBitmap != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .testTag("share_png_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("مشاركة", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    if (rawEncodedContent.isNotBlank()) {
                        viewModel.saveGeneratedCode(
                            content = rawEncodedContent,
                            format = selectedFormat.displayName,
                            fgHex = String.format("#%06X", 0xFFFFFF and selectedColorPreset.fgColor.toArgb()),
                            bgHex = String.format("#%06X", 0xFFFFFF and selectedColorPreset.bgColor.toArgb())
                        )
                        Toast.makeText(context, "تم الحفظ في السجل بنجاح", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = generatedBitmap != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .testTag("save_history_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("السجل", fontSize = 12.sp)
            }
        }
    }
}
