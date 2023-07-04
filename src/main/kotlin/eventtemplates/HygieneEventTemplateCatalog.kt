package eventtemplates

import models.Category
import models.EventTemplate
import models.WeightObject
import java.time.Duration

class HygieneEventTemplateCatalog : EventTemplateCatalog {

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val eventDuration = Duration.parse("PT5M")
        if (eventDuration <= maxDuration) {
            val weightsList: MutableList<WeightObject> = mutableListOf()
            weightsList.add(WeightObject(Category.HYGIENE, 5))
            return EventTemplate("Гигиена", 0, eventDuration, weightsList, "")
        }

        return null
    }

}