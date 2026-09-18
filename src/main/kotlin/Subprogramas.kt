package com.hotelterabithia

import kotlin.math.ceil
import kotlin.math.floor

// ============================================================================
// SUBPROGRAMA 1 — RESERVAS DE QUARTOS
// ============================================================================

fun subprogramaReservas() {
    println("\n[Reservas de Quartos]")

    print("Informe o valor da diária: ")
    val diaria = readlnOrNull()?.toDoubleOrNull() ?: -1.0

    print("Informe a quantidade de diárias (1-30): ")
    val diarias = readlnOrNull()?.toIntOrNull() ?: -1

    if (diaria <= 0 || diarias !in 1..30) {
        println("Valor inválido, ${BancoMemoria.usuarioLogado}")
        return
    }

    print("Informe o nome do hóspede: ")
    val nomeHospede = readlnOrNull().orEmpty()

    print("Tipo de quarto (S - Standard / E - Executivo / L - Luxo): ")
    val tipoInput = readlnOrNull()?.uppercase().orEmpty()
    val (fator, nomeTipoExtenso) = when (tipoInput) {
        "S" -> Pair(1.00, "Standard")
        "E" -> Pair(1.35, "Executivo")
        "L" -> Pair(1.65, "Luxo")
        else -> {
            println("Tipo de quarto inválido.")
            return
        }
    }

    var quartoObj: Quarto? = null
    while (quartoObj == null) {
        print("Escolha um quarto (1-20): ")
        val numQuarto = readlnOrNull()?.toIntOrNull() ?: -1

        if (numQuarto !in 1..20) {
            println("Quarto inexistente. Escolha entre 1 e 20.")
            continue
        }

        val q = BancoMemoria.quartos.find { it.numero == numQuarto }
        if (q != null && q.ocupado) {
            println("Quarto já está ocupado.")
            exibirMapaQuartos()
        } else {
            quartoObj = q
        }
    }

    val subtotal = diaria * diarias * fator
    val taxaServico = subtotal * 0.10
    val totalFinal = subtotal + taxaServico

    println("\nResumo:")
    println("Hóspede: $nomeHospede")
    println("Quarto: ${quartoObj.numero} ($nomeTipoExtenso)")
    println("Subtotal: R$ $subtotal")
    println("Taxa de serviço (10%): R$ $taxaServico")
    println("Total: R$ $totalFinal")

    print("\n${BancoMemoria.usuarioLogado}, confirma a reserva? (S/N): ")
    val confirmacao = readlnOrNull()?.uppercase().orEmpty()

    if (confirmacao == "S") {
        quartoObj.ocupado = true
        quartoObj.hospedeAtual = nomeHospede
        quartoObj.tipo = nomeTipoExtenso

        val novaReserva = ReservaQuarto(
            hospede = nomeHospede,
            numeroQuarto = quartoObj.numero,
            tipoQuarto = nomeTipoExtenso,
            diarias = diarias,
            valorTotal = totalFinal
        )
        BancoMemoria.reservasQuartos.add(novaReserva)

        println("Reserva efetuada com sucesso.")
        exibirMapaQuartos()
    } else {
        println("Reserva não efetuada.")
    }
}

fun exibirMapaQuartos() {
    println("\nMapa de Quartos (4x5):")
    BancoMemoria.quartos.forEach { q ->
        val status = if (q.ocupado) "O" else "L"
        print("[${q.numero}: $status] ")
        if (q.numero % 5 == 0) println()
    }
}

// ============================================================================
// SUBPROGRAMA 2 — CADASTRO DE HÓSPEDES
// ============================================================================

