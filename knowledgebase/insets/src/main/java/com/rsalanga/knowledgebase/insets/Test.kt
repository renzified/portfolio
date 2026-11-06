package com.rsalanga.knowledgebase.insets

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun Test() {
    Column {
        var counter by remember { mutableIntStateOf(0) }
        Text("$counter")
        Button(onClick = { counter++ }) { Text("increment") }
    }
}
