package com.example.stackoverflow.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.model.Question
import com.example.stackoverflow.core.ui.theme.Dimens
import com.example.stackoverflow.core.ui.theme.StackOverflowOrange
import com.example.stackoverflow.core.ui.theme.StackOverflowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    question: Question,
    answers: List<Answer>,
    selectedSort: AnswerSort,
    onSortSelected: (AnswerSort) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        DetailContent(
            question = question,
            answers = answers,
            selectedSort = selectedSort,
            onSortSelected = onSortSelected,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun DetailContent(
    question: Question,
    answers: List<Answer>,
    selectedSort: AnswerSort,
    onSortSelected: (AnswerSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = Dimens.SpacingL)) {
        item {
            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Dimens.SpacingS),
            )
            Text(
                text = stringResource(R.string.detail_viewed_times, question.viewCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpacingXS),
            )
            Text(
                text = question.bodyHtml,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Dimens.SpacingM),
            )

            if (question.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = Dimens.SpacingM),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
                ) {
                    question.tags.forEach { tag -> TagChip(tag) }
                }
            }

            OwnerRow(
                name = question.ownerName,
                avatarUrl = question.ownerAvatarUrl,
                reputation = question.ownerReputation,
                modifier = Modifier.padding(top = Dimens.SpacingL, bottom = Dimens.SpacingS),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpacingM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.detail_answers_count, answers.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    AnswerSort.entries.forEach { sort ->
                        TextButton(onClick = { onSortSelected(sort) }) {
                            Text(
                                text = stringResource(sort.labelRes()),
                                fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal,
                                color = if (sort == selectedSort) {
                                    StackOverflowOrange
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                }
            }
        }

        items(answers, key = { it.id }) { answer ->
            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpacingM))
            AnswerItem(answer)
        }
    }
}

@Composable
private fun AnswerItem(answer: Answer) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = Dimens.SpacingL),
        ) {
            Text(text = "${answer.score}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.detail_answer_votes_label), style = MaterialTheme.typography.labelSmall)
            if (answer.isAccepted) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.detail_accepted_answer),
                    tint = Color(0xFF5EBA7D),
                    modifier = Modifier.padding(top = Dimens.SpacingXS),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = answer.bodyHtml, style = MaterialTheme.typography.bodyMedium)
            OwnerRow(
                name = answer.ownerName,
                avatarUrl = answer.ownerAvatarUrl,
                reputation = answer.ownerReputation,
                modifier = Modifier.padding(top = Dimens.SpacingM, bottom = Dimens.SpacingXS),
            )
        }
    }
}

@Composable
private fun OwnerRow(
    name: String,
    avatarUrl: String?,
    reputation: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.AvatarSize)
                    .clip(CircleShape),
            )
        }
        Column(modifier = Modifier.padding(start = Dimens.SpacingS)) {
            Text(text = name, style = MaterialTheme.typography.bodySmall)
            Text(text = "$reputation", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun AnswerSort.labelRes(): Int = when (this) {
    AnswerSort.ACTIVE -> R.string.sort_active
    AnswerSort.OLDEST -> R.string.sort_oldest
    AnswerSort.VOTES -> R.string.sort_votes
}

@Composable
private fun TagChip(tag: String) {
    Text(
        text = tag,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.TagCornerRadius))
            .background(StackOverflowOrange)
            .padding(horizontal = Dimens.SpacingS, vertical = Dimens.SpacingXS),
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    val sampleQuestion = Question(
        id = 1L,
        title = "Presenting",
        bodyHtml = "Body body body...",
        tags = listOf("android", "viewcontroller", "modalviewcontroller"),
        ownerName = "Lewis Dholpin",
        ownerAvatarUrl = null,
        ownerReputation = 2382,
        isAnswered = true,
        viewCount = 678,
        answerCount = 27,
        score = 430,
        createdAtEpochSeconds = 1_559_577_600L,
        link = null,
    )
    val sampleAnswers = listOf(
        Answer(
            id = 1L,
            questionId = 1L,
            bodyHtml = "I add an information that could be useful for someone...",
            ownerName = "Lewis Dholpin",
            ownerAvatarUrl = null,
            ownerReputation = 2382,
            isAccepted = true,
            score = 528,
            createdAtEpochSeconds = 1_559_577_600L,
        ),
    )
    StackOverflowTheme {
        DetailScreen(
            question = sampleQuestion,
            answers = sampleAnswers,
            selectedSort = AnswerSort.VOTES,
            onSortSelected = {},
            onBackClick = {},
        )
    }
}
