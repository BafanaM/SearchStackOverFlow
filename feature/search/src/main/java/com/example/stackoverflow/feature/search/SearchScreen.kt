package com.example.stackoverflow.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.stackoverflow.core.network.model.Question
import com.example.stackoverflow.core.ui.theme.Dimens
import com.example.stackoverflow.core.ui.theme.StackOverflowBlue
import com.example.stackoverflow.core.ui.theme.StackOverflowGray
import com.example.stackoverflow.core.ui.theme.StackOverflowOrange
import com.example.stackoverflow.core.ui.theme.StackOverflowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    questions: List<Question>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onQuestionClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.search_menu))
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.stack_overflow_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(Dimens.AvatarSize),
                        )
                        Text(
                            text = stringResource(R.string.search_logo_stack),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = Dimens.SpacingXS),
                        )
                        Text(
                            text = stringResource(R.string.search_logo_overflow),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Surface(color = StackOverflowOrange) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.SpacingM),
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.SearchBarCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                )
            }

            QuestionList(questions = questions, onQuestionClick = onQuestionClick)
        }
    }
}

@Composable
private fun QuestionList(
    questions: List<Question>,
    onQuestionClick: (Long) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(questions, key = { it.id }) { question ->
            QuestionRow(question = question, onClick = { onQuestionClick(question.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun QuestionRow(
    question: Question,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Dimens.SpacingM),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = StackOverflowOrange,
            modifier = Modifier
                .padding(top = Dimens.SpacingXXS, end = Dimens.SpacingS)
                .size(Dimens.CheckIconSize),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.search_question_title_prefix, question.title),
                color = StackOverflowBlue,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = question.bodyHtml,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                color = StackOverflowGray,
                modifier = Modifier.padding(top = Dimens.SpacingXXS),
            )
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.search_asked_by_prefix, formatAskedDate(question.createdAtEpochSeconds)))
                    append(" ")
                    withStyle(SpanStyle(color = StackOverflowBlue)) {
                        append(question.ownerName)
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
                modifier = Modifier.padding(top = Dimens.SpacingS),
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = Dimens.SpacingS),
        ) {
            Text(
                text = stringResource(R.string.search_answers_count, question.answerCount),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
            Text(
                text = stringResource(R.string.search_votes_count, question.score),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
            Text(
                text = stringResource(R.string.search_views_count, question.viewCount),
                style = MaterialTheme.typography.labelSmall,
                color = StackOverflowGray,
            )
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = Dimens.SpacingXXS)
                .align(Alignment.CenterVertically),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val sampleQuestions = List(6) { index ->
        Question(
            id = index.toLong(),
            title = "Presenting modal in android 13 fullscreen",
            bodyHtml = "Testing testing. " +
                "Now it's not fullscreen by default and when I try to slide down, the app just dismiss the.....",
            tags = listOf("ios", "viewcontroller"),
            ownerName = "pascalbros",
            ownerAvatarUrl = null,
            ownerReputation = 2382,
            isAnswered = true,
            viewCount = 678,
            answerCount = 27,
            score = 430,
            createdAtEpochSeconds = 1_559_577_600L,
            link = null,
        )
    }
    StackOverflowTheme {
        SearchScreen(
            query = "",
            questions = sampleQuestions,
            onQueryChange = {},
            onSearch = {},
            onQuestionClick = {},
        )
    }
}
