package eventtemplates

import com.opencsv.CSVReader
import models.Category
import models.EventTemplate
import models.WeightObject
import java.io.FileReader
import java.time.Duration

class MandatoryEventTemplateCatalog {


    private val eventTemplateList = mutableListOf<EventTemplate>()

    init {
        readEventTemplatesFromCSV()
    }

    fun getEventTemplate(category: Category, maxDuration: Duration): EventTemplate? {
        var eventTemplate: EventTemplate? = null
        if (eventTemplateList[0].duration <= maxDuration && eventTemplateList[0].weights.any { it.category == category }) {
            eventTemplate = eventTemplateList[0]
            eventTemplateList.removeAt(0)
        }
        return eventTemplate
    }

    private fun readEventTemplatesFromCSV() {
        val filePath = "src/main/resources/week/mandatory-events.csv"
        val reader = CSVReader(FileReader(filePath))
        val lines = reader.readAll()
        reader.close()

        for (line in lines.drop(1)) { // Skipping the header row
            val name = line[0]
            val weight = line[1].toInt()
            val duration = Duration.parse("PT" + line[2] + "M")
            val weightsJson = line[3].split(";")
                .map {
                    val (category, weight) = it.split(":")
                    WeightObject(Category.valueOf(category), weight.toInt())
                }
            eventTemplateList.add(EventTemplate(name, weight, duration, weightsJson.toMutableList(), ""))
        }
    }
}