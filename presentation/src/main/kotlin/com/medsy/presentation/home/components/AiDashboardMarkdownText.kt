package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Renders the constrained Markdown emitted by the dashboard summary prompt. */
@Composable
internal fun AiDashboardMarkdownText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val blocks = remember(text) { parseDashboardMarkdown(text) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        blocks.forEach { block ->
            when (block) {
                is DashboardMarkdownBlock.Heading -> Text(
                    text = block.text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )

                is DashboardMarkdownBlock.Paragraph -> Text(
                    text = block.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                )

                is DashboardMarkdownBlock.ListItem -> Row {
                    Text(
                        text = block.marker,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                    )
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private sealed interface DashboardMarkdownBlock {
    data class Heading(val text: AnnotatedString) : DashboardMarkdownBlock
    data class Paragraph(val text: AnnotatedString) : DashboardMarkdownBlock
    data class ListItem(val marker: String, val text: AnnotatedString) : DashboardMarkdownBlock
}

private val numberedItem = Regex("""^(\d{1,3})[.)]\s+(.*)$""")
private val bulletPrefixes = listOf("- ", "* ", "\u2022 ")

private fun parseDashboardMarkdown(text: String): List<DashboardMarkdownBlock> = buildList {
    text.lines().forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty()) return@forEach

        val bulletPrefix = bulletPrefixes.firstOrNull(line::startsWith)
        val numberedMatch = numberedItem.find(line)
        when {
            line.startsWith("#") -> add(
                DashboardMarkdownBlock.Heading(
                    boldDashboardText(line.trimStart('#').trim()),
                ),
            )

            bulletPrefix != null -> add(
                DashboardMarkdownBlock.ListItem(
                    marker = "\u2022  ",
                    text = boldDashboardText(line.removePrefix(bulletPrefix).trim()),
                ),
            )

            numberedMatch != null -> add(
                DashboardMarkdownBlock.ListItem(
                    marker = "${numberedMatch.groupValues[1]}.  ",
                    text = boldDashboardText(numberedMatch.groupValues[2]),
                ),
            )

            else -> add(DashboardMarkdownBlock.Paragraph(boldDashboardText(line)))
        }
    }
}

private fun boldDashboardText(text: String): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    while (cursor < text.length) {
        val open = text.indexOf("**", cursor)
        if (open < 0) {
            append(text.substring(cursor))
            break
        }
        val close = text.indexOf("**", open + 2)
        if (close < 0) {
            append(text.substring(cursor))
            break
        }
        append(text.substring(cursor, open))
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(text.substring(open + 2, close))
        pop()
        cursor = close + 2
    }
}
