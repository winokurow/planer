package eventtemplates

import models.Category
import models.EventTemplate
import models.WeightObject
import java.time.Duration

class NecessaryEventTemplateCatalog : EventTemplateCatalog {

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val eventDuration = Duration.parse("PT15M")
        if (eventDuration <= maxDuration) {
            val weightsList: MutableList<WeightObject> = mutableListOf()
            weightsList.add(WeightObject(Category.CLEAN, 5))
            weightsList.add(WeightObject(Category.FAMILY, 5))
            weightsList.add(WeightObject(Category.NECESSARY, 15))
            return EventTemplate("Важное", 0, eventDuration, weightsList.toMutableList(), "")
        }

        return null
    }

}