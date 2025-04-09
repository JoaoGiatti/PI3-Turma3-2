<img width=100% src="https://capsule-render.vercel.app/api?type=waving&color=E2DA06&height=120&section=header"/>

<div align="center">
    <img src="Projeto/src/view/assets/logoREADME.png" alt="Logo da Wager" width="300"/>
</div>

<div align="center">
    <h1>Plataforma de Apostas em Eventos Futuros</h1>
</div>

---

<h2 id="desc"> 📖 Descrição Geral</h2>

**Wager** é uma plataforma web de apostas que permite aos usuários criar e participar de apostas em uma ampla gama de eventos futuros. Seja em jogos esportivos, eleições ou até eventos naturais, como catástrofes, a plataforma oferece um ambiente seguro e interativo para que os usuários façam suas previsões e coloquem suas apostas. Todo o gerenciamento financeiro é simulado, proporcionando uma experiência de apostas prática e educativa.

---

<h2 id="func"> 🚀 Funcionalidadesl</h2>

### 🔹 Cadastro e Login
- Registro com informações básicas e autenticação para acesso à plataforma de apostas.

### 🔹 Criação de Eventos
- Permite criação de eventos com título, descrição, cotações e período de apostas.

### 🔹 Exploração de Eventos
- Página principal com eventos em destaque, busca e filtros de pesquisa.

### 🔹 Carteira Virtual
- Inclui adição de créditos simulados, consulta de saldo, histórico de transações e saques simulados com taxas.

### 🔹 Apostas
- Opções de aposta com escolha entre `SIM` ou `NÃO`, debitando o valor apostado da carteira.

### 🔹 Moderação de Eventos
- Moderadores avaliam e aprovam ou rejeitam eventos antes de sua publicação, garantindo conformidade com as regras da plataforma e enviando o feedback por e-mail.
- Após o evento, moderadores confirmam o resultado e o sistema lida com a distribuição de prêmios aos vencedores.

---

<h2 id="arq"> 📐 Arquitetura MVC</h2>

A arquitetura `MVC (Model-View-Controller)` foi utilizada neste projeto para organizar e estruturar o desenvolvimento da aplicação,
separando as responsabilidades em três camadas principais:

### 🔹Model (Modelo):
- Representa a camada de dados e lógica de negócio.
- Gerencia as interações com o banco de dados, como salvar, consultar e atualizar informações.
- Exemplo no projeto: Gerenciamento de eventos, apostas e saldo dos usuários.

### 🔹View (Visão):
- Responsável pela interface do usuário e exibição das informações.
- Implementada usando HTML, CSS e JavaScript para garantir uma experiência amigável e responsiva.
- Exemplo no projeto: Páginas de login, criação de eventos, e consulta de apostas.

### 🔹Controller (Controlador):
- Atua como intermediário entre o modelo e a visão.
- Recebe as requisições do usuário, processa as regras de negócio e retorna as respostas apropriadas.
- Exemplo no projeto: Rotas de autenticação, criação de eventos e processamento de apostas.

### 🔄 Fluxo de Operação:
1. O **usuário** interage com a **View** (ex.: envia uma requisição de criação de evento).
2. A **Controller** processa a requisição, acessa os dados no **Model** e aplica as regras de negócio.
3. O resultado é enviado de volta para a **View**, que o apresenta ao usuário.

### 🎯 Benefícios da arquitetura MVC:
- **Separação de responsabilidades**: Cada camada tem um papel claro, facilitando manutenção e evolução do código.
- **Reutilização de componentes**: Possibilidade de reaproveitar lógicas de negócio e interface em diferentes partes do sistema.
- **Escalabilidade**: Organização que facilita a adição de novas funcionalidades.

---

<h2 id="obs">📝 Observações Importantes</h2>

- Todos os arquivos HTML e CSS estão contidos diretamente na camada View, sem a utilização de uma pasta `public`. Essa organização simplificada foi escolhida por não utilizarmos EJS ou outro template engine.

