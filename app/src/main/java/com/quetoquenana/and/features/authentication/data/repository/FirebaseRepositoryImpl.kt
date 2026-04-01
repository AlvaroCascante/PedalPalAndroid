package com.quetoquenana.and.features.authentication.data.repository

import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.repository.FirebaseRepository
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAuthDataSource,
) : FirebaseRepository
{

    override suspend fun getCurrentUserInfo(): FirebaseUserModel? {
        return firebase.getCurrentUserInfo()
    }

    override suspend fun getFirebaseIdToken(forceRefresh: Boolean): String {
        return firebase.getIdToken(forceRefresh)
    }
}