fun subprogramaHospedes() {
    var voltar = false
    while (!voltar) {
        println("\n[Cadastro de Hóspedes]")
        println("1-Cadastrar  2-Pesquisar exato  3-Pesquisar prefixo  4-Listar  5-Atualizar  6-Remover  7-Voltar")
        val opcao = lerOpcaoInteiro("Opção: ")

        when (opcao) {
            1 -> {
                if (BancoMemoria.hospedesCadastrados.size >= 15) {
                    println("Máximo de cadastros atingido")
                } else {
                    print("Nome do hóspede: ")
                    val nome = readlnOrNull()?.trim().orEmpty()
                    if (BancoMemoria.hospedesCadastrados.any { it.nome.equals(nome, ignoreCase = true) }) {
                        println("Hóspede já cadastrado")
                    } else {
                        val novoId = (BancoMemoria.hospedesCadastrados.maxOfOrNull { it.id } ?: 0) + 1
                        val novoHospede = Hospede(id = novoId, nome = nome)
                        BancoMemoria.hospedesCadastrados.add(novoHospede)
                        println("Hóspede cadastrado com sucesso.")
                    }
                }
            }
            2 -> {
                print("Nome exato para pesquisa: ")
                val termo = readlnOrNull()?.trim().orEmpty()
                val encontrado = BancoMemoria.hospedesCadastrados.find { it.nome.equals(termo, ignoreCase = true) }
                if (encontrado != null) {
                    println("Hóspede ${encontrado.nome} foi encontrado.")
                } else {
                    println("Hóspede não encontrado")
                }
            }
            3 -> {
                print("Prefixo do nome: ")
                val prefixo = readlnOrNull()?.trim().orEmpty()
                val resultados = BancoMemoria.hospedesCadastrados.filter { it.nome.startsWith(prefixo, ignoreCase = true) }
                if (resultados.isEmpty()) {
                    println("Hóspede não encontrado")
                } else {
                    println("Resultados:")
                    resultados.forEachIndexed { index, h -> println("[$index] ${h.nome}") }
                }
            }
            4 -> {
                println("Listagem de Hóspedes (A-Z):")
                BancoMemoria.hospedesCadastrados.sortedBy { it.nome }.forEachIndexed { index, h ->
                    println("[$index] ${h.nome} — Cadastrado em: ${h.dataHoraCadastro}")
                }
            }
            5 -> {
                print("Índice do hóspede a atualizar: ")
                val idx = readlnOrNull()?.toIntOrNull() ?: -1
                val listaOrdenada = BancoMemoria.hospedesCadastrados.sortedBy { it.nome }
                if (idx in listaOrdenada.indices) {
                    val hospedeParaAtualizar = listaOrdenada[idx]
                    print("Novo nome: ")
                    hospedeParaAtualizar.nome = readlnOrNull().orEmpty()
                    println("Operação realizada com sucesso")
                } else {
                    println("Hóspede não encontrado")
                }
            }
            6 -> {
                print("Índice do hóspede a remover: ")
                val idx = readlnOrNull()?.toIntOrNull() ?: -1
                val listaOrdenada = BancoMemoria.hospedesCadastrados.sortedBy { it.nome }
                if (idx in listaOrdenada.indices) {
                    val hospedeParaRemover = listaOrdenada[idx]
                    BancoMemoria.hospedesCadastrados.remove(hospedeParaRemover)
                    println("Operação realizada com sucesso")
                } else {
                    println("Hóspede não encontrado")
                }
            }
            7 -> voltar = true
            else -> println("Opção inválida.")
        }
    }
}

// ============================================================================
// SUBPROGRAMA 3 — EVENTOS (PIPELINE COMPLETO)
// ============================================================================

