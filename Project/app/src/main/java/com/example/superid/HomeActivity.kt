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

// Define os dados de cada item da barra de navegação inferior
data class NavItemData(val label: String, val icon: Int)

// Classe principal da HomeActivity, que é lançada após o login/autenticação
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da tela com o tema do app
        setContent {
            SuperIDTheme {
                HomeScreen()
            }
        }
    }
}

// Função composable que define a estrutura da tela principal com navegação inferior
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController() // Controlador de navegação para gerenciar rotas

    // Lista de itens para exibir na NavigationBar
    val navItemList = listOf(
        NavItemData("Senhas", R.drawable.keyicon),
        NavItemData("Escanear", R.drawable.scanicon),
        NavItemData("perfil", R.drawable.profileicon)
    )

    var selectedIndex by remember { mutableStateOf(0) } // Índice da aba selecionada

    // Estrutura da tela com a NavigationBar inferior
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF252525) // Cor de fundo da barra
            ) {
                // Cria um item de navegação para cada entrada da lista
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index, // Marca o item como selecionado
                        onClick = {
                            selectedIndex = index // Atualiza o índice selecionado
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = navItem.icon), // Ícone do item
                                contentDescription = navItem.label,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFF4EB00), // Cor ao selecionar
                            unselectedIconColor = Color.Gray,      // Cor quando não selecionado
                            indicatorColor = Color.Transparent     // Remove a indicação visual
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Renderiza o conteúdo da tela baseado no item selecionado
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController
        )
    }
}

// Função que define o conteúdo central da tela com base na aba selecionada
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController
) {
    // Renderiza a tela de acordo com o índice selecionado
    when (selectedIndex) {
        0 -> {
            // Senhas: usa um NavHost para permitir navegação entre páginas internas
            NavHost(
                navController = navController as NavHostController,
                startDestination = Routes.PasswordList, // Tela inicial
                modifier = modifier
            ) {
                // Define a rota para a lista de senhas
                composable(Routes.PasswordList) {
                    PasswordPage(navController = navController) // Passa o controlador de navegação
                }
            }
        }
        1 -> ScanPage() // Tela de escanear
        2 -> ProfilePage() // Tela de perfil
    }
}

// Objeto que centraliza as rotas usadas dentro do NavHost
object Routes {
    const val PasswordList = "passwordList" // Rota para a lista de senhas
    const val AddPassword = "addPassword"   // Rota para adicionar senha (ainda não usada aqui)
}
