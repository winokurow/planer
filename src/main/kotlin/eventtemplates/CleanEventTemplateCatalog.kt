package eventtemplates

import com.opencsv.CSVReader
import models.Category
import models.EventTemplate
import models.WeightObject
import java.io.FileReader
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class CleanEventTemplateCatalog : EventTemplateCatalog {


    private val eventTemplateList = mutableListOf<EventTemplate>()

    init {
        readEventTemplatesFromCSV()
    }

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val filteredEventTemplateList = eventTemplateList.filter { eventTemplate ->
            eventTemplate.duration <= maxDuration
        }
        val aggregatedEventTemplate = EventTemplate("", 0, Duration.ZERO, mutableListOf<WeightObject>(), "")
        val totalWeight = filteredEventTemplateList.sumOf { it.weight }
        if (totalWeight > 0) {

            while (aggregatedEventTemplate.duration <= Duration.parse("PT5M")) {

                val randomNumber = Random.nextInt(totalWeight)
                var cumulativeWeight = 0
                for (eventTemplate in filteredEventTemplateList) {
                    cumulativeWeight += eventTemplate.weight
                    if (randomNumber < cumulativeWeight) {
                        println(eventTemplate)
                        aggregatedEventTemplate.name += eventTemplate.name + "-----------"
                        aggregatedEventTemplate.duration += eventTemplate.duration
                        break
                    }
                }
            }
        }
        if (aggregatedEventTemplate.duration == Duration.ZERO) {
            return null
        } else {
            aggregatedEventTemplate.duration = roundToNearest5Minutes(aggregatedEventTemplate.duration)
            val weightObject = WeightObject(Category.CLEAN, aggregatedEventTemplate.duration.toMinutes().toInt())
            aggregatedEventTemplate.weights.add(weightObject)
            return aggregatedEventTemplate;
        }
    }

    private fun readEventTemplatesFromCSV() {
        val filePath = "src/main/resources/eventTemplates/clean.csv"
        val reader = CSVReader(FileReader(filePath))
        val lines = reader.readAll()
        reader.close()

        for (line in lines.drop(1)) { // Skipping the header row
            val name = line[4] + ' ' + line[5] + line[0]
            val weight = line[1].toInt()
            val duration = Duration.parse("PT" + line[2] + "M")
            eventTemplateList.add(EventTemplate(name, weight, duration, mutableListOf(), ""))
        }
    }

    fun roundToNearest5Minutes(duration: Duration): Duration {
        val roundedMinutes = duration.toMinutes() / 5 * 5
        return Duration.of(roundedMinutes, ChronoUnit.MINUTES)
    }
}