fun subprogramaEventos() {
    println("\n[Eventos - Pipeline Completo]")
    print("Convidados: ")
    val convidados = readlnOrNull()?.toIntOrNull() ?: -1

    if (convidados < 0) {
        println("Número de convidados inválido")
        return
    } else if (convidados > 350) {
        println("Capacidade excedida. O limite máximo é de 350 convidados.")
        return
    }

    val auditorio: String
    val cadeirasExtras: Int
    if (convidados <= 150) {
        auditorio = "Laranja"
        cadeirasExtras = 0
        println("Auditório selecionado: Laranja")
    } else if (convidados <= 220) {
        auditorio = "Laranja"
        cadeirasExtras = convidados - 150
        println("Auditório selecionado: Laranja ($cadeirasExtras cadeiras adicionais)")
    } else {
        auditorio = "Colorado"
        cadeirasExtras = 0
        println("Auditório selecionado: Colorado")
    }

    print("Dia da semana (ex: segunda, sabado): ")
    val dia = readlnOrNull()?.lowercase().orEmpty()
    print("Hora inicial (0 a 23): ")
    val horaInicio = readlnOrNull()?.toIntOrNull() ?: -1
    print("Duração em horas (1 a 12): ")
    val duracao = readlnOrNull()?.toIntOrNull() ?: -1

    val horaFim = horaInicio + duracao
    val fimDeSemana = dia == "sabado" || dia == "domingo"
    val limiteHorario = if (fimDeSemana) 15 else 23

    if (horaInicio !in 0..23 || duracao !in 1..12 || horaFim > limiteHorario) {
        println("Horário indisponível ou fora da janela de funcionamento do auditório.")
        return
    }

    print("Nome da empresa: ")
    val empresa = readlnOrNull().orEmpty()

    val baseGarcons = ceil(convidados / 12.0).toInt()
    val reforcoGarcons = floor(duracao / 2.0).toInt()
    val totalGarcons = baseGarcons + reforcoGarcons
    val custoGarcons = totalGarcons * duracao * 10.50

    val cafeL = convidados * 0.2
    val aguaL = convidados * 0.5
    val salgadosQtd = convidados * 7
    val custoBuffet = (cafeL * 0.80) + (aguaL * 0.40) + ((salgadosQtd / 100.0) * 34.00)

    val totalGeral = custoGarcons + custoBuffet

    println("\n--- Relatório Parcial do Evento ---")
    println("Empresa: $empresa | Auditório: $auditorio | Data: $dia às ${horaInicio}hs (Fim: ${horaFim}hs)")
    println("Garçons necessários: $totalGarcons — Custo: R$ %.2f".format(custoGarcons))
    println("Buffet -> Café: %.1f L | Água: %.1f L | Salgados: %d un — Custo: R$ %.2f".format(cafeL, aguaL, salgadosQtd, custoBuffet))
    println("Total do evento: R$ %.2f".format(totalGeral))

    print("Confirmar reserva? (S/N): ")
    val conf = readlnOrNull()?.uppercase().orEmpty()
    if (conf == "S") {
        val eventoReserva = EventoReserva(
            auditorio = auditorio,
            empresa = empresa,
            dia = dia,
            horaInicio = horaInicio,
            duracao = duracao,
            convidados = convidados,
            custoTotal = totalGeral
        )
        BancoMemoria.eventosConfirmados.add(eventoReserva)
        println("Reserva efetuada com sucesso.")
    } else {
        println("Reserva não efetuada.")
    }
}

// ============================================================================
// SUBPROGRAMA 4 — AR-CONDICIONADO
// ============================================================================

fun subprogramaArCondicionado() {
    println("\n[Ar-Condicionado - Comparativo Técnico]")
    val orcamentos = mutableListOf<OrcamentoAC>()

    var continuar = "S"
    while (continuar == "S") {
        print("Nome da empresa: ")
        val empresa = readlnOrNull().orEmpty()
        print("Valor por aparelho: ")
        val valorAparelho = readlnOrNull()?.toDoubleOrNull() ?: 0.0
        print("Quantidade de aparelhos: ")
        val qtd = readlnOrNull()?.toIntOrNull() ?: 0
        print("Percentual de desconto (%): ")
        val descPercent = readlnOrNull()?.toDoubleOrNull() ?: 0.0
        print("Quantidade mínima para desconto: ")
        val minDesc = readlnOrNull()?.toIntOrNull() ?: 0
        print("Valor fixo de deslocamento: ")
        val deslocamento = readlnOrNull()?.toDoubleOrNull() ?: 0.0

        val bruto = valorAparelho * qtd
        val desconto = if (qtd >= minDesc) bruto * (descPercent / 100.0) else 0.0
        val total = bruto - desconto + deslocamento

        val orcamento = OrcamentoAC(
            empresa = empresa,
            valorAparelho = valorAparelho,
            quantidade = qtd,
            descontoPercentual = descPercent,
            minimoParaDesconto = minDesc,
            deslocamento = deslocamento,
            total = total
        )
        orcamentos.add(orcamento)

        println("O serviço de $empresa custará R$ %.2f".format(total))

        print("\nDeseja informar novos dados, ${BancoMemoria.usuarioLogado}? (S/N): ")
        continuar = readlnOrNull()?.uppercase().orEmpty()
    }

    if (orcamentos.size >= 2) {
        val melhor = orcamentos.minByOrNull { it.total }!!
        val pior = orcamentos.maxByOrNull { it.total }!!
        val diffPercentual = ((pior.total - melhor.total) / melhor.total) * 100

        println("\nMelhor orçamento: ${melhor.empresa} — R$ %.2f".format(melhor.total))
        println("Pior orçamento: ${pior.empresa} — R$ %.2f".format(pior.total))
        println("Diferença entre propostas: %.1f%%".format(diffPercentual))
    } else {
        println("É necessário informar ao menos duas empresas para comparação.")
    }
}

