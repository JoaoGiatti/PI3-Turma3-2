package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.superid.ui.theme.SuperIDTheme
import com.example.superid.homepages.PasswordPage
import com.example.superid.homepages.ProfilePage
import com.example.superid.homepages.ScanPage
import com.example.superid.R
import com.example.superid.homepages.AddPasswordScreen

// Agora, vamos garantir que a classe 'NavItem' tenha um nome único.
data class NavItemData(val label: String, val icon: Int)

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val navItemList = listOf(
        NavItemData("Senhas", R.drawable.keyicon),
        NavItemData("Escanear", R.drawable.scanicon),
        NavItemData("perfil", R.drawable.profileicon)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF252525)
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = navItem.icon),
                                contentDescription = navItem.label,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFF4EB00),
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController
) {
    // Usando o NavHostController para navegar corretamente
    when (selectedIndex) {
        0 -> {
            // Usando o NavHostController para navegação
            NavHost(
                navController = navController as NavHostController,
                startDestination = Routes.PasswordList,
                modifier = modifier
            ) {
                // Definindo as rotas
                composable(Routes.PasswordList) {
                    PasswordPage(navController = navController) // Passando o navController
                }
                composable(Routes.AddPassword) {
                    AddPasswordScreen(navController = navController) // Passando o navController
                }
            }
        }
        1 -> ScanPage() // Página de escanear
        2 -> ProfilePage() // Página de perfil
    }
}

object Routes {
    const val PasswordList = "passwordList"
    const val AddPassword = "addPassword"
}