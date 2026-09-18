package com.hotelterabithia

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Hospede(
    val id: Int,
    var nome: String,
    val dataHoraCadastro: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
)

data class Quarto(
    val numero: Int,
    var ocupado: Boolean = false,
    var hospedeAtual: String? = null,
    var tipo: String = "Standard"
)

data class ReservaQuarto(
    val hospede: String,
    val numeroQuarto: Int,
    val tipoQuarto: String,
    val diarias: Int,
    val valorTotal: Double,
    val dataReserva: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
)

data class EventoReserva(
    val auditorio: String,
    val empresa: String,
    val dia: String,
    val horaInicio: Int,
    val duracao: Int,
    val convidados: Int,
    val custoTotal: Double
)

data class OrcamentoAC(
    val empresa: String,
    val valorAparelho: Double,
    val quantidade: Int,
    val descontoPercentual: Double,
    val minimoParaDesconto: Int,
    val deslocamento: Double,
    val total: Double
)