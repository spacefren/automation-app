package com.jxd20072.automationapp.utils

import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes

fun getNodeData(nodeDef: NodeDef): NodeData? {
    return when (nodeDef.nodeType) {
        NodeType.ACTION -> actionNodes[nodeDef.name]?.data
        NodeType.TRIGGER -> triggerNodes[nodeDef.name]?.data
        else -> null
    }
}