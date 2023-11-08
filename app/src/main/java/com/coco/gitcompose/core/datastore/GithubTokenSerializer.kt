package com.coco.gitcompose.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.coco.gitcompose.data.GithubToken
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubTokenSerializer @Inject constructor() : Serializer<GithubToken> {
    override val defaultValue: GithubToken = GithubToken.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): GithubToken =
        try {
            // readFrom is already called on the data store background thread
            GithubToken.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: GithubToken, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}