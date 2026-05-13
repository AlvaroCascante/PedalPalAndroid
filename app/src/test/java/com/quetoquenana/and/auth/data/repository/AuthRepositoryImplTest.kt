package com.quetoquenana.and.auth.data.repository

import com.quetoquenana.and.features.authentication.data.local.datasource.AuthUserLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserDataResponseDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserRequestDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserResponseDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserTokenResponseDto
import com.quetoquenana.and.features.authentication.data.remote.dto.CreateUserUserResponseDto
import com.quetoquenana.and.features.authentication.data.repository.AuthRepositoryImpl
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.session.StoredTokens
import com.quetoquenana.and.features.authentication.session.TokenStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    @Test
    fun `restoreSession with valid local completed profile returns authenticated without Firebase`() = runTest {
        val localSession = authSession(isLoggedIn = false)
        val localUser = authUser(profileCompleted = true)
        val fixture = repositoryFixture(
            session = localSession,
            users = mapOf(localSession.userId to localUser)
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Authenticated, result)
        assertEquals(
            StoredTokens(
                accessToken = localSession.accessToken,
                refreshToken = localSession.refreshToken
            ),
            fixture.tokenStorage.savedTokens
        )
        assertFalse(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
    }

    @Test
    fun `restoreSession with valid local incomplete profile returns profile completion without Firebase`() = runTest {
        val localSession = authSession()
        val localUser = authUser(profileCompleted = false)
        val fixture = repositoryFixture(
            session = localSession,
            users = mapOf(localSession.userId to localUser)
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.ProfileCompletionRequired, result)
        assertEquals(localSession.accessToken, fixture.tokenStorage.savedTokens?.accessToken)
        assertEquals(localSession.refreshToken, fixture.tokenStorage.savedTokens?.refreshToken)
        assertFalse(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
    }

    @Test
    fun `restoreSession with blank access token ignores local session and returns unauthenticated when Firebase user is missing`() = runTest {
        val fixture = repositoryFixture(
            session = authSession(accessToken = ""),
            users = mapOf(DEFAULT_USER_ID to authUser()),
            firebaseUser = null
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Unauthenticated, result)
        assertNull(fixture.tokenStorage.savedTokens)
        assertTrue(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
    }

    @Test
    fun `restoreSession with blank refresh token ignores local session and returns unauthenticated when Firebase user is missing`() = runTest {
        val fixture = repositoryFixture(
            session = authSession(refreshToken = ""),
            users = mapOf(DEFAULT_USER_ID to authUser()),
            firebaseUser = null
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Unauthenticated, result)
        assertNull(fixture.tokenStorage.savedTokens)
        assertTrue(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
    }

    @Test
    fun `restoreSession with missing local user falls back to Firebase remote recovery`() = runTest {
        val fixture = repositoryFixture(
            session = authSession(),
            users = emptyMap(),
            firebaseUser = firebaseUser(isEmailVerified = true),
            remoteSession = remoteSession()
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Authenticated, result)
        assertTrue(fixture.firebase.getCurrentUserInfoCalled)
        assertTrue(fixture.firebase.getIdTokenCalled)
        assertTrue(fixture.remote.resolveFirebaseSessionCalled)
        assertEquals("remote-access-token", fixture.tokenStorage.savedTokens?.accessToken)
        assertEquals("remote-refresh-token", fixture.tokenStorage.savedTokens?.refreshToken)
        assertEquals("firebase-user-id", fixture.sessionLocalDataSource.savedSession?.userId)
    }

    @Test
    fun `restoreSession returns unauthenticated when Firebase lookup throws`() = runTest {
        val fixture = repositoryFixture(
            session = null,
            firebaseException = IllegalStateException("Firebase unavailable")
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Unauthenticated, result)
        assertTrue(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.firebase.getIdTokenCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
        assertNull(fixture.tokenStorage.savedTokens)
    }

    @Test
    fun `restoreSession with unverified Firebase fallback returns unauthenticated without backend call`() = runTest {
        val fixture = repositoryFixture(
            session = null,
            firebaseUser = firebaseUser(isEmailVerified = false)
        )

        val result = fixture.repository.restoreSession()

        assertEquals(SessionStatus.Unauthenticated, result)
        assertTrue(fixture.firebase.getCurrentUserInfoCalled)
        assertFalse(fixture.firebase.getIdTokenCalled)
        assertFalse(fixture.remote.resolveFirebaseSessionCalled)
        assertNull(fixture.tokenStorage.savedTokens)
    }

    private fun repositoryFixture(
        session: AuthSessionEntity? = null,
        users: Map<String, AuthUserEntity> = emptyMap(),
        firebaseUser: FirebaseUserModel? = null,
        firebaseException: Exception? = null,
        remoteSession: CreateUserResponseDto = remoteSession()
    ): RepositoryFixture {
        val sessionLocalDataSource = FakeSessionLocalDataSource(session = session)
        val authUserLocalDataSource = FakeAuthUserLocalDataSource(users = users)
        val remote = FakeAuthRemoteDataSource(response = remoteSession)
        val firebase = FakeFirebaseAuthDataSource(
            user = firebaseUser,
            getCurrentUserInfoException = firebaseException
        )
        val tokenStorage = FakeTokenStorage()

        return RepositoryFixture(
            repository = AuthRepositoryImpl(
                sessionLocalDataSource = sessionLocalDataSource,
                authUserLocalDataSource = authUserLocalDataSource,
                remote = remote,
                firebase = firebase,
                tokenStorage = tokenStorage
            ),
            sessionLocalDataSource = sessionLocalDataSource,
            firebase = firebase,
            remote = remote,
            tokenStorage = tokenStorage
        )
    }

    private data class RepositoryFixture(
        val repository: AuthRepositoryImpl,
        val sessionLocalDataSource: FakeSessionLocalDataSource,
        val firebase: FakeFirebaseAuthDataSource,
        val remote: FakeAuthRemoteDataSource,
        val tokenStorage: FakeTokenStorage
    )

    private class FakeSessionLocalDataSource(
        private var session: AuthSessionEntity?
    ) : SessionLocalDataSource {

        var savedSession: AuthSessionEntity? = null

        override suspend fun getSession(): AuthSessionEntity? = session

        override suspend fun hasActiveSession(): Boolean = session != null

        override suspend fun saveSession(session: AuthSessionEntity) {
            this.session = session
            savedSession = session
        }

        override suspend fun clearSession() {
            session = null
        }
    }

    private class FakeAuthUserLocalDataSource(
        users: Map<String, AuthUserEntity>
    ) : AuthUserLocalDataSource {

        private val usersById = users.toMutableMap()

        override suspend fun getUser(userId: String): AuthUserEntity? = usersById[userId]

        override suspend fun getUserByEmail(email: String): AuthUserEntity? =
            usersById.values.firstOrNull { it.email == email }

        override suspend fun saveUser(user: AuthUserEntity) {
            usersById[user.id] = user
        }

        override suspend fun clearUsers() {
            usersById.clear()
        }
    }

    private class FakeAuthRemoteDataSource(
        private val response: CreateUserResponseDto
    ) : AuthRemoteDataSource {

        var resolveFirebaseSessionCalled = false

        override suspend fun completeRegistration(
            request: CreateUserRequestDto,
            firebaseToken: String
        ): CreateUserResponseDto = response

        override suspend fun resolveFirebaseSession(firebaseToken: String): CreateUserResponseDto {
            resolveFirebaseSessionCalled = true
            return response
        }
    }

    private class FakeFirebaseAuthDataSource(
        private val user: FirebaseUserModel?,
        private val getCurrentUserInfoException: Exception?
    ) : FirebaseAuthDataSource {

        var getCurrentUserInfoCalled = false
        var getIdTokenCalled = false

        override suspend fun getCurrentUserInfo(): FirebaseUserModel? {
            getCurrentUserInfoCalled = true
            getCurrentUserInfoException?.let { throw it }
            return user
        }

        override suspend fun getIdToken(forceRefresh: Boolean): String {
            getIdTokenCalled = true
            return "firebase-id-token"
        }

        override suspend fun isEmailVerified(): Boolean = user?.isEmailVerified == true

        override suspend fun reloadUser() = Unit

        override suspend fun sendEmailVerification() = Unit

        override fun signOut() = Unit

        override suspend fun signInWithEmail(email: String, password: String): FirebaseUserModel =
            user ?: error("No user configured")

        override suspend fun signInWithGoogle(googleIdToken: String): FirebaseUserModel =
            user ?: error("No user configured")

        override suspend fun signUpWithEmail(email: String, password: String): FirebaseUserModel =
            user ?: error("No user configured")
    }

    private class FakeTokenStorage : TokenStorage {

        var savedTokens: StoredTokens? = null

        override suspend fun getTokens(): StoredTokens? = savedTokens

        override suspend fun saveTokens(tokens: StoredTokens) {
            savedTokens = tokens
        }

        override suspend fun clear() {
            savedTokens = null
        }
    }

    private companion object {
        const val DEFAULT_USER_ID = "local-user-id"

        fun authSession(
            userId: String = DEFAULT_USER_ID,
            accessToken: String = "local-access-token",
            refreshToken: String? = "local-refresh-token",
            isLoggedIn: Boolean = true
        ): AuthSessionEntity {
            return AuthSessionEntity(
                userId = userId,
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = 1_234L,
                isLoggedIn = isLoggedIn,
                lastUpdatedAt = 1_000L
            )
        }

        fun authUser(
            id: String = DEFAULT_USER_ID,
            profileCompleted: Boolean = true
        ): AuthUserEntity {
            return AuthUserEntity(
                id = id,
                name = "Test User",
                email = "test@example.com",
                photoUrl = null,
                profileCompleted = profileCompleted,
                updatedAt = 1_000L
            )
        }

        fun firebaseUser(isEmailVerified: Boolean): FirebaseUserModel {
            return FirebaseUserModel(
                uid = "firebase-user-id",
                email = "firebase@example.com",
                displayName = "Firebase User",
                isEmailVerified = isEmailVerified
            )
        }

        fun remoteSession(): CreateUserResponseDto {
            return CreateUserResponseDto(
                registration = CreateUserDataResponseDto(
                    tokenResponse = CreateUserTokenResponseDto(
                        accessToken = "remote-access-token",
                        refreshToken = "remote-refresh-token",
                        expiresIn = 9_999L
                    ),
                    user = CreateUserUserResponseDto(
                        userId = "backend-user-id",
                        idNumber = "1",
                        name = "Remote",
                        lastname = "User",
                        username = "remote@example.com",
                        nickname = "remote",
                        applicationName = "PEDPAL",
                        applicationCode = "PEDPAL"
                    ),
                    photoUrl = null
                )
            )
        }
    }
}
