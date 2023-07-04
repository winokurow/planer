import com.opencsv.CSVWriter
import models.Category
import models.DayOfWeek
import models.EventTemplate
import models.WeightObject
import java.io.File
import java.io.FileWriter
import java.time.Duration
import java.time.LocalTime
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class Planer {

    private val days = Days()
    private var mandatoryEventMap: EnumMap<DayOfWeek, MutableList<Event>> = EnumMap(DayOfWeek::class.java)
    private var optionalEventMap: EnumMap<DayOfWeek, MutableList<Event>> = EnumMap(DayOfWeek::class.java)
    private val weights: EnumMap<Category, Int> = EnumMap(Category::class.java)
    private var weightsDifference: EnumMap<Category, Double> = EnumMap(Category::class.java)
    private var weightsSum by Delegates.notNull<Int>()
    private val dayStart = LocalTime.of(7, 0)
    private val dayEnd = LocalTime.of(21, 45)

    fun generatePlan() {
        mandatoryEventMap = days.readMandatoryEvents()
        optionalEventMap = days.readOptionalEvents()
        calculateSumOfWeightsByCategory()

        val entries = mandatoryEventMap.entries.toList()
        var index = 0

        while (index < entries.size) {
            val day = entries[index]
            val startTimeRange = dayStart
            val endTimeRange = dayEnd
            val gap = findGapBetweenEvents(day.value.sortedBy { it.beginTime }, startTimeRange, endTimeRange)
            if (gap != null) {
                val (gapStart, gapEnd) = gap
                println("Day ${day.key}")
                println("Gap found between $gapStart and $gapEnd")
                calculateWeightDifference()
                println(weightsDifference)
                val sortedCategories = weightsDifference.entries.sortedBy { it.value }.map { it.key }
                println(sortedCategories)
                var found = false
                for (category in sortedCategories) {
                    val event = findOptionalEvent(category, day.key, gapStart, gapEnd)
                    if (event != null) {
                        println("Event found")
                        println(event)
                        day.value.add(event)
                        addToWeights(event.weights)
                        found = true
                        break
                    }
                    val eventTemplate = findAndAddEvent(category, gapStart, gapEnd)
                    if (eventTemplate != null) {
                        val beginTime: LocalTime
                        val endTime: LocalTime
                        if ("WHOLE_DAY" == eventTemplate.postProcessing) {
                            mandatoryEventMap[day.key]?.clear()
                            calculateSumOfWeightsByCategory()
                            endTime = dayEnd
                            beginTime = dayStart
                        } else {
                            if (category == Category.LEISURE) {
                                endTime = gapEnd
                                beginTime = gapEnd.minus(eventTemplate.duration)
                            } else {
                                beginTime = gapStart
                                endTime = gapStart.plus(eventTemplate.duration)
                            }
                        }
                        val event = Event(eventTemplate.name, beginTime, endTime, eventTemplate.weights)
                        println(event)
                        mandatoryEventMap[day.key]?.add(event)
                        addToWeights(event.weights)
                        found = true
                        break
                    }
                }
                if (!found) {
                    println("ERROR Event not found")
                    exitProcess(1)
                }
            } else {
                println("No gap found between events")
                index++;
            }
        }
        outputResult()
    }


    private fun findGapBetweenEvents(
        events: List<Event>,
        beginTime: LocalTime,
        endTime: LocalTime
    ): Pair<LocalTime, LocalTime>? {
        var previousEndTime = beginTime

        for (event in events) {
            if (event.beginTime > previousEndTime) {
                return (previousEndTime to event.beginTime)
            }
            previousEndTime = event.endTime
        }

        if (previousEndTime < endTime) {
            return (previousEndTime to endTime)
        }


        return null
    }

    private fun calculateSumOfWeightsByCategory() {
        Category.values().forEach { category ->
            weights[category] = 0
        }
        for (day in mandatoryEventMap) {
            for (event in day.value) {
                for (weightObject in event.weights) {
                    val category = weightObject.category
                    val weight = weightObject.weight
                    weights[category] = weights.getOrDefault(category, 0) + weight
                }
            }
        }
        this.weightsSum = this.weights.values.sum()

    }

    private fun calculateWeightDifference() {
        for (weight in weights) {
            val weightsSumForCategory = weightsSum * weight.key.coefficient
            val weightsDifferenceForCategory = (weight.value - weightsSumForCategory) / weightsSumForCategory
            weightsDifference[weight.key] = weightsDifferenceForCategory
        }

    }

    private fun findAndAddEvent(category: Category, gapBeginTime: LocalTime, gapEndTime: LocalTime): EventTemplate? {

        return category.createImplementation().getEventTemplate(Duration.between(gapBeginTime, gapEndTime))
    }

    private fun findOptionalEvent(
        category: Category,
        day: DayOfWeek,
        gapBeginTime: LocalTime,
        gapEndTime: LocalTime
    ): Event? {
        val events = optionalEventMap[day]
        return events?.firstOrNull { it.beginTime >= gapBeginTime && it.endTime <= gapEndTime && it.weights.any { it.category == category } }

    }

    private fun addToWeights(additionalWeights: List<WeightObject>) {
        for (weight in additionalWeights) {
            weights[weight.category] = weights.getOrDefault(weight.category, 0) + weight.weight
        }
        this.weightsSum = this.weights.values.sum()
        calculateWeightDifference()
    }

    private fun outputResult() {
        for (dayOfWeek in enumValues<DayOfWeek>()) {
            println("Output Day $dayOfWeek")
            val fileName = "$dayOfWeek-result.csv"
            var events = mandatoryEventMap[dayOfWeek]?.sortedBy { it.beginTime }?.toMutableList()
            writeResultToCSV(fileName, events)
        }
    }

    private fun writeResultToCSV(fileName: String, events: MutableList<Event>?) {
        val filePath = "src/main/resources/result/$fileName"
        val file = File(filePath)
        val fileWriter = FileWriter(file, false)
        val csvWriter = CSVWriter(fileWriter)

        val headerRow = arrayOf("Name", "Begin Time", "End Time", "Weights")
        csvWriter.writeNext(headerRow)

        if (events != null) {
            for (event in events) {
                val name = event.name
                val beginTime = event.beginTime.toString()
                val endTime = event.endTime.toString()
                val weights = event.weights.joinToString(";") { it.category.toString() + ":" + it.weight.toString() }
                val dataRow = arrayOf(name, beginTime, endTime, weights)
                csvWriter.writeNext(dataRow)
            }
        }

        csvWriter.close()
    }
}