- Para manter o projeto focado, algumas funcionalidades foram excluídas, como:
    - Frontend para a moderação, avaliação e finalização de eventos são feitas diretamente no postman.
    - Alterações no perfil de usuário (alteração de senha, email, etc.).
    - Ranking de apostadores e visibilidade pública dos perfis.
    - Opções de disputa ou revisão de resultados para apostas concluídas.

---

<h2 id="tech"> 🛠️ Tecnologias Utilizadas</h2>
<table>
  <tr>
    <th>Categoria</th>
    <th>Tecnologia</th>
    <th>Descrição</th>
  </tr>

  <tr>
    <td>Linguagem Backend</td>
    <td>TypeScript</td>
    <td>Superset de JavaScript que adiciona tipagem estática ao código.</td>
  </tr>
  <tr>
    <td>Runtime Backend</td>
    <td>Node.js</td>
    <td>Ambiente de execução para JavaScript no lado do servidor.</td>
  </tr>
  <tr>
    <td>Framework Backend</td>
    <td>Express.js</td>
    <td>Framework minimalista para criação de APIs e aplicações web.</td>
  </tr>

  <tr>
    <td>Linguagem Frontend</td>
    <td>JavaScript</td>
    <td>Linguagem de programação para adicionar interatividade ao frontend.</td>
  </tr>
  <tr>
    <td>Linguagem de Estilo</td>
    <td>CSS</td>
    <td>Linguagem para definição de estilos e layouts de páginas web.</td>
  </tr>
  <tr>
    <td>Linguagem de Estrutura</td>
    <td>HTML5</td>
    <td>Linguagem de marcação para estruturar o conteúdo das páginas web.</td>
  </tr>

  <tr>
    <td>Sistema de Banco de Dados</td>
    <td>Oracle</td>
    <td>Sistema de banco de dados relacional.</td>
  </tr>
  <tr>
    <td>Ferramenta de Banco de Dados</td>
    <td>DataGrip</td>
    <td>Ferramenta avançada para gerenciamento e consulta de bancos de dados.</td>
  </tr>


  <tr>
    <td>Sistema de Controle de Versão</td>
    <td>Git</td>
    <td>Sistema de controle de versão distribuído para rastrear mudanças no código.</td>
  </tr>
  <tr>
    <td>IDE para Desenvolvimento</td>
    <td>WebStorm</td>
    <td>IDE para desenvolvimento web.</td>
  </tr>
</table>

---

<h2 id="telas">💻 Telas do Sistema</h2>
Nesta seção, apresentamos as principais telas do sistema, destacando suas funcionalidades.
<div align="center"><h3>Tela de Cadastro</h3></div>
<p align="center"><i>A Tela de Cadastro permite que novos usuários criem contas, preenchendo informações obrigatórias como nome, e-mail, senha e data de nascimento.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Tela%20de%20Cadastro.png" alt="Tela de Cadastro" width="700"/>
</div>

<div align="center"><h3>Tela de Login</h3></div>
<p align="center"><i>Na Tela de Login, o usuário pode inserir suas credenciais para acessar o sistema. Inclui validação de dados e mensagens de erro em caso de falha.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Tela%20de%20Login.png" alt="Tela de Login" width="700"/>
</div>

<div align="center"><h3>Home Page</h3></div>
<p align="center"><i>A Home Page permite ao usuário visualizar uma visão geral do sistema, com atalhos para funcionalidades principais e informações resumidas.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Home%20Page.png" alt="Home Page" width="700"/>
</div>

<div align="center"><h3>Criar Evento</h3></div>
<p align="center"><i>A Tela Criar Evento permite ao usuário adicionar novos eventos ao sistema, definindo detalhes como titulo, descrição, valor de cota e periodo de apostas.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Criar%20Evento.png" alt="Criar Evento" width="700"/>
</div>

<div align="center"><h3>Meus Eventos</h3></div>
<p align="center"><i>A Tela Meus Eventos exibe os eventos criados ou registrados pelo usuário, permitindo sua visualização ou exclusão.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Meus%20Eventos.png" alt="Meus Eventos" width="700"/>
</div>

