package dev.eknath.markdownparser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import co.touchlab.kermit.Logger

// Enum for markdown tokens
enum class MarkDownTokens(
    val token: String,
    val pattern: Regex,
    val needsClosingTag: Boolean,
    val spanStyle: SpanStyle? = null
) {
    BOLD("*", "\\*(.*?)\\*".toRegex(), true, SpanStyle(fontWeight = FontWeight.Bold)),
    ITALIC(
        "_",
        "_(.*?)_".toRegex(),
        true,
        SpanStyle(fontWeight = FontWeight.Normal)
    ), // You can change SpanStyle here for Italic
    STROKE_THROUGH(
        "~",
        "~(.*?)~".toRegex(),
        true,
        SpanStyle(textDecoration = TextDecoration.LineThrough)
    ),
    UNDER_LINE(
        "__",
        "__(.*?)__".toRegex(),
        true,
        SpanStyle(textDecoration = TextDecoration.Underline)
    ),
    H1("#", "^# (.*?)$".toRegex(RegexOption.MULTILINE), false),
    H2("##", "^## (.*?)$".toRegex(RegexOption.MULTILINE), false),
    H3("###", "^### (.*?)$".toRegex(RegexOption.MULTILINE), false),
    H4("####", "^#### (.*?)$".toRegex(RegexOption.MULTILINE), false),
    H5("#####", "^##### (.*?)$".toRegex(RegexOption.MULTILINE), false),
    H6("######", "^###### (.*?)$".toRegex(RegexOption.MULTILINE), false)
}

// Function to create an annotated string based on markdown tokens
fun parseMarkdownToAnnotatedString(input: String): AnnotatedString {

    if (input.isEmpty()) {
        Logger.d("Content is empty")
        return buildAnnotatedString { append(input) }
    }

    Logger.d("Parsing content")

    val openTokensMap = mutableMapOf<MarkDownTokens, Int>()
    var currentText = input
    var startIndex = 0

    return buildAnnotatedString {
        var tokenAndIndex = currentText.nextTokenAndIndexIfExists()

        while (tokenAndIndex != null) {
            val (token, tokenIndex) = tokenAndIndex
            Logger.d("Token found: ${token.token} at index $tokenIndex")

            if (openTokensMap.containsKey(token)) {
                Logger.d("Close Token Logic")
                // Closing token
                val openingIndex = openTokensMap.remove(token) ?: 0
                append(currentText.substring(0, tokenIndex))
                pop()

            } else {
                Logger.d("Open Token Logic")
                // Opening token
                openTokensMap[token] = tokenIndex
                append(currentText.substring(0, tokenIndex))

                if (token.spanStyle != null)
                    pushStyle(token.spanStyle)
            }

            // Update string and indices
            currentText = currentText.substring(tokenIndex + token.token.length)
            tokenAndIndex = currentText.nextTokenAndIndexIfExists()
        }

        // Append remaining text
        Logger.d("Appending remaining text")
        append(currentText)
    }
}

// Helper function to find the next token and its index
private fun String.nextTokenAndIndexIfExists(): Pair<MarkDownTokens, Int>? {
    val tokens = MarkDownTokens.entries.mapNotNull { token ->
        val index = this.indexOf(token.token)
        if (index != -1) token to index else null
    }
    return tokens.minByOrNull { it.second }
}
