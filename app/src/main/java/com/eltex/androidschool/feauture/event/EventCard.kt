package com.eltex.androidschool.feauture.event

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltex.androidschool.R
import com.eltex.androidschool.ui.theme.AndroidTheme

@Composable
fun EventCard(
    event: EventUiState,
    modifier: Modifier = Modifier,
    onEvent: (EventAction) -> Unit = {}
) {
    Card(
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.author.take(1), color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1F)) {
                    Text(
                        text = event.author,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp
                    )
                    Text(
                        text = event.published,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    )
                }

                IconButton({ onEvent(EventAction.Menu(event.id)) }, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(Icons.Default.MoreVert, "More")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 16.dp)
            ) {
                Text(
                    text = if (event.type == EventType.OFFLINE) {
                        stringResource(R.string.offline_type)
                    } else stringResource(R.string.online_type),
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
                Text(
                    text = event.datetime,
                    fontSize = 14.sp
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, end = 16.dp),
                text = event.content,
                fontSize = 14.sp
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, end = 16.dp),
                text = event.link,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, end = 16.dp)
            ) {
                TextButton({ onEvent(EventAction.Like(event.id)) }) {
                    Icon(
                        if (event.likedByMe) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        "Like",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(event.likes.toString(), fontWeight = FontWeight.W500)
                }
                IconButton({ onEvent(EventAction.Share(event.id)) }, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(
                        Icons.Default.Share,
                        "Share",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton({ onEvent(EventAction.Participate(event.id)) }) {
                    Icon(
                        if (event.participatedByMe) painterResource(R.drawable.ic_participate)
                        else painterResource(R.drawable.ic_participate_outlined),
                        "Participate",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(event.participants.toString(), fontWeight = FontWeight.W500)
                }
            }
        }
    }
}

@Preview
@Composable
fun EventCardPreview() {
    AndroidTheme {
        EventCard(
            EventUiState(
                id = 1L,
                author = "Lydia Westervelt",
                published = "11.05.22 11:21",
                type = EventType.OFFLINE,
                datetime = "16.05.22 12:00",
                content = "Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                link = "https://m2.material.io/components/cards",
                likes = 2,
                participants = 2
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun EventCardPreviewDark() {
    AndroidTheme {
        EventCard(
            EventUiState(
                id = 1L,
                author = "Lydia Westervelt",
                published = "11.05.22 11:21",
                type = EventType.OFFLINE,
                datetime = "16.05.22 12:00",
                content = "Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                link = "https://m2.material.io/components/cards",
                likes = 2,
                participants = 2
            )
        )
    }
}