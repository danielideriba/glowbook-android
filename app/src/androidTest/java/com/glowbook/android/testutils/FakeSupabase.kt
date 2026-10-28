package com.glowbook.android.testutils

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList

object FakeSupabase : Interceptor {

    data class RecordedRequest(val method: String, val path: String, val headers: Map<String, String>, val body: String?)

    val requests = CopyOnWriteArrayList<RecordedRequest>()

    @Volatile
    var createAppointmentCode = 200

    val calendarDays: List<LocalDate>
        get() = generateSequence(LocalDate.now()) { it.plusDays(1) }
            .filter { it.dayOfWeek != DayOfWeek.SUNDAY }
            .take(6)
            .toList()

    private val professionals = mapOf(
        1L to """{"id":1,"nome":"Camila Rocha","especialidade":"Cabeleireira","avaliacao":4.9,"avatar":"👩","cor_fundo":"#FDE8EC","disponivel":true,"categoria_id":1}""",
        2L to """{"id":2,"nome":"Bea Lima","especialidade":"Manicure","avaliacao":4.7,"avatar":"🧑","cor_fundo":"#E8F0FD","disponivel":true,"categoria_id":2}"""
    )

    private val services = mapOf(
        1L to """{"id":1,"nome":"Corte","categoria_id":1,"preco":120.0,"duracao_minutos":45,"ativo":true,"icone":"scissors"}""",
        2L to """{"id":2,"nome":"Escova","categoria_id":1,"preco":140.0,"duracao_minutos":90,"ativo":true,"icone":"hair-wind"}""",
        3L to """{"id":3,"nome":"Manicure","categoria_id":2,"preco":60.0,"duracao_minutos":60,"ativo":true,"icone":"nail-art-complete"}"""
    )

    fun reset() {
        requests.clear()
        createAppointmentCode = 200
    }

    fun rpcRequests(): List<RecordedRequest> = requests.filter { it.path == "rpc/criar_agendamento" }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.removePrefix("/rest/v1/")
        requests += RecordedRequest(
            method = request.method,
            path = path,
            headers = request.headers.toMap(),
            body = request.body?.let { body -> Buffer().also { body.writeTo(it) }.readUtf8() }
        )

        val (code, json) = when (path) {
            "profissionais" -> 200 to professionalsFor(request)
            "categorias" -> 200 to """[{"id":1,"nome":"Cabelo"},{"id":2,"nome":"Unhas"}]"""
            "servicos" -> 200 to servicesFor(request)
            "proximos_agendamentos" -> 200 to """[{"id":5,"id_servicos":1,"data":"${calendarDays.last()}","horario":"10:00:00",
                "status":"confirmado","profissional_id":1,"profissional":"Camila Rocha","avaliacao":4.9,"categoria":"Cabelo"}]"""
            // One booking on the second calendar day at 11:00 so that slot shows as taken.
            "agendamentos" -> 200 to """[{"id":9,"data":"${calendarDays[1]}","horario":"11:00:00","status":"confirmado","profissional_id":1}]"""
            "historico_agendamentos" -> 200 to "[]"
            "rpc/criar_agendamento" -> createAppointmentCode to
                if (createAppointmentCode == 200) "42" else """{"code":"P0001","message":"horario indisponivel"}"""
            else -> 404 to """{"code":"PGRST205","message":"not found"}"""
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 200) "OK" else "Bad Request")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun professionalsFor(request: Request): String {
        val id = request.url.queryParameter("id")?.removePrefix("eq.")?.toLongOrNull()
        val rows = if (id != null) listOfNotNull(professionals[id]) else professionals.values.toList()
        return rows.joinToString(",", "[", "]")
    }

    private fun servicesFor(request: Request): String {
        val ids = request.url.queryParameter("id")
            ?.removePrefix("in.(")?.removeSuffix(")")
            ?.split(",")?.mapNotNull { it.toLongOrNull() }
        val rows = if (ids != null) ids.mapNotNull { services[it] } else services.values.toList()
        return rows.joinToString(",", "[", "]")
    }
}
