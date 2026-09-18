package com.hotelterabithia

const val NOME_HOTEL = "Terabithia"
const val SENHA_MESTRE = "2678"

fun main() {
    println("Bem-vindo ao $NOME_HOTEL")

    if (!autenticarUsuario()) {
        println("Sistema bloqueado por excesso de tentativas. Até logo.")
        return
    }

    println("Bem-vindo ao Hotel $NOME_HOTEL, ${BancoMemoria.usuarioLogado}. É um imenso prazer ter você por aqui!")

    var rodando = true
    while (rodando) {
        exibirMenuPrincipal()
        val opcao = lerOpcaoInteiro("Escolha uma opção: ")

        when (opcao) {
            1 -> subprogramaReservas()
            2 -> subprogramaHospedes()
            3 -> subprogramaEventos()
            4 -> subprogramaArCondicionado()
            5 -> subprogramaAbastecimento()
            6 -> subprogramaRelatorios()
            7 -> {
                println("Muito obrigado e até logo, ${BancoMemoria.usuarioLogado}.")
                rodando = false
            }
            else -> println("Opção inválida! Por favor, escolha um número correspondente ao menu.")
        }
    }
}

fun autenticarUsuario(): Boolean {
    var tentativas = 3
    while (tentativas > 0) {
        print("Usuário: ")
        val usuario = readlnOrNull().orEmpty()
        print("Senha: ")
        val senha = readlnOrNull().orEmpty()

        if (senha == SENHA_MESTRE && usuario.isNotBlank()) {
            BancoMemoria.usuarioLogado = usuario.trim()
            return true
        } else {
            tentativas--
            println("Credenciais inválidas. Tentativas restantes: $tentativas")
        }
    }
    return false
}

fun exibirMenuPrincipal() {
    println("\n========================================")
    println("   MENU PRINCIPAL - HOTEL $NOME_HOTEL")
    println("========================================")
    println("1. Reservas de Quartos")
    println("2. Cadastro de Hóspedes")
    println("3. Eventos")
    println("4. Ar-Condicionado")
    println("5. Abastecimento")
    println("6. Relatórios Operacionais")
    println("7. Sair")
    println("----------------------------------------")
}

fun lerOpcaoInteiro(mensagem: String): Int {
    print(mensagem)
    return readlnOrNull()?.toIntOrNull() ?: -999
}