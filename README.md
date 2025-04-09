<img width=100% src="https://capsule-render.vercel.app/api?type=waving&color=E2DA06&height=120&section=header"/>

<div align="center">
    <img src="Readme Assets/SuperIDLogo.png" alt="SuperId Logo" width="100" margintop="-30"/>
</div>

<div align="center">
    <h1>SuperId - Gerenciador de Senhas com Login por QR Code</h1>
</div>

---

<h2 id="desc"> 📖 Descrição Geral</h2>

**SuperId** é um aplicativo mobile de gerenciamento de senhas que permite o login rápido e seguro em sites e sistemas através da leitura de QR Code. Voltado para usuários que desejam centralizar suas credenciais de forma prática e criptografada, o SuperId também oferece sugestões de senhas seguras, armazenamento em nuvem com autenticação via Firebase e sincronização em tempo real com a web.

---

<h2 id="func"> 🚀 Funcionalidades</h2>

### 🔐 Autenticação
- Login via **Firebase Authentication** com email e senha.
- Autenticação segura com tokens e sessões protegidas.

### 📲 Leitura de QR Code
- Leitura de QR Code na tela de login de sites integrados.
- Preenchimento automático das credenciais salvas.

### 🔑 Gerenciador de Senhas
- Criação, edição e exclusão de senhas.
- Organização por categorias.
- Sugestão automática de senhas fortes.

### ☁️ Armazenamento em Nuvem
- Sincronização das senhas com **Firebase Firestore**.
- Acesso de qualquer lugar com segurança.

### 🖥️ Extensão Web
- Permite a geração de QR Code para login rápido no navegador.
- Comunicação entre app mobile e web.

---

<h2 id="arq"> 📐 Arquitetura do Projeto</h2>

Utilizamos uma abordagem moderna combinando **Firebase** com **Jetpack Compose** no Android Studio para o frontend mobile, e **HTML + JS** para a interface web.

### 🔹 Mobile (Kotlin + Jetpack Compose)
- Interfaces declarativas e reativas.
- Ciclo de vida gerenciado e responsividade nativa.

### 🔹 Web (HTML/CSS/JS)
- Interface leve para geração de QR Codes e integração com o app.

### 🔹 Firebase
- Firestore: banco de dados em tempo real.
- Authentication: gerenciamento seguro de usuários.
- Hosting: hospedagem da aplicação web.

---

<h2 id="obs">📝 Observações Importantes</h2>

- Toda a comunicação entre o app e o navegador é feita de forma segura com autenticação e criptografia de dados.
- Os QR Codes gerados expiram após determinado tempo para garantir a segurança.
- O usuário pode optar por ativar autenticação em dois fatores no login no app.

---

<h2 id="tech"> 🛠️ Tecnologias Utilizadas</h2>

| Categoria                 | Tecnologia            | Descrição                                                       |
|--------------------------|-----------------------|------------------------------------------------------------------|
| Mobile Frontend          | Kotlin + Jetpack Compose | Desenvolvimento nativo Android com UI moderna e reativa.       |
| Backend/Nuvem            | Firebase Firestore    | Banco de dados NoSQL em tempo real.                             |
| Autenticação             | Firebase Authentication | Serviço seguro de autenticação de usuários.                    |
| Web                      | HTML, CSS, JS         | Interface de login e leitura de QR Codes.                       |
| Geração de QR Code       | qrcode.js             | Biblioteca JS para gerar QR Codes na interface web.             |
| Controle de Versão       | Git                   | Versionamento de código.                                        |
| IDE                      | Android Studio        | Ambiente de desenvolvimento para o app mobile.                  |
| IDE Web                  | VSCode                | Desenvolvimento da interface web.                               |

---

<h2 id="telas">💻 Telas do Sistema</h2>

<div align="center"><h3>Tela de Login</h3></div>
<p align="center"><i>Login de usuário com Firebase Auth, com opção de redefinir senha e manter sessão ativa.</i></p>
<div align="center">
    <img src="Readme Assets/TelaLogin.png" alt="Tela de Login" width="700"/>
</div>

<div align="center"><h3>Home do App</h3></div>
<p align="center"><i>Acesso rápido às senhas armazenadas, categorias, e opção para escanear QR Code.</i></p>
<div align="center">
    <img src="Readme Assets/TelaHome.png" alt="Home App" width="700"/>
</div>

<div align="center"><h3>Scanner de QR Code</h3></div>
<p align="center"><i>Escaneamento de QR para login rápido no navegador.</i></p>
<div align="center">
    <img src="Readme Assets/TelaScanner.png" alt="Scanner QR" width="700"/>
</div>

<div align="center"><h3>Criação de Senha</h3></div>
<p align="center"><i>Cadastro de uma nova senha com nome, usuário e senha, além de sugestão automática.</i></p>
<div align="center">
    <img src="Readme Assets/TelaNovaSenha.png" alt="Nova Senha" width="700"/>
</div>

---

<h2 id="colab">🤝 Colaboradores</h2>
Um agradecimento especial aos integrantes do PI3, que contribuíram para este projeto.

<table>
  <!-- Informações sobre a Maria -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/Maria Gabriella Xavier Puccinelli" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/141333033" width="100px;" alt="Maria Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>Maria Gabriela</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>FUNÇÕES</h3>
        <p>
            DESCRIÇÃO
        </p>
    </td>
  </tr>

  <!-- Informações sobre o João -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/JoaoGiatti" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/91966589?v=4" width="100px;" alt="João Giatti Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>JOÃO GIATTI</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>FUNÇÕES</h3>
        <p>
            DESCRIÇÃO
        </p>
    </td>
  </tr>

  <!-- Informações sobre a Sofia -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/Sofia de Abreu Guimaraes" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/161376445?v=4" width="100px;" alt="Sofia Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>Sofia Abreu</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>FUNÇÕES</h3>
        <p>
            DESCRIÇÃO
        </p>
    </td>
  </tr>

  <!-- Informações sobre o Vitor -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/vitorhugovhbds" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/201089225" width="100px;" alt="Vitor Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>Vitor Hugo</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>FUNÇÕES</h3>
        <p>
            DESCRIÇÃO
        </p>
    </td>
  </tr>
</table>

<img width=100% src="https://capsule-render.vercel.app/api?type=waving&color=2480D3&height=120&section=footer"/>
