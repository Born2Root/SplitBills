package org.weilbach.splitbills.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.weilbach.splitbills.R
import java.io.File
import java.io.FileWriter

fun startMailActivity(activity: Activity, groupName: String, appendix: String, content: String, subject: String, emails: Array<String>) {
    val file = getGroupFile(activity, groupName)
    val writer = FileWriter(file)
    writer.append(appendix)
    writer.flush()
    writer.close()
    val mimeType = "text/plain"
    val apkUri = FileProvider.getUriForFile(
            activity,
            "org.weilbach.splitbills.fileprovider",
            file)
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.setDataAndType(apkUri, mimeType)
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    emailIntent.putExtra(Intent.EXTRA_TEXT, content)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, emails)
    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    emailIntent.putExtra(Intent.EXTRA_STREAM, apkUri)

    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.share_group_intent)))
}

private fun getGroupFile(appContext: Context, groupName: String): File {
    val filename = groupNameToFileName(groupName)
    return File(appContext.filesDir, "$filename.sbgrp")
}

private fun groupNameToFileName(groupName: String): String {
    return groupName.replace(" ", "_")
}