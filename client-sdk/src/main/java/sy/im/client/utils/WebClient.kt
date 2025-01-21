package sy.im.client.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody

object WebClient {
    private val mapper = ObjectMapper().registerKotlinModule()
    val client = OkHttpClient()

    fun <T> get(url: String, typeReference: TypeReference<T>, token: String? = null): T {
        val request = if (token == null) {
            Request.Builder().get().url(url).build()
        } else {
            Request.Builder().get().url(url).header("TOKEN", token).build()
        }
        val str = client.newCall(request).execute().body().string()
        val res = mapper.readValue(str, typeReference)
        return res
    }

    fun <T> post(url: String, typeReference: TypeReference<T>): T {
        val request = Request.Builder().post(RequestBody.create(null, ByteArray(0))).url(url).build()
        val str = client.newCall(request).execute().body().string()
        val res = mapper.readValue(str, typeReference)
        return res
    }
}