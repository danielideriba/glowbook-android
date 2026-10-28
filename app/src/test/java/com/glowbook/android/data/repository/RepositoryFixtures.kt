package com.glowbook.android.data.repository

import com.glowbook.android.data.remote.model.AppointmentUpNextResponse
import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
import com.glowbook.android.data.remote.model.AppointmentsResponse
import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.data.remote.model.ServicesResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

internal fun <T> errorResponse(code: Int): Response<T> =
    Response.error(code, """{"message":"error"}""".toResponseBody("application/json".toMediaType()))

internal fun professional(id: Long = 1) = ProfessionalsResponse(
    id = id,
    nome = "Camila Rocha",
    especialidade = "Cabeleireira",
    avaliacao = 4.9,
    categoriaId = 1
)

internal fun service(id: Long, nome: String = "Serviço $id") = ServicesResponse(
    id = id,
    nome = nome,
    categoriaId = 1,
    preco = 100.0,
    duracaoMinutos = 60
)

internal fun historyRow(id: Long, idServicos: Long?, status: String = "concluido") = AppointmentsHistoryResponse(
    id = id,
    idServicos = idServicos,
    data = "2026-10-20",
    horario = "10:00:00",
    status = status,
    jaPassou = true,
    profissionalId = 1,
    profissional = "Camila Rocha",
    avaliacao = 4.9,
    categoria = "Cabelo"
)

internal fun upNextRow(id: Long, idServicos: Long) = AppointmentUpNextResponse(
    id = id,
    idServicos = idServicos,
    data = "2026-10-30",
    horario = "14:00:00",
    status = "confirmado",
    profissionalId = 1,
    profissional = "Camila Rocha",
    avaliacao = 4.9,
    categoria = "Cabelo"
)

internal fun booking(id: Long, horario: String = "11:00:00") = AppointmentsResponse(
    id = id,
    data = "2026-10-30",
    horario = horario,
    status = "confirmado",
    profissionalId = 1
)
