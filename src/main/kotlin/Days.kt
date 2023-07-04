import com.opencsv.CSVReader
import models.Category
import models.DayOfWeek
import models.WeightObject
import java.io.FileReader
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class Days {

    fun readMandatoryEvents(): EnumMap<DayOfWeek, MutableList<Event>> {
        val days: EnumMap<DayOfWeek, MutableList<Event>> = EnumMap(DayOfWeek::class.java)
        for (dayOfWeek in enumValues<DayOfWeek>()) {
            val fileName = "$dayOfWeek-mandatory.csv"
            val events = readEventsFromCSV(fileName)
            days[dayOfWeek] = events
        }
        return days
    }

    fun readOptionalEvents(): EnumMap<DayOfWeek, MutableList<Event>> {
        val days: EnumMap<DayOfWeek, MutableList<Event>> = EnumMap(DayOfWeek::class.java)
        for (dayOfWeek in enumValues<DayOfWeek>()) {
            val fileName = "$dayOfWeek-optional.csv"
            val events = readEventsFromCSV(fileName)
            days[dayOfWeek] = events
        }
        return days
    }

    private fun readEventsFromCSV(fileName: String): MutableList<Event> {
        val events = mutableListOf<Event>()
        val filePath = "src/main/resources/week/$fileName"
        val reader = CSVReader(FileReader(filePath))
        val lines = reader.readAll()
        reader.close()

        for (line in lines.drop(1)) { // Skipping the header row
            val name = line[0]
            val beginTime = LocalTime.parse(line[1], DateTimeFormatter.ISO_LOCAL_TIME)
            val endTime = LocalTime.parse(line[2], DateTimeFormatter.ISO_LOCAL_TIME)
            val weightsJson = line[3].split(";")
                .map {
                    val (category, weight) = it.split(":")
                    WeightObject(Category.valueOf(category), weight.toInt())
                }
            events.add(Event(name, beginTime, endTime, weightsJson))
        }
        return events
    }
}