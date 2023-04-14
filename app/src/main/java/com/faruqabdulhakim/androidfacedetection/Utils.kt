package com.faruqabdulhakim.androidfacedetection

import android.app.Application
import java.io.File

fun getOutputDirectory(application: Application): File {
    val outputDirectory = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    return if (outputDirectory != null && outputDirectory.exists()) outputDirectory
    else application.filesDir
}