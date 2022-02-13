package com.overman.basicapp

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : MultiDexApplication()  {
}