# Escola Nova Era Tech - Desafio App Rick & Morty

Esta é uma aplicação Android desenvolvida em Kotlin que consome a API pública do Rick and Morty para listar personagens, pesquisar e gerir uma lista de favoritos guardada localmente.

## 📋 Índice

- O Desafio: "App 04: Rick & Morty"
    - Funcionalidades Principais

- O Meu Processo
    - Tecnologias Utilizadas
    - O que Aprendi
---

## 🎯 O Desafio

O objetivo deste desafio foi criar uma aplicação robusta para apresentar as personagens da série *Rick & Morty*, aplicando conceitos do ecossistema Android.

### Funcionalidades Principais:
- **Consumo de API:** Integração com uma API pública para obtenção dos dados em tempo real.
- **Listagem Dinâmica:** Apresentação das personagens com paginação infinita para uma navegação fluida.
- **Procura Avançada:** Permitir ao utilizador procurar personagens específicas através do nome.
- **Detalhes Completos:** Exibição de um ecrã detalhado para cada personagem selecionada.
- **Sistema de Favoritos:** Permitir favoritar e desfavoritar personagens com persistência local.

### ⚠ Importantes:
1. Os dados principais e a listagem geral vêm diretamente da API pública.
2. A lista de favoritos deverá ser totalmente persistida localmente no dispositivo utilizando o **Room Database**.

---

## 🛠️ O Meu Processo

### Tecnologias Utilizadas

O projeto foi construído utilizando as ferramentas e bibliotecas nativas recomendadas para o desenvolvimento Android moderno:

- [Kotlin](https://kotlinlang.org/) - Linguagem de programação oficial e principal do projeto.
- [Retrofit](https://square.github.io/retrofit/) - Cliente HTTP para realizar os pedidos e mapeamento da API.
- [Coil](https://coil-hq.github.io/coil/) - Biblioteca moderna e otimizada para o carregamento de imagens de forma assíncrona.
- [Room Database](https://developer.android.com/training/data-storage/room) - Camada de abstração sobre o SQLite para a persistência local dos favoritos.
- **RecyclerView & Adapters** - Para a renderização eficiente e listagem dos dados no ecrã.

### 🧠 O que Aprendi

Este projeto foi fundamental para consolidar os meus conhecimentos na criação de aplicações baseadas em dados externos e armazenamento local a nível iniciante:

**Integração com APIs:**
* Aprendi a estruturar e consumir dados de uma API, focando-me sobretudo em pedidos `GET`, uma vez que este ecossistema específico não exigia operações de modificação (como `POST`, `PUT`, `PATCH` ou `DELETE`).

**Comunicação entre Ecrãs:**
* Compreendi na prática como capturar os dados obtidos da rede, exibi-los numa `RecyclerView` e passá-los de forma segura entre as diferentes *Activities* utilizando `Intents` e serialização de objetos via `Parcelable`.

**Arquitetura e Organização:**
* A separação do código e das respetivas responsabilidades (API, Base de Dados, Adapters e UI) foi um dos maiores desafios do projeto. Manter o código organizado foi essencial para conseguir:
    - Listar as personagens corretamente.
    - Criar as funcionalidades de acrescentar e remover dados da Room Database.
    - Realizar consultas à base de dados local para renderizar o ecrã de favoritos.
    - Persistir as alterações no ecrã anterior, mantendo o estado do ícone de favorito atualizado de acordo com a tabela do Room.

> **Conclusão:**
> Foi um excelente projeto prático para aplicar chamadas a APIs, entender o ciclo de vida dos dados e dominar a criação e consulta de uma base de dados local num nível inicial de desenvolvimento.

---