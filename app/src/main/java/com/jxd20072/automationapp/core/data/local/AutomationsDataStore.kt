package com.jxd20072.automationapp.core.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AutomationsDataSerializer @Inject constructor() : Serializer<AutomationsDataMap> {
	override val defaultValue: AutomationsDataMap = AutomationsDataMap.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): AutomationsDataMap {
		try {
			return AutomationsDataMap.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(t: AutomationsDataMap, output: OutputStream) {
		t.writeTo(output)
	}
}