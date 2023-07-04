package eventtemplates

import com.opencsv.CSVReader
import models.Category
import models.EventTemplate
import models.WeightObject
import java.io.FileReader
import java.time.Duration
import kotlin.random.Random

class StudyEventTemplateCatalog : EventTemplateCatalog {


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
                    if ("LANGUAGE" == eventTemplate.postProcessing) {
                        val languageEventTemplate = LanguageEventTemplateCatalog().getEventTemplate(maxDuration)
                        if (languageEventTemplate != null) {
                            languageEventTemplate.name = eventTemplate.name + ":" + languageEventTemplate.name
                        }
                        return languageEventTemplate
                    }
                    if ("CHESS" == eventTemplate.postProcessing) {
                        val chessEventTemplate = ChessEventTemplateCatalog().getEventTemplate(maxDuration)
                        if (chessEventTemplate != null) {
                            chessEventTemplate.name = eventTemplate.name + ":" + chessEventTemplate.name
                        }
                        return chessEventTemplate
                    }
                    return eventTemplate
                }
            }
        }

        return null
    }

    private fun readEventTemplatesFromCSV() {
        val filePath = "src/main/resources/eventTemplates/study.csv"
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
            val postProcessor = line[4]
            eventTemplateList.add(EventTemplate(name, weight, duration, weightsJson.toMutableList(), postProcessor))
        }
    }
}