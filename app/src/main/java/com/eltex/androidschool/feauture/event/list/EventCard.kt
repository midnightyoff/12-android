package com.eltex.androidschool.feauture.event.list

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.event.domain.EventType
import com.eltex.androidschool.ui.theme.AndroidTheme

@Composable
fun EventCard(
    event: EventUiModel,
    modifier: Modifier = Modifier,
    onEvent: (EventMessage) -> Unit = {},
) {
    val likeIconColor by animateColorAsState(
        targetValue = if (event.likedByMe) Color.Red else MaterialTheme.colorScheme.primary,
    )

    val likeScale by animateFloatAsState(
        targetValue = if (event.likedByMe) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
    )

    val participateRotation by animateFloatAsState(
        targetValue = if (event.participatedByMe) 360f else 0f,
        animationSpec = tween(durationMillis = 500),
    )

    var expanded by remember { mutableStateOf(false) }

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

                IconButton({
                    expanded = true
                }) {
                    Icon(Icons.Default.MoreVert, null)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.event_menu_delete)) },
                            onClick = {
                                onEvent(EventMessage.Delete(event.id))
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.event_menu_edit)) },
                            onClick = {
                                onEvent(EventMessage.EditEvent(event))
                                expanded = false
                            }
                        )
                    }
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
                TextButton({ onEvent(EventMessage.Like(event.id)) }, modifier = Modifier.scale(likeScale)) {
                    Icon(
                        if (event.likedByMe) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        "Like",
                        tint = likeIconColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(event.likes.toString(), fontWeight = FontWeight.W500)
                }
                IconButton({ onEvent(EventMessage.Share(event.id)) }, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(
                        Icons.Default.Share,
                        "Share",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton({ onEvent(EventMessage.Participate(event.id)) }) {
                    Icon(
                        if (event.participatedByMe) painterResource(R.drawable.ic_participate)
                        else painterResource(R.drawable.ic_participate_outlined),
                        "Participate",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.rotate(participateRotation)
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
            EventUiModel(
                id = 1L,
                author = "Lydia Westervelt",
                published = "01.01.25 12:00",
                type = EventType.OFFLINE,
                datetime = "01.01.25 14:00",
                content = "Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                link = "https://m2.material.io/components/cards",
                likes = 2,
                participants = 2
            ),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun EventCardPreviewDark() {
    AndroidTheme {
        EventCard(
            EventUiModel(
                id = 1L,
                author = "Lydia Westervelt",
                published = "01.01.25 12:00",
                type = EventType.OFFLINE,
                datetime = "01.01.25 14:00",
                content = "Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                link = "https://m2.material.io/components/cards",
                likes = 2,
                participants = 2
            ),
        )
    }
}