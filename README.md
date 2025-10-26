# Teste prático - Desenvolvimento Mobile

## Descrição
Você deve desenvolver uma aplicação Android (Java ou Kotlin) que permita aos usuários adicionar, visualizar, editar e excluir atividades. Cada atividade deve conter um título, uma descrição e um status (pendente, em andamento, concluído).

## Requisitos

### 1. Interface
Criar uma interface intuitiva.

### 2. Gerenciamento de Atividades
Permitir que o usuário:
- Adicione novas atividades com título, descrição, status e uma data limite para ser executada.
- Edite atividades existentes.
- Exclua atividades.
- Atualize o status de uma atividade (pendente, em andamento, concluído).
- Marque uma atividade como finalizada, assim ela não será contabilizada como atrasada.

### 3. Exibição das Atividades
- As atividades devem ser agrupadas em listas, por status.
- Cada lista deve exibir o título do status e o total de atividades, além das atividades que a compõem.
- Atividades atrasadas (com data limite anterior à data atual) devem ser destacadas.

### 4. Persistência dos Dados
Armazenar os dados no DataStore ou SQLite para que não sejam perdidos ao recarregar a tela.

### 5. Boas Práticas
Escrever um código limpo, bem organizado e com comentários explicativos, quando necessário.

## Diferenciais (Opcional, mas um plus)
- Permitir que o usuário adicione uma imagem como anexo à atividade, exibindo essa imagem nos detalhes da atividade.
- Implementação de testes unitários.
- Integração com uma API externa para persistência de dados.

---

## Entrega Final


### Status de Implementação (Requisitos)

O projeto cobriu os Requisitos Obrigatórios

#### 1. Gerenciamento de Atividades (CRUD)

* **Adicionar Atividade:** Implementado via `AddTaskModal`. (Status: **CONCLUÍDO**)
* **Editar Atividade:** Implementado via `EditTaskModal` (permite alterar texto, data e status). (Status: **CONCLUÍDO**)
* **Excluir Atividade:** Implementado via `DropdownMenu`. (Status: **CONCLUÍDO**)
* **Atualização de Status:** Implementado com `FilterChip` (Status: **CONCLUÍDO**)

#### 2. Exibição e Persistência

* **Persistência:** Dados salvos localmente com **DataStore**. (Status: **CONCLUÍDO**)
* **Ordenação:** Lista principal ordenada por data limite (`deadline` asc.). (Status: **CONCLUÍDO**)
* **Agrupamento:** Lista filtrável por status (Todos, Pendentes, Em andamento, Concluídos). (Status: **CONCLUÍDO**)
* **Atrasadas:** Atividades pendentes expiradas são **automaticamente excluídas** ao iniciar o app (`cleanupExpiredTasks`). (Status: **CONCLUÍDO**)
* **Boas Práticas:** Código limpo e modular. (Status: **CONCLUÍDO**)

---

#### 3. Diferenciais (Plus)

* **Adicionar Imagem:** Implementada a seleção de imagem (`PickVisualMedia` com fallback) e exibição na `TaskDetailScreen`.
* **Teste Unitário:** Estrutura de testes unitários básica configurada.
* **API Externa:** Funcionalidade não implementada, priorizada a estabilidade da persistência local.

---
#### Mostrando o teste

* **[Explicativo das telas](https://drive.google.com/file/d/17TsmTIkYLUFgg9uil0EYlwd0UlYKQSmq/view?usp=drive_link)**
* **[APK do Teste](https://drive.google.com/file/d/1Hcfnixz_tHh0CYnzuvRt-ypqwxMHAcqG/view?usp=drive_link)**
* **[Vídeo de Demonstração](https://drive.google.com/file/d/1if8EBL7k_UO2RWeAeFi6gPj8RqBeDea3/view?usp=drive_link)**

---

### Como Executar:

1.  **Pré-requisitos:** Android SDK (Mínimo API 26).
2.  **Clone o Repositório:** `git clone https://github.com/andersonlourenc/gerenciador-de-atividades.git`
3.  **Abra no Android Studio** e clique em `Sync Project`.
4.  **Execute:** Utilize um emulador com Google Play Services (API 33+) e clique em `Run 'app'`.



