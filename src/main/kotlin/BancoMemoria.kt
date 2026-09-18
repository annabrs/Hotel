package com.hotelterabithia

object BancoMemoria {
    var usuarioLogado: String = ""
    val quartos: MutableList<Quarto> = (1..20).map { Quarto(numero = it) }.toMutableList()
    val hospedesCadastrados = mutableListOf<Hospede>()
    val reservasQuartos = mutableListOf<ReservaQuarto>()
    val eventosConfirmados = mutableListOf<EventoReserva>()
}