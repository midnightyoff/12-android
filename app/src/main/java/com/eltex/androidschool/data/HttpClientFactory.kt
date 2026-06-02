package com.eltex.androidschool.data

import com.eltex.androidschool.domain.AppException
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException

object HttpClientFactory {
    val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                preconfigured = OkHttpFactory.client
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            expectSuccess = true

            HttpResponseValidator {
                handleResponseException {
                    when (it) {
                        is ResponseException -> {
                            when (it.response.status) {
                                HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> {
                                    throw AppException.Forbidden()
                                }

                                else -> throw AppException.UnknownException(
                                    it.response.status.value, it.message,
                                )
                            }
                        }

                        is UnresolvedAddressException -> throw AppException.NetworkException()

                        else -> throw it
                    }
                }
            }

            defaultRequest {
                url("https://eltex-android.ru/api/")
                contentType(ContentType.Application.Json)
            }
        }
    }
}