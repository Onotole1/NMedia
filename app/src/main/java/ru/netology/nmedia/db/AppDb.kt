package ru.netology.nmedia.db


import androidx.room.*
import ru.netology.nmedia.dao.Converters
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.entity.PostEntity

class Converters {
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
}

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

}
