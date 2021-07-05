package com.example.composetest.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.composetest.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(version = 1, exportSchema = false, entities = [Note::class])
abstract class NotesDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

}

@Module
@InstallIn(ActivityRetainedComponent::class)
object NotesDatabaseModule {

    @Provides
    fun provideNotesDatabase(
        @ApplicationContext applicationContext: Context
    ) : NotesDatabase {
        val databaseName = applicationContext.getString(R.string.app_db_name)
        return Room.databaseBuilder(applicationContext, NotesDatabase::class.java, databaseName)
            .build()
    }

}