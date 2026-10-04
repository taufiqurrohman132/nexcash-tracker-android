package com.example.nexcashapplication.feature.notification.data

import com.example.nexcashapplication.core.datasource.local.dao.NotificationDao
import com.example.nexcashapplication.core.datasource.local.model.NotificationEntity
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationHistoryRepository
import javax.inject.Inject

/** [NotificationDao] ↔ domain. Tipe entitas (budget_alert/reminder) tetap di layer data. */
class NotificationHistoryRepositoryImpl @Inject constructor(
    private val dao: NotificationDao,
) : NotificationHistoryRepository {

    override suspend fun hasBeenSent(id: String): Boolean = dao.countById(id) > 0

    override suspend fun record(
        id: String,
        type: String,
        title: String,
        body: String,
        deepLink: String?,
        refId: String?,
    ) {
        dao.insertIfAbsent(
            NotificationEntity(
                id = id,
                type = type,
                title = title,
                body = body,
                deepLink = deepLink,
                refId = refId,
                isRead = false,
                sentAt = System.currentTimeMillis(),
            ),
        )
    }
}
