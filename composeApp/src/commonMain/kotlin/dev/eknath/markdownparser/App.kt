package dev.eknath.markdownparser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val userInputValue = remember { mutableStateOf(TextFieldValue()) }
        val styledText = remember { derivedStateOf { parseMarkdownToAnnotatedString(userInputValue.value.text) } }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {

            MarkDownToolBar(onClick = {
                val newTextValue = userInputValue.value.text + it.token
                userInputValue.value = userInputValue.value.copy(
                    text = newTextValue,
                    selection = TextRange(newTextValue.length, newTextValue.length),
                    composition = TextRange(newTextValue.length, newTextValue.length)
                )
            })

            Row(modifier = Modifier.fillMaxWidth().heightIn(500.dp)) {
                TextField(
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                    value = userInputValue.value,
                    onValueChange = {
                        userInputValue.value = it
                    }
                )
                Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
                Text(
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                    text = styledText.value
                )
            }
        }
    }
}

@Composable
private fun MarkDownToolBar(modifier: Modifier = Modifier, onClick: (MarkDownTokens) -> Unit) {
    Row(modifier = Modifier.heightIn(30.dp)) {
        MarkDownTokens.entries.forEach { markDownItem ->
            Button(
                modifier = Modifier.wrapContentWidth(),
                onClick = { onClick(markDownItem) }) {
                Text(markDownItem.name, maxLines = 1)
            }
        }
    }
}

