package dev.eknath.markdownparser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import co.touchlab.kermit.Logger

// Enum for markdown tokens
enum class MarkDownTokens(val token: String, val pattern: Regex, val needsClosingTag: Boolean) {
    BOLD(token = "*", pattern = "\\*(.*?)\\*".toRegex(), needsClosingTag = true),
    ITALIC(token = "_", pattern = "_(.*?)_".toRegex(), needsClosingTag = true),
    STROKE_THROUGH(token = "~", pattern = "~(.*?)~".toRegex(), needsClosingTag = true),
    UNDER_LINE(token = "__", pattern = "_(.*?)_".toRegex(), needsClosingTag = true),
    H1(token = "#", pattern = "# (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false),
    H2(token = "##", pattern = "## (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false),
    H3(token = "###", pattern = "### (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false),
    H4(token = "####", pattern = "#### (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false),
    H5(token = "#####", pattern = "##### (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false),
    H6(token = "######", pattern = "###### (.*?)$".toRegex(RegexOption.MULTILINE), needsClosingTag = false)
}

// Function to create an annotated string based on markdown tokens
fun parseMarkdownToAnnotatedString(input: String): AnnotatedString {

    if (input.isEmpty() || input.nextTokenAndIndexIfExists() == null) {
        Logger.d("Content is empty or tokens not found")
        return buildAnnotatedString { append(input) }
    }

    Logger.d("Content is not empty or tokens are available")

    val openTokensMap = mutableMapOf<MarkDownTokens, Int>()
    var stringRequiresParsing = input
    var startIndex = 0
    var trimEndIndex = 0

    val annotatedString = buildAnnotatedString {
        var tokenAndIndex = stringRequiresParsing.nextTokenAndIndexIfExists()

        while (tokenAndIndex != null) {
            val (token, tokenIndex) = tokenAndIndex
            Logger.d(
                "token found: $token at index: $tokenIndex isAvailableAtOpenTokenMap:${
                    openTokensMap.containsKey(
                        token
                    )
                }"
            )

            if (openTokensMap.containsKey(token)) {
                Logger.d("a close token found: $token")
                //close tag
                trimEndIndex = tokenIndex
                println("close MD: ${token}")
                openTokensMap.minus(token)
                if (token == MarkDownTokens.BOLD)
                    pushStyle(style = SpanStyle(fontWeight = FontWeight.Normal))
            } else {
                Logger.d("OpenTokenFound: $token")
                //open tag
                openTokensMap.plus(token to tokenIndex)
                trimEndIndex = tokenIndex
                val textToAppend = stringRequiresParsing.substring(startIndex = startIndex, endIndex = trimEndIndex)
                append(textToAppend)

                startIndex = trimEndIndex.plus(token.token.length)
                stringRequiresParsing = if(stringRequiresParsing.lastIndex > startIndex)stringRequiresParsing.substring(startIndex = startIndex) else ""
                Logger.d("updatedThe stringRequiresParsing :$stringRequiresParsing")
                startIndex = 0
                trimEndIndex = 0

                if(token  == MarkDownTokens.BOLD)
                    pushStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
            }
            tokenAndIndex = stringRequiresParsing.nextTokenAndIndexIfExists()
        }
        Logger.d("Appending the balance string that has no token $stringRequiresParsing")
        append(stringRequiresParsing)
    }
    return annotatedString
}

private fun String.nextTokenAndIndexIfExists(): Pair<MarkDownTokens, Int>? {
    Logger.d("searchingToken")
    val tokens = MarkDownTokens.entries
    // We iterate over the tokens and find the token with the smallest valid index
    Logger.d("inputForToken: $this")
    val nextToken =
        tokens.minByOrNull { this.indexOf(it.token).takeIf { idx -> idx != -1 } ?: Int.MAX_VALUE }
    val nextTokenIndex = nextToken?.let { this.indexOf(it.token) } ?: -1

    Logger.d("outputForTokenLogic: token:$nextToken  index:$nextTokenIndex $this")
    // If there is no valid token or index, return null
    return if (nextToken == null || nextTokenIndex == -1) null else nextToken to nextTokenIndex
}

