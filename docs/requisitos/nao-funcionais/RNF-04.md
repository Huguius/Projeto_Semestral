# RNF-04 — Responsividade

> **Métrica:** Layouts adaptáveis para desktop e mobile  
> **Ferramenta de Verificação:** Testes manuais + CSS (Bootstrap 5)  
> **Prioridade:** Média

---

## 1. Descrição

A interface web deve ser **funcional e visualmente adequada** em **desktop** (≥ 1024px) e **mobile** (320px–768px). A responsividade é garantida pelo uso do **Bootstrap 5** como framework CSS.

---

## 2. Critérios de Verificação

| # | Critério | Tipo |
|---|----------|------|
| CV-01 | Todas as páginas legíveis em telas ≥ 320px de largura | Obrigatório |
| CV-02 | Navbar colapsável em mobile (hamburger menu) | Obrigatório |
| CV-03 | Tabela de livros com scroll horizontal em mobile | Obrigatório |
| CV-04 | Formulários ocupam largura total em mobile | Obrigatório |
| CV-05 | Botões clicáveis com tamanho mínimo de 44x44px (acessibilidade) | Desejável |
| CV-06 | Sem overflow horizontal em nenhuma página | Obrigatório |

---

## 3. Breakpoints (Bootstrap 5)

| Breakpoint | Largura | Dispositivo |
|-----------|---------|-------------|
| `xs` | < 576px | Celular vertical |
| `sm` | ≥ 576px | Celular horizontal |
| `md` | ≥ 768px | Tablet |
| `lg` | ≥ 992px | Desktop |
| `xl` | ≥ 1200px | Desktop grande |

---

## 4. Páginas e Adaptações

| Página | Desktop | Mobile |
|--------|---------|--------|
| **Login / Register** | Formulário centralizado (col-6) | Formulário full width |
| **Lista de livros** | Tabela com todas as colunas | Tabela com scroll horizontal ou cards empilhados |
| **Formulário de livro** | 2 colunas (col-6 + col-6) | 1 coluna (full width) |
| **Detalhes do livro** | Card com description list | Card full width |
| **Navbar** | Links visíveis | Hamburger menu |

---

## 5. Implementação — Layout Base (Thymeleaf + Bootstrap)

```html
<!-- templates/layout.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biblioteca Pessoal</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <!-- Navbar responsiva -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="/">📚 Biblioteca Pessoal</a>
            <button class="navbar-toggler" type="button"
                    data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <!-- links aqui -->
            </div>
        </div>
    </nav>

    <!-- Conteúdo principal -->
    <main class="container mt-4">
        <div th:replace="${content}"></div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

> [!NOTE]
> A tag `<meta name="viewport">` é **essencial** — sem ela, o mobile não renderiza responsivamente.

---

## 6. Estratégia de Verificação

| Método | O que verifica |
|--------|---------------|
| **DevTools (Chrome)** | Testar em viewports: 320px, 768px, 1024px, 1920px |
| **Teste manual mobile** | Acessar via celular real na mesma rede (IP local) |
| **Validação visual** | Sem overflow, sem sobreposição, textos legíveis |

> [!NOTE]
> Não há testes automatizados de responsividade neste projeto. A verificação é **manual** via DevTools e dispositivos reais.

---

## 7. RFs Impactados

| RF | Como RNF-04 se aplica |
|----|-----------------------|
| **RF-01** | Formulário de cadastro responsivo (campos empilham em mobile) |
| **RF-02** | Formulário de login centralizado e adaptável |
| **RF-05** | Tabela de livros com scroll horizontal ou cards em mobile |
| **RF-09** | Card de detalhes full width em mobile |

---

## 8. Conexão com outros RNFs

| RNF | Relação |
|-----|---------|
| **RNF-08 (Manutenibilidade)** | Bootstrap 5 padroniza o CSS — menos código custom |
| **RNF-05 (Segurança)** | Bootstrap CDN via HTTPS |

> [!TIP]
> **Para a oral:** "Usamos Bootstrap 5 para responsividade porque ele é baseado em Flexbox e grid system. A tag `viewport` no `<head>` é obrigatória — sem ela, mobile ignora os media queries. Testamos manualmente em 4 breakpoints usando o DevTools do Chrome."
