package com.wififred.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xbill.DNS.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object DnsServer {
    private var socket: DatagramSocket? = null
    private var running = false
    private var thread: Thread? = null

    private val localRecords = mutableMapOf<String, String>(
        "wififred.local" to "10.0.0.1",
        "test.local" to "192.168.1.100",
        "myserver.local" to "127.0.0.1"
    )

    private const val UPSTREAM_DNS = "1.1.1.1"

    @Volatile
    private var queryCount: Long = 0

    fun isRunning(): Boolean = running
    fun getPort(): Int = socket?.localPort ?: 0
    fun getQueryCount(): Long = queryCount

    suspend fun start(port: Int = 5353): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (running) return@withContext Result.success("DNS يعمل بالفعل")

            LogStore.info("DNS: بدء الخادم على المنفذ $port...")
            socket = DatagramSocket(port)
            running = true
            queryCount = 0

            thread = Thread {
                val buffer = ByteArray(512)
                while (running) {
                    try {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket?.receive(packet)
                        handleQuery(packet)
                    } catch (e: Exception) {
                        if (running) LogStore.error("DNS error: ${e.message}")
                    }
                }
            }.apply { start() }

            LogStore.info("DNS: ✅ يعمل على المنفذ $port")
            Result.success("DNS يعمل على المنفذ $port")
        } catch (e: Exception) {
            running = false
            LogStore.error("DNS: فشل البدء: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        running = false
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        thread?.interrupt()
        thread = null
        LogStore.info("DNS: تم الإيقاف (استقبل $queryCount استعلام)")
    }

    private fun handleQuery(packet: DatagramPacket) {
        try {
            val data = packet.data.copyOf(packet.length)
            val query = Message(data)
            queryCount++

            val question = query.question
            if (question == null) return

            val qname = question.name.toString().trimEnd('.')
            val qtype = question.type

            LogStore.debug("DNS ← $qname (${Type.string(qtype)})")

            val response = Message.newQuery(question)
            response.header.id = query.header.id
            response.header.setFlag(Flags.QR.toInt())
            response.header.setFlag(Flags.RA.toInt())

            val localIp = localRecords[qname]
            if (localIp != null && qtype == Type.A) {
                val record = ARecord(
                    Name.fromString("$qname."),
                    DClass.IN,
                    300L,
                    InetAddress.getByName(localIp)
                )
                response.addRecord(record, Section.ANSWER)
                LogStore.info("DNS → $qname = $localIp (محلي)")
                sendResponse(packet, response)
                return
            }

            try {
                val resolver = SimpleResolver(UPSTREAM_DNS)
                resolver.setTimeout(5)
                val upstreamQuery = Message.newQuery(question)
                val upstreamResponse = resolver.send(upstreamQuery)

                upstreamResponse.header.id = query.header.id
                LogStore.debug("DNS → $qname (من الخارجي)")
                sendResponse(packet, upstreamResponse)
            } catch (e: Exception) {
                LogStore.warn("DNS: فشل الاستعلام الخارجي لـ $qname")
                response.header.rcode = Rcode.SERVFAIL
                sendResponse(packet, response)
            }
        } catch (e: Exception) {
            LogStore.error("DNS parse error: ${e.message}")
        }
    }

    private fun sendResponse(request: DatagramPacket, response: Message) {
        try {
            val bytes = response.toWire()
            val reply = DatagramPacket(bytes, bytes.size, request.address, request.port)
            socket?.send(reply)
        } catch (e: Exception) {
            LogStore.error("DNS send error: ${e.message}")
        }
    }

    fun addLocalRecord(domain: String, ip: String) {
        localRecords[domain] = ip
        LogStore.info("DNS: إضافة $domain → $ip")
    }

    fun getLocalRecords(): Map<String, String> = localRecords.toMap()
}
