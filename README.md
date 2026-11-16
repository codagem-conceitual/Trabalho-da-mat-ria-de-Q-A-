# ClínicaFácil

Sistema web de agendamento online para clínicas-escola. O paciente da comunidade escolhe o estagiário, a
especialidade e o horário, e acompanha as suas consultas pelo celular ou pelo computador. A coordenação da
clínica cadastra especialidades e estagiários, e cada estagiário define os seus horários de atendimento.

Projeto da disciplina **Gestão e Qualidade de Software** (A3) e do programa **Ânima Hub**.

> Este sistema foi desenvolvido a partir do [AppointmentScheduler](https://github.com/slabiak/AppointmentScheduler),
> de Tomasz Słabiak, distribuído sob a licença MIT. A equipe traduziu o sistema para o português, adaptou-o
> para a realidade de uma clínica-escola e corrigiu defeitos encontrados nos testes.

## Tecnologias

| Parte | Tecnologia |
| --- | --- |
| Linguagem | Java (roda no Java 21) |
| Framework | Spring Boot 2.2 (Spring MVC, Spring Security, Spring Data JPA) |
| Telas | Thymeleaf, Bootstrap 4, FullCalendar, DataTables |
| Banco de dados | MySQL (produção) ou H2 em memória (perfil `local`) |
| Testes | JUnit 4 e Mockito |
| Build | Maven (já vem o `mvnw`, não precisa instalar) |

## Como rodar no computador

Precisa só do **Java 21** instalado. Não precisa instalar MySQL.

1. Abra o terminal na pasta do projeto.
2. Rode:

```bash
mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

3. Abra <http://localhost:8080> no navegador.

O perfil `local` usa um banco H2 em memória: os dados de exemplo são criados ao ligar e **somem ao desligar**.
O envio de e-mail fica desligado nesse perfil.

## Usuários de exemplo

Todos usam a senha `qwerty123`. São pessoas fictícias.

| Perfil | Usuário | Quem é |
| --- | --- | --- |
| Coordenação (administrador) | `coordenacao` | Coordenação da clínica-escola |
| Estagiário | `estagiario` | Lucas Pereira (Psicologia e Nutrição) |
| Estagiário | `estagiaria` | Ana Costa (Fisioterapia e Odontologia) |
| Paciente | `paciente` | Maria Souza |

Especialidades cadastradas: Psicologia (50 min), Fisioterapia (50 min), Nutrição (40 min) e Odontologia (60 min),
todas gratuitas. Horário dos estagiários: segunda a sexta, das 8h às 17h, com almoço das 12h às 13h.

## Perfis de acesso

- **Coordenação:** cadastra especialidades e estagiários, liga cada estagiário às especialidades que ele atende e
  vê todas as consultas e pacientes.
- **Estagiário:** define o próprio horário de atendimento e os intervalos, e vê só as próprias consultas.
- **Paciente:** faz o próprio cadastro na tela de login, agenda, cancela e acompanha as consultas.

## Como funciona o agendamento

1. Em **Minhas consultas**, o paciente clica em **Nova consulta**.
2. Escolhe o estagiário.
3. Escolhe a especialidade.
4. Escolhe o dia e um dos horários livres.
5. Confere os dados e clica em **Confirmar agendamento**.

Os horários livres são calculados assim (método `getAvailableHours` de `AppointmentServiceImpl`):

1. pega o horário de trabalho do estagiário no dia escolhido;
2. tira os intervalos (por exemplo, o almoço);
3. tira as consultas já marcadas do estagiário naquele dia;
4. tira as consultas já marcadas do paciente naquele dia;
5. divide o tempo que sobrou pela duração da especialidade.

## Status da consulta

| Status | Quando acontece |
| --- | --- |
| Agendada | Logo depois de o paciente marcar a consulta |
| Realizada | Quando o horário de fim da consulta passa |
| Confirmada | 24 horas depois de realizada, se o paciente não contestar |
| Encerrada | No primeiro dia do mês seguinte, quando o comprovante é emitido |
| Cancelada | Quando o paciente ou o estagiário cancela |
| Contestação pedida | Quando o paciente informa que a consulta não aconteceu (até 24 horas depois) |
| Contestação aceita | Quando o estagiário confirma que a consulta não aconteceu |

Regras de cancelamento: o paciente pode cancelar até 24 horas antes da consulta e no máximo 1 vez por mês.
O estagiário pode cancelar qualquer consulta com status Agendada.

## Testes automáticos

Para rodar os testes de unidade:

```bash
mvnw test -DskipITs
```

Os testes ficam em `src/test/java`. Os testes de integração (arquivos que terminam em `IT`) precisam do Docker
e por isso ficam de fora do comando acima.

Testes criados pela equipe:

- `HorariosLivresTest`: confere que marcar uma consulta não esconde os outros horários do mesmo turno e que
  o cálculo de horários livres não altera o horário de trabalho salvo do estagiário (defeito encontrado e
  corrigido pela equipe).

## Licença

O código original é distribuído sob a licença MIT. O texto da licença fica no arquivo [LICENSE.md](LICENSE.md)
e precisa ser mantido em inglês, como está, porque é o texto legal original.
