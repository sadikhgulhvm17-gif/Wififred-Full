package com.wififred.app.data

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object SshManager {
    private var session: Session? = null

    suspend fun connect(
        host: String,
        port: Int,
        username: String,
        password: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            disconnect()

            LogStore.info("SSH: جارٍ الاتصال بـ $host:$port ...")
            LogStore.debug("SSH: المستخدم = $username")

            val jsch = JSch()
            val newSession = jsch.getSession(username, host, port)

            if (password.isNotBlank()) {
                newSession.setPassword(password)
            }

            // قبول مفاتيح المضيف تلقائياً (للتطوير فقط)
            val config = java.util.Properties()
            config["StrictHostKeyChecking"] = "no"
            config["PreferredAuthentications"] = "password,keyboard-interactive,publickey"
            newSession.setConfig(config)
            newSession.timeout = 15000

            newSession.connect(15000)
            session = newSession

            // اختبار سريع: تشغيل whoami و uname
            val testResult = runCommandInternal("whoami && uname -sr") ?: "غير معروف"
            LogStore.info("SSH: ✅ تم الاتصال")
            LogStore.debug("SSH: النظام = ${testResult.trim()}")

            Result.success(testResult.trim())
        } catch (e: Exception) {
            LogStore.error("SSH فشل: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            session?.disconnect()
        } catch (_: Exception) {}
        session = null
        LogStore.info("SSH: تم قطع الاتصال")
    }

    fun isConnected(): Boolean = session?.isConnected == true

    suspend fun runCommand(command: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = runCommandInternal(command)
                ?: return@withContext Result.failure(Exception("فشل التنفيذ"))
            Result.success(result)
        } catch (e: Exception) {
            LogStore.error("SSH command error: ${e.message}")
            Result.failure(e)
        }
    }

    private fun runCommandInternal(command: String): String? {
        val s = session ?: return null
        if (!s.isConnected) return null

        var channel: ChannelExec? = null
        return try {
            channel = s.openChannel("exec") as ChannelExec
            channel.setCommand(command)

            val outputBuffer = ByteArrayOutputStream()
            val errorBuffer = ByteArrayOutputStream()
            channel.outputStream = outputBuffer
            channel.setErrStream(errorBuffer)

            channel.connect(10000)
            Thread.sleep(500)

            while (!channel.isClosed) {
                Thread.sleep(100)
            }
            channel.disconnect()

            val out = outputBuffer.toString("UTF-8")
            val err = errorBuffer.toString("UTF-8")
            if (out.isNotBlank()) out else err
        } catch (e: Exception) {
            channel?.disconnect()
            throw e
        }
    }
}
