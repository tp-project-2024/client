import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLogin: (LoginDto) -> Unit,
    onRegister: (RegisterDto) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            var nickname by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var error by rememberSaveable { mutableStateOf("") }
            var isNicknameError by rememberSaveable { mutableStateOf(false) }
            var isPasswordError by rememberSaveable { mutableStateOf(false) }

            Text(
                text = "Login"
            )
            TextField(
                value = nickname,
                onValueChange = {
                    isNicknameError = false
                    if (it.length <= 64)
                        nickname = it
                },
                label = { Text("Username") },
                isError = isNicknameError,
            )
            TextField(
                value = password,
                onValueChange = {
                    isPasswordError = false
                    if (it.length <= 64)
                        password = it
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = isPasswordError,
            )
            Button(onClick = {
                if (nickname.isBlank()) {
                    error = "Username can't be blank"
                    isNicknameError = true
                    return@Button
                }
                if (password.isBlank()) {
                    error = "Password can't be blank"
                    isPasswordError = true
                    return@Button
                }
                onLogin(LoginDto(nickname, password))
            }) {
                Text("Login")
            }
            Text(
                text = error,
                color = Color.Red,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            var nickname by rememberSaveable { mutableStateOf("") }
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var error by rememberSaveable { mutableStateOf("") }
            var isNicknameError by rememberSaveable { mutableStateOf(false) }
            var isEmailError by rememberSaveable { mutableStateOf(false) }
            var isPasswordError by rememberSaveable { mutableStateOf(false) }

            Text(
                text = "Register"
            )
            TextField(
                value = nickname,
                onValueChange = {
                    isNicknameError = false
                    if (it.length <= 64)
                        nickname = it
                },
                label = { Text("Username") },
            )
            TextField(
                value = email,
                onValueChange = {
                    isEmailError = false
                    if (it.length <= 64)
                        email = it
                },
                label = { Text("Email") },
            )
            TextField(
                value = password,
                onValueChange = {
                    isPasswordError = false
                    if (it.length <= 64)
                        password = it
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )
            Button(onClick = {
                if (nickname.isBlank()) {
                    error = "Username can't be blank"
                    return@Button
                }
                if (email.isBlank()) {
                    error = "Email can't be blank"
                    return@Button
                }
                if (password.isBlank()) {
                    error = "Password can't be blank"
                    return@Button
                }
                onRegister(RegisterDto(nickname, email, password))
            }) {
                Text("Register")
            }
            Text(
                text = error,
                color = Color.Red,
            )
        }
    }
}