<div align="center"><h3>Apostar em Evento</h3></div>
<p align="center"><i>A Tela Apostar em Evento permite ao usuário visualizar as informações do evento e realizar apostas.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Apostar%20Em%20Evento.png" alt="Apostar em Evento" width="700"/>
</div>

<div align="center"><h3>Carteira</h3></div>
<p align="center"><i>A Tela Carteira apresenta o saldo do usuário, exibe histórico de transações e apostas além de permitir saque e adição de valores.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Carteira.png" alt="Carteira" width="700"/>
</div>

<div align="center"><h3>Erro 401</h3></div>
<p align="center"><i>A Tela de Erro 401 informa ao usuário que o acesso foi negado por falta de autenticação.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Erro%20401.png" alt="Erro 401" width="700"/>
</div>

<div align="center"><h3>Erro 404</h3></div>
<p align="center"><i>A Tela de Erro 404 informa ao usuário que a página solicitada não foi encontrada.</i></p>
<div align="center">
    <img src="Projeto/src/view/assets/Telas%20do%20Sistema/Erro%20404.png" alt="Erro 404" width="700"/>
</div>

---

<h2 id="colab">🤝 Colaboradores</h2>
Um agradecimento especial a todas as pessoas que contribuíram para este projeto.

<table>
  <!-- Informações sobre o Jean -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/JeanYuki148" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/177983282?v=4" width="100px;" alt="Jean Yuki Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>JEAN YUKI</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>Documentação(DB), Qualidade e Testes</h3>
        <p>
            Jean foi responsável pela modelagem do banco de dados e pelos testes do projeto. Ele criou diagramas que representavam
            a estrutura e os relacionamentos entre as tabelas, fornecendo uma base sólida para o desenvolvimento e a organização
            dos dados no sistema. Além disso, atuou essencialmente como QA (Quality Assurance), garantindo que o sistema atendesse aos requisitos
            definidos na documentação, realizando testes para assegurar a correção das funcionalidades e o funcionamento adequado do sistema.
        </p>
    </td>
  </tr>

  <!-- Informações sobre a Jhenifer -->
  <tr>
    <td align="center" width="150px">
      <a href="https://github.com/JheniferLais" style="color: #ffffff; text-decoration: none;">
        <img src="https://avatars.githubusercontent.com/u/145135351" width="100px;" alt="Jhenifer Laís Profile Picture" style="border-radius: 50%;"/><br>
        <sub>
          <strong>JHENIFER LAÍS</strong>
        </sub>
      </a>
    </td>
    <td>
      <h3>Backend, Banco de Dados, Frontend, Integração e Planejamento</h3>
        <p>
            Jhenifer foi responsável pelo desenvolvimento do backend, implementando a arquitetura MVC (Model-View-Controller)
            para estruturar o projeto de forma organizada. Implementou o banco de dados, cuidando da criação da entidades
            e relacionamentos. Realizou a integração entre o backend e o frontend, além de desenvolver a lógica de programação 
            responsável pela interatividade e funcionalidade do frontend utilizando JavaScript. Além disso, atuou na
            organização do time, colaborando na coordenação de tarefas e no cumprimento dos prazos.
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
      <h3>Frontend, Backend, UI/UX, Prototipação, Integração e Depuração</h3>
        <p>
            João foi responsável por grande parte do planejamento de disposição de elementos e lógica de navegação
            entre as páginas web. Significante quanto a parte da programação em Frontend (html, css e javaScript),
            e organização da linha de trabalho. Além de contribuir com a integração "Back to Front", conectando parte da
            lógica bruta do backend com os elementos distintos do frontend, João também foi notório quanto a identidade
            visual do projeto, e grande habilidade na aplicação de técnicas de depuração, contribuindo para a identificação 
            e resolução eficiente de problemas durante o desenvolvimento.
        </p>
    </td>
  </tr>
</table>

<img width=100% src="https://capsule-render.vercel.app/api?type=waving&color=2480D3&height=120&section=footer"/>