// ============================================================================
// SUBPROGRAMA 5 — ABASTECIMENTO
// ============================================================================

fun subprogramaAbastecimento() {
    println("\n[Abastecimento - Análise Econômica]")
    val capacidadeTanque = 42.0

    print("Wayne Oil -> Preço do Álcool: ")
    val alcoolWayne = readlnOrNull()?.toDoubleOrNull() ?: 0.0
    print("Wayne Oil -> Preço da Gasolina: ")
    val gasolinaWayne = readlnOrNull()?.toDoubleOrNull() ?: 0.0

    print("Stark Petrol -> Preço do Álcool: ")
    val alcoolStark = readlnOrNull()?.toDoubleOrNull() ?: 0.0
    print("Stark Petrol -> Preço da Gasolina: ")
    val gasolinaStark = readlnOrNull()?.toDoubleOrNull() ?: 0.0

    val melhorOpcaoWayne = if (alcoolWayne <= gasolinaWayne * 0.70) "Álcool" else "Gasolina"
    val totalWayne = capacidadeTanque * if (melhorOpcaoWayne == "Álcool") alcoolWayne else gasolinaWayne

    val melhorOpcaoStark = if (alcoolStark <= gasolinaStark * 0.70) "Álcool" else "Gasolina"
    val totalStark = capacidadeTanque * if (melhorOpcaoStark == "Álcool") alcoolStark else gasolinaStark

    println("\nWayne Oil: melhor opção = $melhorOpcaoWayne | Total (42L) = R$ %.2f".format(totalWayne))
    println("Stark Petrol: melhor opção = $melhorOpcaoStark | Total (42L) = R$ %.2f".format(totalStark))

    val recomendacao = if (totalWayne <= totalStark) {
        "é mais barato abastecer com $melhorOpcaoWayne no posto Wayne Oil."
    } else {
        "é mais barato abastecer com $melhorOpcaoStark no posto Stark Petrol."
    }

    println("\n${BancoMemoria.usuarioLogado}, $recomendacao")
}

// ============================================================================
// SUBPROGRAMA 6 — RELATÓRIOS OPERACIONAIS
// ============================================================================

fun subprogramaRelatorios() {
    println("\n========================================")
    println("      RELATÓRIOS OPERACIONAIS - $NOME_HOTEL")
    println("========================================")

    val quartosOcupados = BancoMemoria.quartos.count { it.ocupado }
    val taxaOcupacao = (quartosOcupados / 20.0) * 100
    val totalReservasQuartos = BancoMemoria.reservasQuartos.size
    val totalHospedes = BancoMemoria.hospedesCadastrados.size
    val totalEventos = BancoMemoria.eventosConfirmados.size

    val receitaHospedagem = BancoMemoria.reservasQuartos.sumOf { it.valorTotal }
    val receitaEventos = BancoMemoria.eventosConfirmados.sumOf { it.custoTotal }
    val receitaGeral = receitaHospedagem + receitaEventos

    println(String.format("%-30s | %s", "Métrica Operacional", "Valor"))
    println("-".repeat(50))
    println(String.format("%-30s | %d", "Total de Reservas Confirmadas", totalReservasQuartos))
    println(String.format("%-30s | %.1f%% (%d/20)", "Taxa de Ocupação Atual", taxaOcupacao, quartosOcupados))
    println(String.format("%-30s | %d", "Hóspedes Cadastrados", totalHospedes))
    println(String.format("%-30s | %d", "Eventos Confirmados", totalEventos))
    println(String.format("%-30s | R$ %.2f", "Receita de Hospedagem", receitaHospedagem))
    println(String.format("%-30s | R$ %.2f", "Receita de Eventos", receitaEventos))
    println(String.format("%-30s | R$ %.2f", "Receita Acumulada Geral", receitaGeral))
    println("========================================")
}