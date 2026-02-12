package com.jxd20072.automationapp.core.executor

private const val TAG = "APP_Executor/Variables"

//fun readVariable(variable: VariableDef.TEXT): String {
//	return variable.value
//}
//
//fun readVariable(variable: VariableDef.NUMBER): Float? {
//	try {
//		return variable.value.toFloat()
//	} catch (e: Exception) {
//		Log.e(TAG, "Could not read variable ${variable.name} as NUMBER")
//		return null
//	}
//}
//
//fun readVariable(variable: VariableDef.BOOL): Boolean? {
//	try {
//		return variable.value.toBooleanStrict()
//	} catch (e: Exception) {
//		Log.e(TAG, "Could not read variable ${variable.name} as BOOL")
//		return null
//	}
//}
//
//fun readVariable(
//	variable: VariableDef.VARIABLE,
//	context: ExecutionFlowContext
//): VariableDef? {
//	val variableFound = context.getVariable(variable.value)
//	if (variableFound == null) {
//		Log.e(TAG, "Variable ${variable.name} not found")
//	}
//	return variableFound
//}
//
//fun readVariable(variable: VariableDef, context: ExecutionFlowContext): Any? {
//	return when (variable.variableType) {
//		VariableDefType.TEXT -> readVariable(variable as VariableDef.TEXT)
//		VariableDefType.NUMBER -> readVariable(variable as VariableDef.NUMBER)
//		VariableDefType.BOOL -> readVariable(variable as VariableDef.BOOL)
//		VariableDefType.VARIABLE -> readVariable(variable as VariableDef.VARIABLE, context)
//	}
//}
//
//fun readVariableAsText(variable: VariableDef): String {
//	if (variable !is VariableDef.TEXT) {
//		throw Exception(
//			"Attempt to read variable ${variable.name} (${variable.variableType}) as TEXT"
//		)
//	}
//	return readVariable(variable)
//}
//
//fun readVariableAsNumber(variable: VariableDef): Float? {
//	if (variable !is VariableDef.NUMBER) {
//		throw Exception(
//			"Attempt to read variable ${variable.name} (${variable.variableType}) as NUMBER"
//		)
//	}
//	return readVariable(variable)
//}
//
//fun readVariableAsBool(variable: VariableDef): Boolean? {
//	if (variable !is VariableDef.BOOL) {
//		throw Exception(
//			"Attempt to read variable ${variable.name} (${variable.variableType}) as BOOL"
//		)
//	}
//	return readVariable(variable)
//}
//
//fun readVariableAsVariable(
//	variable: VariableDef,
//	context: ExecutionFlowContext
//): VariableDef? {
//	if (variable !is VariableDef.VARIABLE) {
//		throw Exception(
//			"Attempt to read variable ${variable.name} (${variable.variableType}) as BOOL"
//		)
//	}
//	return readVariable(variable, context)
//}