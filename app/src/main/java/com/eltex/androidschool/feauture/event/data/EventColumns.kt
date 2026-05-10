package com.eltex.androidschool.feauture.event.data

object EventColumns {
    const val TABLE_NAME = "events"
    const val COLUMN_ID = "id"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_AUTHOR = "author"
    const val COLUMN_PUBLISHED = "published"
    const val COLUMN_TYPE = "type"
    const val COLUMN_DATETIME = "datetime"
    const val COLUMN_LINK = "link"
    const val COLUMN_LIKED_BY_ME = "likedByMe"
    const val COLUMN_LIKES = "likes"
    const val COLUMN_PARTICIPATED_BY_ME = "participatedByMe"
    const val COLUMN_PARTICIPANTS = "participants"

    val ALL_COLUMNS = arrayOf(
        COLUMN_ID,
        COLUMN_CONTENT,
        COLUMN_AUTHOR,
        COLUMN_PUBLISHED,
        COLUMN_TYPE,
        COLUMN_DATETIME,
        COLUMN_LINK,
        COLUMN_LIKED_BY_ME,
        COLUMN_LIKES,
        COLUMN_PARTICIPATED_BY_ME,
        COLUMN_PARTICIPANTS
    )
}
