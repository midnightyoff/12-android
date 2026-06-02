package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.domain.AppException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.nio.channels.UnresolvedAddressException

class EventRepositoryImplTest {
    private val eventJson = """
        {
            "id": 1,
            "author": "Alice",
            "content": "Hello",
            "published": "2026-05-01T10:00:00Z",
            "type": "OFFLINE",
            "datetime": "2026-05-01T12:00:00Z",
            "link": "https://example.com",
            "likeOwnerIds": [1, 2, 3],
            "likedByMe": true,
            "participantsIds": [1, 2],
            "participatedByMe": false
        }
    """.trimIndent()

    private val eventsJson = "[$eventJson]"

    @Test
    fun `getEvents - success - returns mapped events`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventsJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val events = repository.getEvents()

            assertEquals(1, events.size)
            val event = events.single()
            assertEquals(1L, event.id)
            assertEquals("Alice", event.author)
            assertEquals("Hello", event.content)
            assertEquals(3, event.likes)
            assertEquals(2, event.participants)
            assertEquals(true, event.likedByMe)
            assertEquals(false, event.participatedByMe)

            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/api/events", request.url.encodedPath)
        }
    }

    @Test
    fun `getEvents - 403 response - throws Forbidden`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            assertThrows(AppException.Forbidden::class.java) {
                runBlocking { repository.getEvents() }
            }
        }
    }

    @Test
    fun `getEvents - 401 response - throws Forbidden`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.Unauthorized) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            assertThrows(AppException.Forbidden::class.java) {
                runBlocking { repository.getEvents() }
            }
        }
    }

    @Test
    fun `getEvents - 500 response - throws UnknownException with code`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val error = assertThrows(AppException.UnknownException::class.java) {
                runBlocking { repository.getEvents() }
            }
            assertEquals(500, error.code)
        }
    }

    @Test
    fun `getEvents - unresolved address - throws NetworkException`() {
        runBlocking {
            val mockEngine = MockEngine { throw UnresolvedAddressException() }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            assertThrows(AppException.NetworkException::class.java) {
                runBlocking { repository.getEvents() }
            }
        }
    }

    @Test
    fun `likeById - likedByMe false - sends POST to likes`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val event = repository.likeById(id = 1L, likedByMe = false)

            assertEquals(1L, event.id)
            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/api/events/1/likes", request.url.encodedPath)
        }
    }

    @Test
    fun `likeById - likedByMe true - sends DELETE to likes`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            repository.likeById(id = 7L, likedByMe = true)

            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals("/api/events/7/likes", request.url.encodedPath)
        }
    }

    @Test
    fun `likeById - 403 response - throws Forbidden`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            assertThrows(AppException.Forbidden::class.java) {
                runBlocking { repository.likeById(id = 1L, likedByMe = false) }
            }
        }
    }

    @Test
    fun `participateById - participatedByMe false - sends POST to participants`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val event = repository.participateById(id = 3L, participatedByMe = false)

            assertEquals(1L, event.id)
            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/api/events/3/participants", request.url.encodedPath)
        }
    }

    @Test
    fun `participateById - participatedByMe true - sends DELETE to participants`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            repository.participateById(id = 5L, participatedByMe = true)

            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals("/api/events/5/participants", request.url.encodedPath)
        }
    }

    @Test
    fun `participateById - 500 response - throws UnknownException`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val error = assertThrows(AppException.UnknownException::class.java) {
                runBlocking { repository.participateById(id = 1L, participatedByMe = false) }
            }
            assertEquals(500, error.code)
        }
    }

    @Test
    fun `saveEvent - success - sends POST events and returns mapped event`() {
        runBlocking {
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(eventJson),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val event = repository.saveEvent(id = 0L, content = "Hello")

            assertEquals("Hello", event.content)
            val request = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/api/events", request.url.encodedPath)
        }
    }

    @Test
    fun `saveEvent - 403 response - throws Forbidden`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            assertThrows(AppException.Forbidden::class.java) {
                runBlocking { repository.saveEvent(id = 0L, content = "x") }
            }
        }
    }

    @Test
    fun `deleteById - success - sends DELETE`() {
        runBlocking {
            val mockEngine = MockEngine { respond(content = "", status = HttpStatusCode.OK) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            repository.deleteById(id = 42L)

            val request: HttpRequestData = mockEngine.requestHistory.single()
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals("/api/events/42", request.url.encodedPath)
        }
    }

    @Test
    fun `deleteById - 500 response - throws UnknownException`() {
        runBlocking {
            val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
            val repository = EventRepositoryImpl(client = buildClient(mockEngine))

            val error = assertThrows(AppException.UnknownException::class.java) {
                runBlocking { repository.deleteById(id = 42L) }
            }
            assertEquals(500, error.code)
        }
    }

    private fun buildClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(json = Json { ignoreUnknownKeys = true })
        }

        expectSuccess = true

        HttpResponseValidator {
            handleResponseException {
                when (it) {
                    is ResponseException -> {
                        when (it.response.status) {
                            HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized ->
                                throw AppException.Forbidden()

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
            url("https://example.test/api/")
            contentType(ContentType.Application.Json)
        }
    }
}