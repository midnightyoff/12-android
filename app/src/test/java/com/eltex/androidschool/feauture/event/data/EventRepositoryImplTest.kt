package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.data.installAppResponseValidator
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventType
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
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
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.channels.UnresolvedAddressException
import java.time.Instant

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
    fun `getEvents - success - returns mapped events`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(eventsJson),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        val events = repository.getEvents()

        val expected = Event(
            id = 1L,
            author = "Alice",
            content = "Hello",
            published = Instant.parse("2026-05-01T10:00:00Z"),
            type = EventType.OFFLINE,
            datetime = Instant.parse("2026-05-01T12:00:00Z"),
            link = "https://example.com",
            likedByMe = true,
            likes = 3,
            participatedByMe = false,
            participants = 2,
        )
        assertEquals(listOf(expected), events)

        val request = mockEngine.requestHistory.single()
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("/api/events", request.url.encodedPath)
    }

    @Test
    fun `getEvents - 403 response - throws Forbidden`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        assertFailsWith<AppException.Forbidden> { repository.getEvents() }
    }

    @Test
    fun `getEvents - 401 response - throws Forbidden`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.Unauthorized) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        assertFailsWith<AppException.Forbidden> { repository.getEvents() }
    }

    @Test
    fun `getEvents - 500 response - throws UnknownException with code`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        val error = assertFailsWith<AppException.UnknownException> { repository.getEvents() }
        assertEquals(500, error.code)
    }

    @Test
    fun `getEvents - unresolved address - throws NetworkException`() = runTest {
        val mockEngine = MockEngine { throw UnresolvedAddressException() }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        assertFailsWith<AppException.NetworkException> { repository.getEvents() }
    }

    @Test
    fun `likeById - likedByMe false - sends POST to likes`() = runTest {
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

    @Test
    fun `likeById - likedByMe true - sends DELETE to likes`() = runTest {
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

    @Test
    fun `likeById - 403 response - throws Forbidden`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        assertFailsWith<AppException.Forbidden> { repository.likeById(id = 1L, likedByMe = false) }
    }

    @Test
    fun `participateById - participatedByMe false - sends POST to participants`() = runTest {
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

    @Test
    fun `participateById - participatedByMe true - sends DELETE to participants`() = runTest {
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

    @Test
    fun `participateById - 500 response - throws UnknownException`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        val error = assertFailsWith<AppException.UnknownException> {
            repository.participateById(id = 1L, participatedByMe = false)
        }
        assertEquals(500, error.code)
    }

    @Test
    fun `saveEvent - success - sends POST events and returns mapped event`() = runTest {
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

    @Test
    fun `saveEvent - 403 response - throws Forbidden`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.Forbidden) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        assertFailsWith<AppException.Forbidden> { repository.saveEvent(id = 0L, content = "x") }
    }

    @Test
    fun `deleteById - success - sends DELETE`() = runTest {
        val mockEngine = MockEngine { respond(content = "", status = HttpStatusCode.OK) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        repository.deleteById(id = 42L)

        val request: HttpRequestData = mockEngine.requestHistory.single()
        assertEquals(HttpMethod.Delete, request.method)
        assertEquals("/api/events/42", request.url.encodedPath)
    }

    @Test
    fun `deleteById - 500 response - throws UnknownException`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val repository = EventRepositoryImpl(client = buildClient(mockEngine))

        val error = assertFailsWith<AppException.UnknownException> { repository.deleteById(id = 42L) }
        assertEquals(500, error.code)
    }

    private fun buildClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(json = Json { ignoreUnknownKeys = true })
        }

        expectSuccess = true

        installAppResponseValidator()

        defaultRequest {
            url("https://example.test/api/")
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * Inline so the [block] runs in the caller's coroutine context, letting suspend repository
     * calls be asserted directly without [kotlinx.coroutines.runBlocking].
     */
    private inline fun <reified T : Throwable> assertFailsWith(block: () -> Unit): T {
        val thrown = try {
            block()
            null
        } catch (e: Throwable) {
            e
        }
        assertTrue(
            "Expected ${T::class.java.name} but was ${thrown?.let { it::class.java.name }}",
            thrown is T,
        )
        return thrown as T
    }
}