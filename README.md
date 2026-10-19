# 🔥 Flame Commerce

E-commerce simples em **Java 17 + Spring Boot 3** (projeto da disciplina Análise e Projeto de Sistemas).
Baseado no quadro do Trello "Analise e projeto de sistemas": requisitos, regras de negócio RN1–RN5,
diagrama ER, classes, sequência e atividades.

## Como rodar

```bash
mvn spring-boot:run        # http://localhost:8080
mvn test                   # testes das regras de negócio
```

Requisitos: JDK 17+ e Maven. O banco é H2 em arquivo (`./data`), criado e populado na primeira execução.

| Perfil  | E-mail             | Senha        |
|---------|--------------------|--------------|
| Admin   | admin@flame.com    | admin123     |
| Cliente | cliente@flame.com  | cliente123   |

## Telas

| Rota | Tela |
|------|------|
| `/` e `/produtos/{id}` | Frente da loja (vitrine, busca, categorias, detalhe) |
| `/login` | Login |
| `/cadastro` | Cadastro de cliente |
| `/carrinho`, `/checkout` | Carrinho e finalização (endereço + pagamento) |
| `/pedidos` | Acompanhar e cancelar pedidos |
| `/admin` | Painel: indicadores, produtos/estoque, pedidos (só ADMIN) |

## Arquitetura (MVC + Repository)

```
controller/   C do MVC: recebe a requisição, valida, devolve a view
service/      regras de negócio (RN1–RN5), transações
repository/   Repository pattern (Spring Data JPA)
model/        M do MVC: entidades do diagrama ER
dto/          formulários com Bean Validation
config/       segurança, seed de dados, dados globais das views
templates/    V do MVC: Thymeleaf
```

O gateway de pagamento é uma interface (`PagamentoGateway`) com implementação simulada:
Pix/boleto aprovam sempre; cartão terminado em `0000` é recusado.

## Regras de negócio no código

| Regra | Onde |
|-------|------|
| RN1 cliente cadastrado para comprar | `SecurityConfig` + `PedidoService.finalizarCompra` |
| RN2 sem estoque não entra no carrinho | `CarrinhoService.garantirEstoque` + recheck no checkout |
| RN3 pedido só após pagamento aprovado | `PedidoService.finalizarCompra` (recusado = nada é gravado) |
| RN4 preço vigente na compra | `ItemPedido.precoUnitario` (cópia no momento da compra) |
| RN5 cancelar só antes do envio | `Pedido.isCancelavel` + `PedidoService.cancelar` (repõe estoque) |

## Tema

As cores vêm do front Alexandria (fundo preto→`#56070c`, card `#1c1e1f`, botão `#56070c`→`#5f1013`,
inputs `#f2f5f6`, fonte Sedan SC). Todos os valores estão em `static/css/theme.css`.
