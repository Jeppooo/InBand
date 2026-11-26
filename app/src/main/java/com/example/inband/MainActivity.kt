package com.example.inband

import android.os.Bundle
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inband.ui.theme.InBandTheme

enum class NavigationItem(val title: String, val icon: ImageVector) {
    HOME("InBand", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    MESSAGES("Messages", Icons.Default.Email),
    PROFILE("Profile", Icons.Default.Person),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InBandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var musicianLoginComplete by remember { mutableStateOf(false) }
    var selectedSubscription by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }
    val bottomNavItems = listOf(NavigationItem.HOME, NavigationItem.SEARCH, NavigationItem.MESSAGES, NavigationItem.PROFILE)

    val screen = when {
        selectedRole == null -> "roles"
        selectedRole == "Musician" && !musicianLoginComplete -> "musician-login"
        selectedRole == "Musician" && selectedSubscription == null -> "subscription"
        else -> "main"
    }

    Crossfade(
        targetState = screen,
        animationSpec = tween(durationMillis = 350),
        label = "role-screen-transition"
    ) { currentScreen ->
        when (currentScreen) {
            "roles" -> RoleSelectionScreen(onRoleSelected = { selectedRole = it })
            "musician-login" -> MusicianLoginScreen(onContinue = { musicianLoginComplete = true })
            "subscription" -> SubscriptionScreen(onSubscriptionSelected = { selectedSubscription = it })
            else -> Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.safeContent.only(WindowInsetsSides.Top),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(selectedItem.title, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { selectedItem = NavigationItem.SETTINGS }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (selectedItem == NavigationItem.SETTINGS) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedItem) {
                NavigationItem.HOME -> HomeScreen()
                NavigationItem.SEARCH -> SearchScreen()
                NavigationItem.MESSAGES -> DirectMessagesScreen()
                NavigationItem.PROFILE -> ProfileScreen()
                NavigationItem.SETTINGS -> SettingsScreen()
            }
        }
    }
    }
    }
}

@Composable
fun MusicianLoginScreen(onContinue: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    val canContinue = listOf(firstName, lastName, email, phoneNumber, city, address, country, birthDate)
        .all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF17171D))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Musician login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF4F4F6)
        )
        Text(
            text = "Enter your details to continue",
            fontSize = 16.sp,
            color = Color(0xFFB8B8C2)
        )
        var emailCorrect by remember { mutableStateOf(true) }
        ProfileTextField("First Name", firstName, { firstName = it })
        ProfileTextField("Last Name", lastName, { lastName = it })
        ProfileTextField("Email", email, {
            email = it
            emailCorrect = checkAtEmail(it)

        }, KeyboardType.Email)
        if(!emailCorrect){
            Text("Invalid email", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        ProfileTextField("Phone number", phoneNumber, { phoneNumber = it }, KeyboardType.Phone)
        ProfileTextField("City", city, { city = it })
        ProfileTextField("Address", address, { address = it })
        ProfileTextField("Country", country, { country = it })
        var birthDateCorrect by remember { mutableStateOf(true) }
        ProfileTextField(
            label = "Birth date (DD/MM/YYYY)",
            value = birthDate,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(8)
                birthDate = digits
                val formatted = formatBirthDate(digits)
                birthDateCorrect = digits.isEmpty() || digits.length < 8 || checkDateBirth(formatted)
            },
            keyboardType = KeyboardType.Number,
            visualTransformation = DateVisualTransformation()
        )
        if(!birthDateCorrect){
            Text("Invalid birth date", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF2A4B),
                contentColor = Color.White
            )
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = MaterialTheme.shapes.medium
    )
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) {
                out += "/"
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return trimmed.length
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@Composable
fun HomeScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Home Screen",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search artists, music schools, venues...")},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (searchQuery.isEmpty()) {
            Text(
                text = "Type to search",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Results for \"$searchQuery\"",
                fontSize = 16.sp,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Profile Screen",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DirectMessagesScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Direct Messages Screen",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Settings Screen",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    val roles = listOf("Band", "Musician", "Music school", "Gig venue")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF17171D))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to InBand",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF4F4F6)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "First, tell us who you are",
            fontSize = 16.sp,
            color = Color(0xFFB8B8C2)
        )
        Spacer(modifier = Modifier.height(28.dp))
        roles.chunked(2).forEach { roleRow ->
            Row(modifier = Modifier.fillMaxWidth()) {
                roleRow.forEach { role ->
                    OutlinedButton(
                        onClick = { onRoleSelected(role) },
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .padding(6.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color(0xFFFF2A4B)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFFF2A4B)
                        )
                    ) {
                        Text(role)
                    }
                }
            }
        }
    }
}

data class SubscriptionPlan(val name: String, val price: String)

@Composable
fun SubscriptionScreen(onSubscriptionSelected: (String) -> Unit){
    val subscriptions = listOf(
        SubscriptionPlan("Basic Plan", "5,99€/mo"),
        SubscriptionPlan("Plus Plan", "12,99€/mo"),
        SubscriptionPlan("Premium Plan", "15,99€/mo")
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF17171D))
        .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Choose your subscription plan",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFFF4F4F6)
                )
            Spacer(modifier = Modifier.height(28.dp))
                subscriptions.chunked(2).forEach { subRow ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        subRow.forEach { plan ->
                            OutlinedButton(
                                onClick = { onSubscriptionSelected(plan.name) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp)
                                    .padding(6.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(2.dp, Color(0xFFFF2A4B)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFFFF2A4B)
                                )
                            )
                            {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = plan.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = plan.price,
                                        fontSize = 14.sp,
                                        color = Color(0xFFB8B8C2)
                                    )
                                }
                            }
                        }
                    }
                }

    }
}
fun checkAtEmail(email : String): Boolean{
    return email.contains(regex = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$".toRegex())
}
fun checkDateBirth(birthDate : String): Boolean{
    return birthDate.contains(regex = "^(?:0[1-9]|[12]\\d|3[01])([/.-])(?:0[1-9]|1[012])\\1(?:19|20)\\d\\d$".toRegex())
}
fun formatBirthDate(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)
    return when {
        digits.length >= 5 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
        digits.length >= 3 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
        else -> digits
    }
}

