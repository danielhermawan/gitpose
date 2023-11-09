package com.coco.gitcompose.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.coco.gitcompose.data.GithubToken
import com.coco.gitcompose.datamodel.CurrentUser
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserSerializer @Inject constructor(): Serializer<CurrentUser>{
    override val defaultValue: CurrentUser = CurrentUser.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CurrentUser =
        try {
            CurrentUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: CurrentUser, output: OutputStream) {
        t.writeTo(output)
    }
}