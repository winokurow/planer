package eventtemplates

import com.opencsv.CSVReader
import models.Category
import models.EventTemplate
import models.WeightObject
import java.io.FileReader
import java.time.Duration
import kotlin.random.Random

class LanguageEventTemplateCatalog : EventTemplateCatalog {


    private val eventTemplateList = mutableListOf<EventTemplate>()

    init {
        readEventTemplatesFromCSV()
    }

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val filteredEventTemplateList = eventTemplateList.filter { eventTemplate ->
            eventTemplate.duration <= maxDuration
        }
        val totalWeight = filteredEventTemplateList.sumOf { it.weight }
        if (totalWeight > 0) {
            val randomNumber = Random.nextInt(totalWeight)

            var cumulativeWeight = 0
            for (eventTemplate in filteredEventTemplateList) {
                cumulativeWeight += eventTemplate.weight
                if (randomNumber < cumulativeWeight) {
                    println(eventTemplate)
                    return eventTemplate
                }
            }
        }
        return null
    }

    private fun readEventTemplatesFromCSV() {
        val filePath = "src/main/resources/eventTemplates/language.csv"
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